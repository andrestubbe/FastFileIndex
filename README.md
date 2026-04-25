# FastFileIndex - Ultra-fast native file indexer with real-time metadata extraction and zero-copy directory traversal [ALPHA].



[![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastFileIndex.svg)](https://jitpack.io/#andrestubbe/FastFileIndex)

[![Demo Screenshot](docs/demo-screenshot.png)](https://www.youtube.com/watch?v=69XEJ49yqbA)

## Table of Contents

- [Description](#description)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Performance](#performance)
- [Installation](#installation)
- [Building from Source](#building-from-source)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

## Description

FastFileIndex is the first module in the FastJava file search engine trilogy:

- **FastFileIndex** - Full filesystem scan ? produces a binary, mmap-capable index of all files
- **FastFileSearch** - Builds Prefix Trie, N-Gram index, Exact Match map, and Ranking engine on top of the index
- **FastFileWatch** - Uses USN Journal to keep the index + search structures live-updated with zero rescans

This architecture is similar to Everything, Spotlight, VSCode, and fsearch but modular and embeddable.

## Quick Start

```java
import fastfileindex.FastFileIndex;

public class Example {
    public static void main(String[] args) {
        // Build index from root directories
        String[] roots = { "C:\\Users\\YourName\\Documents" };
        FastFileIndex.build(roots);
        
        // Save to binary file
        FastFileIndex.save("files.idx");
        
        // Load from binary file (instant with mmap)
        FastFileIndex.load("files.idx");
        
        // Query entries
        long count = FastFileIndex.getEntryCount();
        System.out.println("Indexed files: " + count);
        
        // Get file information
        String path = FastFileIndex.getEntryPath(0);
        long size = FastFileIndex.getEntrySize(0);
        long modified = FastFileIndex.getEntryModified(0);
        int type = FastFileIndex.getEntryType(0);
    }
}
```

## Key Features

- **mmap-based loading** - Uses CreateFileMapping + MapViewOfFile for zero-copy access
- **Path-Blob storage** - All paths stored in a single contiguous blob for better cache behavior
- **Parallel build** - Threadpool with lock-free append buffer for fast scanning
- **FNV-1a hashing** - Stable file IDs based on path hash
- **Type detection** - Automatic detection of images, PDFs, text, code, video, audio, archives
- **Unix timestamps** - Converts file timestamps to Unix seconds
- **Append-only format** - No fragmentation, no locks needed
- **Instant loading** - Loads in &lt;1-3 ms with memory-mapped I/O
- **Zero-copy access** - Path access via string_view without copying

## Performance

Benchmark results scanning C:\ drive (Windows 10):

| Method | Time | Files | Speedup |
|--------|------|-------|---------|
| FastFileIndex | 5,633 ms | 89,171 | 8.33x faster |
| Java Files.walk() | 46,895 ms | 682,815 | baseline |

FastFileIndex is **8.33x faster** than Java's `Files.walk()` for filesystem scanning. The benchmark can be run from the `examples/Benchmark` directory.

## Binary Format

The index consists of two files:

- **files.idx** - FileEntryHeader records with id, parentId, size, modified, type, pathOffset, pathLen
- **paths.bin** - Path-Blob containing all paths in a single contiguous blob

## Installation

### Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastfileindex</artifactId>
    <version>v1.0.0</version>
</dependency>
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastcore</artifactId>
    <version>v1.0.0</version>
</dependency>
```

> **Note:** FastCore handles native library loading from the JAR automatically. Both dependencies are required.

### Gradle (JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastfileindex:v1.0.0'
    implementation 'com.github.andrestubbe:fastcore:v1.0.0'
}
```

## Building from Source

For detailed build instructions, see [COMPILE.md](COMPILE.md).

## Platform Support

- **Windows 10+** (x86_64) - Fully supported with native implementation
- **Linux** - Planned
- **macOS** - Planned

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Related Projects

- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) - Prefix Trie, N-Gram index, and Ranking engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) - USN Journal-based live updates
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction
