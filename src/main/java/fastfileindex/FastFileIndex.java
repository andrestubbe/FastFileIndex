package fastfileindex;

/**
 * FastFileIndex - Creates a compact, append-only, mmap-friendly binary index of all files.
 * 
 * <p>FastFileIndex is the first module in the FastJava file search engine trilogy:
 * <ul>
 *   <li>FastFileIndex - Full filesystem scan → produces a binary, mmap-capable index of all files</li>
 *   <li>FastFileSearch - Builds Prefix Trie, N-Gram index, Exact Match map, and Ranking engine on top of the index</li>
 *   <li>FastFileWatch - Uses USN Journal to keep the index + search structures live-updated with zero rescans</li>
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
public class FastFileIndex {
    static {
        try {
            // Try System.loadLibrary first (default)
            System.loadLibrary("fastfileindex");
        } catch (UnsatisfiedLinkError e1) {
            try {
                // Fallback to absolute path (relative to user.dir)
                String userDir = System.getProperty("user.dir");
                String dllPath = userDir + "\\build\\fastfileindex.dll";
                System.load(dllPath);
            } catch (UnsatisfiedLinkError e2) {
                System.err.println("Failed to load fastfileindex.dll: " + e2.getMessage());
                throw e2;
            }
        }
    }
    
    /**
     * Builds the file index by scanning the specified root directories.
     * @param roots Array of root directory paths to scan
     */
    public static native void build(String[] roots);
    
    /**
     * Builds the file index with progress callback.
     * @param roots Array of root directory paths to scan
     * @param callback Progress callback interface
     */
    public static native void buildWithProgress(String[] roots, ProgressCallback callback);
    
    /**
     * Saves the built index to a binary file.
     * @param indexPath Path where the index file should be saved
     */
    public static native void save(String indexPath);
    
    /**
     * Loads an index from a binary file using memory-mapped I/O.
     * @param indexPath Path to the index file to load
     */
    public static native void load(String indexPath);
    
    /**
     * Gets the number of entries in the loaded index.
     * @return Number of file entries
     */
    public static native long getEntryCount();
    
    /**
     * Gets the path of a file entry by index.
     * @param index Index of the entry (0-based)
     * @return File path
     */
    public static native String getEntryPath(long index);
    
    /**
     * Gets the size of a file entry by index.
     * @param index Index of the entry (0-based)
     * @return File size in bytes
     */
    public static native long getEntrySize(long index);
    
    /**
     * Gets the modification time of a file entry by index.
     * @param index Index of the entry (0-based)
     * @return Unix timestamp
     */
    public static native long getEntryModified(long index);
    
    /**
     * Gets the type of a file entry by index.
     * @param index Index of the entry (0-based)
     * @return File type enum value
     */
    public static native int getEntryType(long index);
    
    public static void main(String[] args) {
        System.out.println("=== FastFileIndex ===");
        System.out.println("FastFileIndex - Binary file indexing with mmap support");
        System.out.println("=== OK ===");
    }
}
