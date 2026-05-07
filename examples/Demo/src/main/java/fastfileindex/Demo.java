package fastfileindex;

/**
 * Demo - Visual demo showing all indexed files rapidly.
 * Demonstrates the speed and volume of the index by displaying files in a "rush" effect.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FileIndex Demo ===");
        System.out.println("Debug: Demo started");
        System.out.println("Ready to scan C: drive in real-time");
        System.out.println("Press ENTER to start...");
        try {
            System.in.read();
        } catch (Exception e) {
            System.out.println("Debug: Exception reading input: " + e.getMessage());
        }
        System.out.println("Starting real-time file scan...");
        System.out.println();
        
        // Scan entire C: drive
        String[] roots = { "C:\\" };
        
        // Build index with native API
        FastFileIndex.build(roots);
        
        // Calculate total size from the index
        long totalSize = 0;
        long count = FastFileIndex.getEntryCount();
        for (long i = 0; i < count; i++) {
            long size = FastFileIndex.getEntrySize(i);
            totalSize += size;
            
            // Truncate path to fit console width (120 chars) to avoid word wrap
            String displayPath = FastFileIndex.getEntryPath(i);
            if (displayPath != null && displayPath.length() > 120) {
                displayPath = displayPath.substring(0, 120);
            }
            if (displayPath != null) {
                System.out.println(displayPath);
                System.out.flush();
            }
            
            // Small delay for visual effect (every 50 files)
            if (i % 50 == 0 && i > 0) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
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
