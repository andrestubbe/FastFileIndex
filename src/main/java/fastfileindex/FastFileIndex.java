package fastfileindex;

import java.io.File;

public class FastFileIndex {
    static {
        try {
            // Use FastCore to handle extraction and loading of the JNI DLL securely
            fastcore.FastCore.loadLibrary("fastfileindex");
        } catch (Throwable e) {
            System.err.println("CRITICAL: FastCore failed to load native DLL: " + e.getMessage());
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
