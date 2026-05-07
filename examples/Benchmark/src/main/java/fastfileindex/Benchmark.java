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
            
            // Bar visualization
            int barLength = Math.min((int) speedup, 50);
            System.out.print("[");
            for (int i = 0; i < 50; i++) {
                if (i < barLength) {
                    System.out.print("█");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println("] " + String.format("%.2fx", speedup));
        }
    }
    
    private static long runFastFileIndex(String path, boolean warmup) {
        if (!warmup) System.out.print("FastFileIndex: ");
        
        long start = System.currentTimeMillis();
        
        FastFileIndex.build(new String[]{path});
        
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        
        if (!warmup) {
            long count = FastFileIndex.getEntryCount();
            System.out.println(elapsed + " ms (" + count + " files)");
        }
        
        return elapsed;
    }
    
    private static long runJavaWalk(String path, boolean warmup) {
        if (!warmup) System.out.print("Java Files.walk(): ");
        
        long start = System.currentTimeMillis();
        AtomicLong count = new AtomicLong(0);
        
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) {
                        count.incrementAndGet();
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
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
