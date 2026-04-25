package fastfileindex;

/**
 * IndexMetadata - Index metadata value type.
 */
public record IndexMetadata(
        long entryCount,
        long totalSize,
        long buildTime,
        String version
) {}
