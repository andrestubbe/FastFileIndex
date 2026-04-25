package fastfileindex;

import fastfileindex.FastFileIndex;

/**
 * Demo class for testing FastFileIndex functionality.
 * This class demonstrates usage and validates the implementation.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastFileIndex Demo ===");
        
        // Test basic functionality
        String[] roots = { "C:\\Users\\andre\\Documents" };
        FastFileIndex.build(roots);
        
        String indexPath = "test-index.idx";
        FastFileIndex.save(indexPath);
        
        FastFileIndex.load(indexPath);
        
        long count = FastFileIndex.getEntryCount();
        System.out.println("Indexed files: " + count);
        
        if (count > 0) {
            System.out.println("First file: " + FastFileIndex.getEntryPath(0));
            System.out.println("Size: " + FastFileIndex.getEntrySize(0) + " bytes");
            System.out.println("Type: " + FastFileIndex.getEntryType(0));
        }
        
        System.out.println("=== Demo Complete ===");
    }
}
