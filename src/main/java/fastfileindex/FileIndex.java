package fastfileindex;

/**
 * FileIndex - Handle-based file index with mmap loading.
 * 
 * <p>FileIndex is the first module in the FastJava file search engine trilogy:
 * <ul>
 *   <li>FileIndex - Full filesystem scan → produces a binary, mmap-capable index of all files</li>
 *   <li>SearchEngine - Builds Prefix Trie, N-Gram index, Exact Match map, and Ranking engine on top of the index</li>
 *   <li>WatchService - Uses USN Journal to keep the index + search structures live-updated with zero rescans</li>
 * </ul>
 * 
 * <p>This architecture is similar to Everything, Spotlight, VSCode, and fsearch but modular and embeddable.
 * 
 * <p><b>Key Features:</b>
 * <ul>
 *   <li>mmap-based loading (CreateFileMapping + MapViewOfFile)</li>
 *   <li>Path-Blob + Offsets instead of std::string per entry</li>
 *   <li>Parallel build with threadpool and lock-free append buffer</li>
 *   <li>FNV-1a hashing for stable file IDs</li>
 *   <li>Type detection (image, pdf, text, code)</li>
 *   <li>Converts file timestamps to Unix seconds</li>
 *   <li>Saves to files.idx (append-only)</li>
 *   <li>Loads instantly (&lt;1-3 ms with mmap)</li>
 *   <li>Zero-copy path access via string_view</li>
 * </ul>
 * 
 * <p><b>Binary Format:</b>
 * <ul>
 *   <li>files.idx - FileEntryHeader with id, parentId, size, modified, type, pathOffset, pathLen</li>
 *   <li>paths.bin - Path-Blob containing all paths in a single contiguous blob</li>
 * </ul>
 * 
 * @since 1.0.0
 * @version 1.0.0
 */
public final class FileIndex {
    static {
        try {
            // Try System.loadLibrary first (default)
            System.loadLibrary("fastcore");
        } catch (UnsatisfiedLinkError e1) {
            try {
                // Fallback to absolute path (relative to user.dir)
                String userDir = System.getProperty("user.dir");
                String dllPath = userDir + "\\build\\fastcore.dll";
                System.load(dllPath);
            } catch (UnsatisfiedLinkError e2) {
                System.err.println("Failed to load fastcore.dll: " + e2.getMessage());
                throw e2;
            }
        }
    }
    
    private final long nativeHandle;

    private FileIndex(long nativeHandle) {
        this.nativeHandle = nativeHandle;
    }

    public long handle() {
        return nativeHandle;
    }

    /**
     * Build index from roots.
     */
    public static native FileIndex build(String[] roots, BuildOptions options);

    /**
     * Open existing index from file.
     */
    public static native FileIndex open(String indexPath);

    /**
     * Save index to file.
     */
    public native void save(String indexPath);

    /**
     * Close index and free resources.
     */
    public native void close();

    /**
     * Get index metadata.
     */
    public native IndexMetadata metadata();

    /**
     * Get entry count.
     */
    public native long entryCount();

    /**
     * Get file entry by ID.
     */
    public native FileEntry get(long id);

    public static void main(String[] args) {
        System.out.println("=== FileIndex ===");
        System.out.println("FileIndex - Binary file indexing with mmap support");
        System.out.println("=== OK ===");
    }
}
