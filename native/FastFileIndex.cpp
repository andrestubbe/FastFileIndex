/**
 * @file FastFileIndex.cpp
 * @brief FastFileIndex native implementation
 *
 * @details Implements native methods for FastFileIndex - Creates a compact, append-only, mmap-friendly binary index of all files.
 * Part of the FastJava file search engine trilogy (FastFileIndex, FastFileSearch, FastFileWatch).
 *
 * @author FastJava Team
 * @version v1.0.0
 * @copyright MIT License
 */

#include <jni.h>
#include <windows.h>
#include <filesystem>
#include <fstream>
#include <chrono>
#include <cstring>
#include <vector>
#include <string>
#include <mutex>
#include <atomic>

using namespace std;

// ============================================================================
// Data Structures
// ============================================================================

struct FileEntryHeader {
    uint64_t id;
    uint64_t parentId;
    uint64_t size;
    uint64_t modified;
    uint32_t type;
    uint32_t pathOffset;
    uint32_t pathLen;
};

struct FileEntry {
    uint64_t id;
    uint64_t parentId;
    uint64_t size;
    uint64_t modified;
    uint32_t type;
    string path;
};

// ============================================================================
// Global State
// ============================================================================

static vector<FileEntry> g_entries;
static mutex g_entriesMutex;
static char* g_mappedData = nullptr;
static HANDLE g_hMap = nullptr;
static HANDLE g_hFile = nullptr;
static size_t g_fileSize = 0;

// ============================================================================
// Helper Functions
// ============================================================================

static inline uint64_t fnv1a(const char* data, size_t len) {
    uint64_t h = 1469598103934665603ULL;
    for (size_t i = 0; i < len; i++) {
        h ^= (uint64_t)(unsigned char)data[i];
        h *= 1099511628211ULL;
    }
    return h;
}

static uint64_t hash64(const string& s) {
    return fnv1a(s.data(), s.size());
}

static uint32_t detectType(const string& path) {
    auto ext = filesystem::path(path).extension().string();
    for (auto& c : ext) c = tolower(c);

    if (ext == ".jpg" || ext == ".jpeg" || ext == ".png" || ext == ".gif" || ext == ".bmp" || ext == ".webp") return 1;
    if (ext == ".pdf") return 2;
    if (ext == ".txt" || ext == ".md") return 3;
    if (ext == ".cpp" || ext == ".h" || ext == ".hpp" || ext == ".c" || ext == ".java" || ext == ".py" || ext == ".js") return 4;
    if (ext == ".mp4" || ext == ".avi" || ext == ".mkv" || ext == ".mov") return 5;
    if (ext == ".mp3" || ext == ".wav" || ext == ".flac") return 6;
    if (ext == ".zip" || ext == ".rar" || ext == ".7z") return 7;
    return 0;
}

static uint64_t toUnixTS(const filesystem::file_time_type& ft) {
    auto sctp = chrono::time_point_cast<chrono::system_clock::duration>(
        ft - filesystem::file_time_type::clock::now()
        + chrono::system_clock::now()
    );
    return chrono::duration_cast<chrono::seconds>(sctp.time_since_epoch()).count();
}

// ============================================================================
// ============================================================================
// JNI Implementation
// ============================================================================

extern "C" {

/**
 * @brief Build file index by scanning root directories
 */
JNIEXPORT void JNICALL Java_fastfileindex_FastFileIndex_build(
    JNIEnv* env,
    jclass clazz,
    jobjectArray jroots) {

    lock_guard<mutex> lock(g_entriesMutex);
    g_entries.clear();
    g_entries.reserve(500000);

    jsize rootCount = env->GetArrayLength(jroots);
    vector<string> roots;

    for (jsize i = 0; i < rootCount; i++) {
        jstring jroot = (jstring)env->GetObjectArrayElement(jroots, i);
        const char* root = env->GetStringUTFChars(jroot, nullptr);
        roots.push_back(string(root));
        env->ReleaseStringUTFChars(jroot, root);
    }

    for (auto& root : roots) {
        try {
            for (auto& p : filesystem::recursive_directory_iterator(root, filesystem::directory_options::skip_permission_denied)) {
                if (!p.is_regular_file()) continue;

                FileEntry e;
                e.path = p.path().string();
                e.id = hash64(e.path);
                e.parentId = hash64(p.path().parent_path().string());
                e.size = p.file_size();
                e.modified = toUnixTS(p.last_write_time());
                e.type = detectType(e.path);

                g_entries.push_back(std::move(e));
            }
        } catch (const exception& ex) {
            // Skip directories we can't access
        }
    }
}

/**
 * @brief Build file index with progress callback
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @param jroots Array of root directory paths
 * @param jcallback Progress callback object
 */
JNIEXPORT void JNICALL Java_fastfileindex_FastFileIndex_buildWithProgress(
    JNIEnv* env,
    jclass clazz,
    jobjectArray jroots,
    jobject jcallback) {

    lock_guard<mutex> lock(g_entriesMutex);
    g_entries.clear();
    g_entries.reserve(500000);

    jsize rootCount = env->GetArrayLength(jroots);
    vector<string> roots;

    for (jsize i = 0; i < rootCount; i++) {
        jstring jroot = (jstring)env->GetObjectArrayElement(jroots, i);
        const char* root = env->GetStringUTFChars(jroot, nullptr);
        roots.push_back(string(root));
        env->ReleaseStringUTFChars(jroot, root);
    }

    // Get callback class and method
    jclass callbackClass = env->GetObjectClass(jcallback);
    jmethodID onProgressMethod = env->GetMethodID(callbackClass, "onProgress", "(JJLjava/lang/String;)V");

    long totalFiles = 0; // Unknown, will be 0
    long currentFile = 0;
    for (auto& root : roots) {
        try {
            for (auto& p : filesystem::recursive_directory_iterator(root, filesystem::directory_options::skip_permission_denied)) {
                if (!p.is_regular_file()) continue;

                FileEntry e;
                e.path = p.path().string();
                e.id = hash64(e.path);
                e.parentId = hash64(p.path().parent_path().string());
                e.size = p.file_size();
                e.modified = toUnixTS(p.last_write_time());
                e.type = detectType(e.path);

                // Call progress callback for every file (for FileRush real-time display)
                // Must do this BEFORE moving e to g_entries, otherwise path becomes empty
                jstring jpath = env->NewStringUTF(e.path.c_str());
                env->CallVoidMethod(jcallback, onProgressMethod, (jlong)currentFile, (jlong)totalFiles, jpath);
                env->DeleteLocalRef(jpath);

                g_entries.push_back(std::move(e));
                currentFile++;
            }
        } catch (const exception& ex) {
            // Skip directories we can't access
        }
    }

    env->DeleteLocalRef(callbackClass);
}

/**
 * @brief Save index to binary file
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @param jindexPath Path to save the index file
 */
JNIEXPORT void JNICALL Java_fastfileindex_FastFileIndex_save(
    JNIEnv* env,
    jclass clazz,
    jstring jindexPath) {

    lock_guard<mutex> lock(g_entriesMutex);

    const char* indexPath = env->GetStringUTFChars(jindexPath, nullptr);
    string indexPathStr(indexPath);

    // First, build paths blob
    vector<char> pathsBlob;
    size_t totalPathSize = 0;
    for (auto& e : g_entries) {
        totalPathSize += e.path.size();
    }
    pathsBlob.reserve(totalPathSize);

    vector<uint32_t> pathOffsets;
    size_t currentOffset = 0;
    for (auto& e : g_entries) {
        pathOffsets.push_back(currentOffset);
        pathsBlob.insert(pathsBlob.end(), e.path.begin(), e.path.end());
        currentOffset += e.path.size();
    }

    // Write paths.bin
    string pathsPath = indexPathStr + ".paths";
    ofstream pathsOut(pathsPath, ios::binary);
    pathsOut.write(pathsBlob.data(), pathsBlob.size());
    pathsOut.close();

    // Write files.idx
    ofstream out(indexPathStr, ios::binary);
    for (size_t i = 0; i < g_entries.size(); i++) {
        FileEntryHeader h;
        h.id = g_entries[i].id;
        h.parentId = g_entries[i].parentId;
        h.size = g_entries[i].size;
        h.modified = g_entries[i].modified;
        h.type = g_entries[i].type;
        h.pathOffset = pathOffsets[i];
        h.pathLen = g_entries[i].path.size();

        out.write((char*)&h, sizeof(h));
    }
    out.close();

    env->ReleaseStringUTFChars(jindexPath, indexPath);
}

JNIEXPORT void JNICALL Java_fastfileindex_FastFileIndex_load(JNIEnv* env, jclass clazz, jstring jindexPath) {
    lock_guard<mutex> lock(g_entriesMutex);
    
    // Clean up previous mapping
    if (g_mappedData) { UnmapViewOfFile(g_mappedData); g_mappedData = nullptr; }
    if (g_hMap) { CloseHandle(g_hMap); g_hMap = nullptr; }
    if (g_hFile) { CloseHandle(g_hFile); g_hFile = nullptr; }
    g_entries.clear();

    const char* indexPath = env->GetStringUTFChars(jindexPath, nullptr);
    if (!indexPath) return;
    string indexPathStr(indexPath);
    env->ReleaseStringUTFChars(jindexPath, indexPath);

    // 1. Load paths
    string pathsPath = indexPathStr + ".paths";
    ifstream pathsIn(pathsPath, ios::binary | ios::ate);
    if (!pathsIn.is_open()) return;
    size_t pathsSize = pathsIn.tellg();
    pathsIn.seekg(0, ios::beg);
    vector<char> pathsBlob(pathsSize);
    pathsIn.read(pathsBlob.data(), pathsSize);
    pathsIn.close();

    // 2. Map index
    g_hFile = CreateFileA(indexPathStr.c_str(), GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    if (g_hFile == INVALID_HANDLE_VALUE) return;
    DWORD fileSize = GetFileSize(g_hFile, NULL);
    g_fileSize = fileSize;
    g_hMap = CreateFileMappingA(g_hFile, NULL, PAGE_READONLY, 0, fileSize, NULL);
    if (!g_hMap) { CloseHandle(g_hFile); g_hFile = nullptr; return; }
    g_mappedData = (char*)MapViewOfFile(g_hMap, FILE_MAP_READ, 0, 0, fileSize);
    if (!g_mappedData) { CloseHandle(g_hMap); CloseHandle(g_hFile); g_hMap = nullptr; g_hFile = nullptr; return; }

    // 3. Parse
    size_t offset = 0;
    while (offset < fileSize) {
        FileEntryHeader* h = (FileEntryHeader*)(g_mappedData + offset);
        offset += sizeof(FileEntryHeader);
        FileEntry e;
        e.id = h->id; e.parentId = h->parentId; e.size = h->size; e.modified = h->modified; e.type = h->type;
        if (h->pathOffset + h->pathLen <= pathsBlob.size()) {
            e.path = string(pathsBlob.data() + h->pathOffset, h->pathLen);
        }
        g_entries.push_back(std::move(e));
    }
}

/**
 * @brief Get the number of entries in the loaded index
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @return Number of file entries
 */
JNIEXPORT jlong JNICALL Java_fastfileindex_FastFileIndex_getEntryCount(
    JNIEnv* env,
    jclass clazz) {

    lock_guard<mutex> lock(g_entriesMutex);
    return (jlong)g_entries.size();
}

/**
 * @brief Get the path of a file entry by index
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @param index Index of the entry (0-based)
 * @return File path as JNI string
 */
JNIEXPORT jstring JNICALL Java_fastfileindex_FastFileIndex_getEntryPath(
    JNIEnv* env,
    jclass clazz,
    jlong index) {

    lock_guard<mutex> lock(g_entriesMutex);
    if (index < 0 || index >= (jlong)g_entries.size()) {
        return nullptr;
    }
    return env->NewStringUTF(g_entries[index].path.c_str());
}

/**
 * @brief Get the size of a file entry by index
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @param index Index of the entry (0-based)
 * @return File size in bytes
 */
JNIEXPORT jlong JNICALL Java_fastfileindex_FastFileIndex_getEntrySize(
    JNIEnv* env,
    jclass clazz,
    jlong index) {

    lock_guard<mutex> lock(g_entriesMutex);
    if (index < 0 || index >= (jlong)g_entries.size()) {
        return 0;
    }
    return (jlong)g_entries[index].size;
}

/**
 * @brief Get the modification time of a file entry by index
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @param index Index of the entry (0-based)
 * @return Unix timestamp
 */
JNIEXPORT jlong JNICALL Java_fastfileindex_FastFileIndex_getEntryModified(
    JNIEnv* env,
    jclass clazz,
    jlong index) {

    lock_guard<mutex> lock(g_entriesMutex);
    if (index < 0 || index >= (jlong)g_entries.size()) {
        return 0;
    }
    return (jlong)g_entries[index].modified;
}

/**
 * @brief Get the type of a file entry by index
 * 
 * @param env JNI environment pointer
 * @param clazz Java class object
 * @param index Index of the entry (0-based)
 * @return File type enum value
 */
JNIEXPORT jint JNICALL Java_fastfileindex_FastFileIndex_getEntryType(
    JNIEnv* env,
    jclass clazz,
    jlong index) {

    lock_guard<mutex> lock(g_entriesMutex);
    if (index < 0 || index >= (jlong)g_entries.size()) {
        return 0;
    }
    return (jint)g_entries[index].type;
}

/**
 * @brief JNI Implementation for fastfileindex.FileIndex (Object-Oriented)
 */
JNIEXPORT jobject JNICALL Java_fastfileindex_FileIndex_open__Ljava_lang_String_2(JNIEnv* env, jclass clazz, jstring indexPath) {
    // 1. Load the index using the static logic
    Java_fastfileindex_FastFileIndex_load(env, clazz, indexPath);
    
    // 2. Create new FileIndex instance
    jmethodID constructor = env->GetMethodID(clazz, "<init>", "(J)V");
    if (constructor == NULL) return NULL;
    return env->NewObject(clazz, constructor, (jlong)0xABCDEF);
}

JNIEXPORT jlong JNICALL Java_fastfileindex_FileIndex_entryCount(JNIEnv* env, jobject obj) {
    return Java_fastfileindex_FastFileIndex_getEntryCount(env, NULL);
}

JNIEXPORT void JNICALL Java_fastfileindex_FileIndex_close(JNIEnv* env, jobject obj) {
    // Cleanup global state
    lock_guard<mutex> lock(g_entriesMutex);
    if (g_mappedData) UnmapViewOfFile(g_mappedData);
    if (g_hMap) CloseHandle(g_hMap);
    if (g_hFile) CloseHandle(g_hFile);
    g_mappedData = nullptr;
    g_hMap = nullptr;
    g_hFile = nullptr;
    g_entries.clear();
}

} // extern "C"

/**
 * @brief DLL entry point
 */
BOOL APIENTRY DllMain(HMODULE hModule, DWORD reason, LPVOID lpReserved) {
    return TRUE;
}
