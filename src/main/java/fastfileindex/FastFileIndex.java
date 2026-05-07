package fastfileindex;

import java.io.File;

public class FastFileIndex {
    static {
        try {
            // FORCE absolute path to the newly compiled DLL to avoid outdated cached versions
            String exactDllPath = "C:\\Users\\andre\\Documents\\2026-04-28-Work-FastJava\\FastFileIndex\\build\\fastfileindex.dll";
            System.out.println("DEBUG: Forcing JNI load from: " + exactDllPath);
            System.load(exactDllPath);
            System.out.println("DEBUG: JNI DLL loaded successfully!");
        } catch (Throwable e) {
            System.err.println("CRITICAL: Failed to load JNI DLL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static native void build(String[] roots);
    public static native void buildWithProgress(String[] roots, Object callback);
    public static native void save(String indexPath);
    public static native void load(String indexPath);
    
    public static native long getEntryCount();
    public static native String getEntryPath(long index);
    public static native long getEntrySize(long index);
    public static native long getEntryModified(long index);
    public static native int getEntryType(long index);
}
