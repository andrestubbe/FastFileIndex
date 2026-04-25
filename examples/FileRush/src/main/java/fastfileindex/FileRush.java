package fastfileindex;

import fastfileindex.FastFileIndex;
import fastfileindex.ProgressCallback;

/**
 * FileRush - Visual demo showing all indexed files rapidly.
 * Demonstrates the speed and volume of the index by displaying files in a "rush" effect.
 */
public class FileRush {
    public static void main(String[] args) {
        System.out.println("=== FileRush ===");
        System.out.println("Starting real-time file scan...");
        System.out.println();
        
        // Scan entire C: drive
        String[] roots = { "C:\\" };
        
        ProgressCallback callback = new ProgressCallback() {
            @Override
            public void onProgress(long current, long total, String currentPath) {
                // Truncate path to fit console width (100 chars) to avoid word wrap
                String displayPath = currentPath;
                if (currentPath != null && currentPath.length() > 100) {
                    displayPath = "..." + currentPath.substring(currentPath.length() - 97);
                }
                System.out.println(displayPath);
                System.out.flush();
                
                // Small delay for visual effect (every 50 files)
                if (current % 50 == 0 && current > 0) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }
        };
        
        FastFileIndex.buildWithProgress(roots, callback);
        
        System.out.println();
        System.out.println();
        System.out.println("=== Rush Complete ===");
    }
    
    private static String truncatePath(String path, int maxLength) {
        if (path == null || path.length() <= maxLength) {
            return path;
        }
        return "..." + path.substring(path.length() - maxLength + 3);
    }
}
