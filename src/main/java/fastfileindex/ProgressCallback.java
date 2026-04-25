package fastfileindex;

/**
 * Progress callback interface for indexing operations.
 */
public interface ProgressCallback {
    /**
     * Called during indexing to report progress.
     * @param current Current number of files indexed
     * @param total Estimated total number of files (may be 0 if unknown)
     * @param currentPath Current file being indexed
     */
    void onProgress(long current, long total, String currentPath);
}
