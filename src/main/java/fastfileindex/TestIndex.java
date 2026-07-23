package fastfileindex;

import java.io.File;

public class TestIndex {
    public static void main(String[] args) {
        System.out.println("=== Testing FastFileIndex ===");
        String userHome = System.getProperty("user.home");
        String docsPath = new File(userHome, "Documents").getAbsolutePath();
        System.out.println("Target directory: " + docsPath);

        System.out.println("\nBuilding index...");
        FastFileIndex.buildWithProgress(new String[]{docsPath}, (ProgressCallback) (current, total, path) -> {
            if (current % 500 == 0) {
                System.out.println("Progress: " + current + " files scanned, current: " + path);
            }
        });

        long count = FastFileIndex.getEntryCount();
        System.out.println("\nBuild complete! Total entries indexed: " + count);

        System.out.println("\nSample entries:");
        long limit = Math.min(count, 15);
        for (long i = 0; i < limit; i++) {
            System.out.println(" [" + i + "] " + FastFileIndex.getEntryPath(i) + " (type=" + FastFileIndex.getEntryType(i) + ", size=" + FastFileIndex.getEntrySize(i) + ")");
        }

        System.out.println("\nSaving index to test_files.idx...");
        FastFileIndex.save("test_files.idx");
        System.out.println("Saved! File size: " + new File("test_files.idx").length() + " bytes");

        System.out.println("\nTesting loading test_files.idx...");
        FastFileIndex.load("test_files.idx");
        System.out.println("Loaded! Entry count after load: " + FastFileIndex.getEntryCount());
        System.out.println("=== TEST SUCCESS ===");
    }
}
