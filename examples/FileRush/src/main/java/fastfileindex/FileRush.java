package fastfileindex;

import fastfileindex.FastFileIndex;

/**
 * FileRush - Visual demo showing all indexed files rapidly.
 * Demonstrates the speed and volume of the index by displaying files in a "rush" effect.
 */
public class FileRush {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: FileRush <index-file>");
            System.out.println("Example: FileRush test-index.idx");
            return;
        }

        String indexPath = args[0];
        
        System.out.println("=== FileRush ===");
        System.out.println("Loading index: " + indexPath);
        
        FastFileIndex.load(indexPath);
        
        long count = FastFileIndex.getEntryCount();
        System.out.println("Indexed files: " + count);
        System.out.println();
        System.out.println("Starting file rush...");
        System.out.println();
        
        // ANSI color codes for visual effect
        final String RESET = "\u001B[0m";
        final String[] COLORS = {
            "\u001B[31m", // Red
            "\u001B[32m", // Green
            "\u001B[33m", // Yellow
            "\u001B[34m", // Blue
            "\u001B[35m", // Magenta
            "\u001B[36m", // Cyan
            "\u001B[37m"  // White
        };
        
        // Display files in columns with color cycling
        int columns = 4;
        int colorIndex = 0;
        
        for (long i = 0; i < count; i++) {
            String path = FastFileIndex.getEntryPath(i);
            long size = FastFileIndex.getEntrySize(i);
            int type = FastFileIndex.getEntryType(i);
            
            // Truncate path for display
            String displayPath = truncatePath(path, 25);
            
            // Color cycling
            String color = COLORS[colorIndex % COLORS.length];
            colorIndex++;
            
            // Format output
            String typeStr = getTypeString(type);
            String sizeStr = formatSize(size);
            
            System.out.print(color + String.format("%-25s %-8s %s", displayPath, sizeStr, typeStr) + RESET);
            
            // New line after each column
            if ((i + 1) % columns == 0) {
                System.out.println();
            }
            
            // Small delay for visual effect (every 100 files)
            if (i % 100 == 0 && i > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        
        // Final newline if needed
        if (count % columns != 0) {
            System.out.println();
        }
        
        System.out.println();
        System.out.println();
        System.out.println("=== Rush Complete ===");
        System.out.println("Total files displayed: " + count);
    }
    
    private static String truncatePath(String path, int maxLength) {
        if (path == null || path.length() <= maxLength) {
            return path;
        }
        return "..." + path.substring(path.length() - maxLength + 3);
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
    
    private static String getTypeString(int type) {
        switch (type) {
            case 0: return "DIR";
            case 1: return "IMG";
            case 2: return "PDF";
            case 3: return "TXT";
            case 4: return "CODE";
            case 5: return "VID";
            case 6: return "AUD";
            case 7: return "ARC";
            default: return "UNK";
        }
    }
}
