package fastfileindex;

/**
 * BuildOptions - Options for building the index.
 */
public class BuildOptions {
    private boolean followSymlinks = false;
    private boolean includeHidden = false;
    private int maxDepth = -1;

    public BuildOptions() {}

    public BuildOptions followSymlinks(boolean value) {
        this.followSymlinks = value;
        return this;
    }

    public BuildOptions includeHidden(boolean value) {
        this.includeHidden = value;
        return this;
    }

    public BuildOptions maxDepth(int value) {
        this.maxDepth = value;
        return this;
    }

    public boolean followSymlinks() {
        return followSymlinks;
    }

    public boolean includeHidden() {
        return includeHidden;
    }

    public int maxDepth() {
        return maxDepth;
    }

    public static BuildOptions defaults() {
        return new BuildOptions();
    }
}
