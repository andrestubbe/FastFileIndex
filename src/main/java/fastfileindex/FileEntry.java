package fastfileindex;

/**
 * FileEntry - File entry value type.
 */
public record FileEntry(
        long id,
        long parentId,
        String path,
        long size,
        long modified,
        FileType type
) {}
