/**
 * @file FastFileIndex.cpp
 * @brief FastFileIndex native implementation
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

static vector<FileEntry> g_entries;
static mutex g_entriesMutex;
static char* g_mappedData = nullptr;
static HANDLE g_hMap = nullptr;
static HANDLE g_hFile = nullptr;
static size_t g_fileSize = 0;

static uint64_t hash64(const string& str) {
    uint64_t hash = 14695981039346656037ULL;
    for (char c : str) {
        hash ^= (uint8_t)c;
        hash *= 1099511628211ULL;
    }
    return hash;
}

static uint64_t toUnixTS(filesystem::file_time_type ft) {
    auto sctp = chrono::time_point_cast<chrono::system_clock::duration>(
        ft - filesystem::file_time_type::clock::now() + chrono::system_clock::now());
    return chrono::duration_cast<chrono::seconds>(sctp.time_since_epoch()).count();
}

static uint32_t detectType(const string& path) {
    size_t dot = path.rfind('.');
    if (dot == string::npos) return 0;
    string ext = path.substr(dot + 1);
    for (auto& c : ext) c = tolower(c);
    
    if (ext == "java" || ext == "cpp" || ext == "h" || ext == "c" || ext == "py" || ext == "js" || ext == "ts") return 1;
    if (ext == "txt" || ext == "md" || ext == "json" || ext == "xml" || ext == "yaml" || ext == "yml") return 2;
    if (ext == "png" || ext == "jpg" || ext == "jpeg" || ext == "gif" || ext == "bmp" || ext == "ico") return 3;
    if (ext == "zip" || ext == "tar" || ext == "gz" || ext == "7z" || ext == "rar" || ext == "jar") return 4;
    if (ext == "exe" || ext == "dll" || ext == "so" || ext == "dylib") return 5;
    return 0;
}

extern "C" {

JNIEXPORT void* JNICALL FastFileIndex_getNativeEntriesHandle() {
    return (void*)&g_entries;
}

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
        std::error_code ec;
        auto it = filesystem::recursive_directory_iterator(root, filesystem::directory_options::skip_permission_denied, ec);
        auto end = filesystem::recursive_directory_iterator();

        while (it != end) {
            if (ec) {
                ec.clear();
                it.increment(ec);
                continue;
            }

            try {
                if (it->is_regular_file(ec)) {
                    FileEntry e;
                    e.path = it->path().string();
                    e.id = hash64(e.path);
                    e.parentId = hash64(it->path().parent_path().string());
                    e.size = it->file_size(ec);
                    e.modified = toUnixTS(it->last_write_time(ec));
                    e.type = detectType(e.path);

                    g_entries.push_back(std::move(e));
                }
            } catch (...) {
            }

            it.increment(ec);
        }
    }
}

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

    jclass callbackClass = env->GetObjectClass(jcallback);
    jmethodID onProgressMethod = env->GetMethodID(callbackClass, "onProgress", "(JJLjava/lang/String;)V");

    long totalFiles = 0;
    long currentFile = 0;
    for (auto& root : roots) {
        std::error_code ec;
        auto it = filesystem::recursive_directory_iterator(root, filesystem::directory_options::skip_permission_denied, ec);
        auto end = filesystem::recursive_directory_iterator();

        while (it != end) {
            if (ec) {
                ec.clear();
                it.increment(ec);
                continue;
            }

            try {
                if (it->is_regular_file(ec)) {
                    FileEntry e;
                    e.path = it->path().string();
                    e.id = hash64(e.path);
                    e.parentId = hash64(it->path().parent_path().string());
                    e.size = it->file_size(ec);
                    e.modified = toUnixTS(it->last_write_time(ec));
                    e.type = detectType(e.path);

                    jstring jpath = env->NewStringUTF(e.path.c_str());
                    env->CallVoidMethod(jcallback, onProgressMethod, (jlong)currentFile, (jlong)totalFiles, jpath);
                    env->DeleteLocalRef(jpath);

                    g_entries.push_back(std::move(e));
                    currentFile++;
                }
            } catch (...) {
            }

            it.increment(ec);
        }
    }

    env->DeleteLocalRef(callbackClass);
}

JNIEXPORT void JNICALL Java_fastfileindex_FastFileIndex_save(
    JNIEnv* env,
    jclass clazz,
    jstring jindexPath) {

    lock_guard<mutex> lock(g_entriesMutex);
    const char* indexPath = env->GetStringUTFChars(jindexPath, nullptr);
    string indexPathStr(indexPath);
    env->ReleaseStringUTFChars(jindexPath, indexPath);

    ofstream out(indexPathStr, ios::binary);
    if (!out) return;

    string pathsStr = indexPathStr + ".paths";
    ofstream outPaths(pathsStr, ios::binary);
    if (!outPaths) return;

    uint32_t pathOffset = 0;
    for (auto& e : g_entries) {
        FileEntryHeader h;
        h.id = e.id;
        h.parentId = e.parentId;
        h.size = e.size;
        h.modified = e.modified;
        h.type = e.type;
        h.pathOffset = pathOffset;
        h.pathLen = (uint32_t)e.path.length();

        out.write((char*)&h, sizeof(h));
        outPaths.write(e.path.c_str(), e.path.length());
        pathOffset += h.pathLen;
    }
}

JNIEXPORT void JNICALL Java_fastfileindex_FastFileIndex_load(
    JNIEnv* env,
    jclass clazz,
    jstring jindexPath) {

    lock_guard<mutex> lock(g_entriesMutex);
    const char* indexPath = env->GetStringUTFChars(jindexPath, nullptr);
    string indexPathStr(indexPath);
    env->ReleaseStringUTFChars(jindexPath, indexPath);

    if (g_mappedData) UnmapViewOfFile(g_mappedData);
    if (g_hMap) CloseHandle(g_hMap);
    if (g_hFile) CloseHandle(g_hFile);
    g_entries.clear();

    g_hFile = CreateFileA(indexPathStr.c_str(), GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    if (g_hFile == INVALID_HANDLE_VALUE) return;

    DWORD sizeHigh = 0;
    DWORD sizeLow = GetFileSize(g_hFile, &sizeHigh);
    g_fileSize = ((size_t)sizeHigh << 32) | sizeLow;

    g_hMap = CreateFileMappingA(g_hFile, NULL, PAGE_READONLY, 0, 0, NULL);
    if (!g_hMap) {
        CloseHandle(g_hFile);
        return;
    }

    g_mappedData = (char*)MapViewOfFile(g_hMap, FILE_MAP_READ, 0, 0, 0);
    if (!g_mappedData) {
        CloseHandle(g_hMap);
        CloseHandle(g_hFile);
        return;
    }

    string pathsStr = indexPathStr + ".paths";
    ifstream inPaths(pathsStr, ios::binary);
    if (!inPaths) return;

    size_t count = g_fileSize / sizeof(FileEntryHeader);
    g_entries.reserve(count);

    FileEntryHeader* headers = (FileEntryHeader*)g_mappedData;
    for (size_t i = 0; i < count; i++) {
        FileEntry e;
        e.id = headers[i].id;
        e.parentId = headers[i].parentId;
        e.size = headers[i].size;
        e.modified = headers[i].modified;
        e.type = headers[i].type;

        e.path.resize(headers[i].pathLen);
        inPaths.seekg(headers[i].pathOffset);
        inPaths.read(&e.path[0], headers[i].pathLen);

        g_entries.push_back(std::move(e));
    }
}

JNIEXPORT jlong JNICALL Java_fastfileindex_FastFileIndex_getEntryCount(
    JNIEnv* env,
    jclass clazz) {
    lock_guard<mutex> lock(g_entriesMutex);
    return (jlong)g_entries.size();
}

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

JNIEXPORT jobject JNICALL Java_fastfileindex_FileIndex_open__Ljava_lang_String_2(JNIEnv* env, jclass clazz, jstring indexPath) {
    Java_fastfileindex_FastFileIndex_load(env, clazz, indexPath);
    jmethodID constructor = env->GetMethodID(clazz, "<init>", "(J)V");
    if (constructor == NULL) return NULL;
    return env->NewObject(clazz, constructor, (jlong)0xABCDEF);
}

JNIEXPORT jlong JNICALL Java_fastfileindex_FileIndex_entryCount(JNIEnv* env, jobject obj) {
    return Java_fastfileindex_FastFileIndex_getEntryCount(env, NULL);
}

JNIEXPORT void JNICALL Java_fastfileindex_FileIndex_close(JNIEnv* env, jobject obj) {
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

BOOL APIENTRY DllMain(HMODULE hModule, DWORD reason, LPVOID lpReserved) {
    return TRUE;
}