package fastfileindex;

import fastfileindex.FastFileIndex;
import fastfileindex.ProgressCallback;

/**
 * Demo class for testing FastFileIndex functionality.
 * This class demonstrates usage and validates the implementation.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastFileIndex Demo ===");
        System.out.println();
        
        // Test with visual progress bar
        String[] roots = { "C:\\Users\\andre\\Documents" };
        
        ProgressCallback callback = new ProgressCallback() {
            private long lastUpdate = 0;
            
            @Override
            public void onProgress(long current, long total, String currentPath) {
                long now = System.currentTimeMillis();
                // Update display every 100ms to avoid flickering
                if (now - lastUpdate < 100 && current < total) {
                    return;
                }
                lastUpdate = now;
                
                // Calculate percentage
                double percentage = total > 0 ? (current * 100.0 / total) : 0;
                
                // Build progress bar
                int barWidth = 40;
                int filled = (int)(barWidth * percentage / 100);
                StringBuilder bar = new StringBuilder("[");
                for (int i = 0; i < barWidth; i++) {
                    if (i < filled) {
                        bar.append("=");
                    } else if (i == filled) {
                        bar.append(">");
                    } else {
                        bar.append(" ");
                    }
                }
                bar.append("]");
                
                // Clear line and print progress
                System.out.print("\r" + bar + " " + String.format("%.1f", percentage) + "% (" + current + "/" + total + ") files");
                
                // Print current path on new line occasionally
                if (current % 500 == 0 || current == total) {
                    System.out.println();
                    System.out.println("  Scanning: " + truncatePath(currentPath, 60));
                }
            }
            
            private String truncatePath(String path, int maxLength) {
                if (path == null || path.length() <= maxLength) {
                    return path;
                }
                return "..." + path.substring(path.length() - maxLength + 3);
            }
        };
        
        System.out.println("Starting index build...");
        FastFileIndex.buildWithProgress(roots, callback);
        System.out.println();
        System.out.println();
        
        String indexPath = "test-index.idx";
        System.out.println("Saving index to: " + indexPath);
        FastFileIndex.save(indexPath);
        
        System.out.println("Loading index from: " + indexPath);
        FastFileIndex.load(indexPath);
        
        long count = FastFileIndex.getEntryCount();
        System.out.println();
        System.out.println("=== Results ===");
        System.out.println("Indexed files: " + count);
        
        System.out.println();
        System.out.println("=== Demo Complete ===");
    }
}
