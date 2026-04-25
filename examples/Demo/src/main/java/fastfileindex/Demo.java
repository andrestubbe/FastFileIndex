package fastfileindex;

import fastfileindex.FastFileIndex;
import fastfileindex.ProgressCallback;

/**
 * Demo - Visual demo showing all indexed files rapidly.
 * Demonstrates the speed and volume of the index by displaying files in a "rush" effect.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastFileIndex Demo ===");
        System.out.println("Ready to scan C: drive in real-time");
        System.out.println("Press ENTER to start...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore
        }
        System.out.println("Starting real-time file scan...");
        System.out.println();
        
        // Scan entire C: drive
        String[] roots = { "C:\\" };
        
        ProgressCallback callback = new ProgressCallback() {
            private long fileCount = 0;
            private long totalSize = 0;
            
            @Override
            public void onProgress(long current, long total, String currentPath) {
                // Truncate path to fit console width (120 chars) to avoid word wrap
                String displayPath = currentPath;
                if (currentPath != null && currentPath.length() > 120) {
                    displayPath = currentPath.substring(0, 120);
                }
                System.out.println(displayPath);
                System.out.flush();
                
                fileCount = current;
                
                // Small delay for visual effect (every 50 files)
                if (current % 50 == 0 && current > 0) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }
            
            public long getFileCount() {
                return fileCount;
            }
            
            public long getTotalSize() {
                return totalSize;
            }
        };
        
        FastFileIndex.buildWithProgress(roots, callback);
        
        // Calculate total size from the index
        long totalSize = 0;
        long count = FastFileIndex.getEntryCount();
        for (long i = 0; i < count; i++) {
            totalSize += FastFileIndex.getEntrySize(i);
        }
        
        System.out.println();
        System.out.println();
        System.out.println("=== Demo Complete ===");
        System.out.println("Total files scanned: " + count);
        System.out.println("Total size: " + formatSize(totalSize));
    }
    
    private static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
