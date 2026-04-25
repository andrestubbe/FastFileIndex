package fastfileindex;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class Benchmark {
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "C:\\";
        
        System.out.println("=== FastFileIndex vs Java Files.walk() Benchmark ===");
        System.out.println("Scanning: " + path);
        System.out.println();
        
        // Warmup
        System.out.println("Warmup runs...");
        runFastFileIndex(path, true);
        runJavaWalk(path, true);
        System.out.println();
        
        // Benchmark
        System.out.println("Benchmark runs:");
        long fastTime = runFastFileIndex(path, false);
        long javaTime = runJavaWalk(path, false);
        
        System.out.println();
        System.out.println("=== Results ===");
        System.out.println("FastFileIndex: " + fastTime + " ms");
        System.out.println("Java Files.walk(): " + javaTime + " ms");
        
        if (javaTime > 0) {
            double speedup = (double) javaTime / fastTime;
            System.out.printf("Speedup: %.2fx faster%n", speedup);
        }
    }
    
    private static long runFastFileIndex(String path, boolean warmup) {
        if (!warmup) System.out.print("FastFileIndex: ");
        
        long start = System.currentTimeMillis();
        AtomicLong count = new AtomicLong(0);
        
        FastFileIndex.buildWithProgress(new String[]{path}, new ProgressCallback() {
            @Override
            public void onProgress(String currentFile, long totalFiles, long currentPath) {
                count.incrementAndGet();
            }
        });
        
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        
        if (!warmup) {
            System.out.println(elapsed + " ms (" + count.get() + " files)");
        }
        
        return elapsed;
    }
    
    private static long runJavaWalk(String path, boolean warmup) {
        if (!warmup) System.out.print("Java Files.walk(): ");
        
        long start = System.currentTimeMillis();
        AtomicLong count = new AtomicLong(0);
        
        try {
            Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .forEach(file -> count.incrementAndGet());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        
        if (!warmup) {
            System.out.println(elapsed + " ms (" + count.get() + " files)");
        }
        
        return elapsed;
    }
}
