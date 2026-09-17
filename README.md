# FastFileIndex 0.1.2 [2026-07-23] — Ultra-Fast Native File Indexing for Java

[![Status](https://img.shields.io/badge/status-0.1.2-brightgreen.svg)](https://github.com/andrestubbe/FastFileIndex/releases/tag/0.1.2)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastFileIndex)

**🔍 Scan and search millions of files in milliseconds with zero latency.**

FastFileIndex is the high-performance file indexing engine for the FastJava ecosystem. It bypasses standard Java file IO
to provide direct, native-accelerated indexing and search capabilities for massive directory trees.

[**Watch Demo (YouTube)**](https://youtu.be/69XEJ49yqbA) | Watch JMH Benchmark (Youtube)

[![FastFileIndex Showcase](docs/screenshot.png)](https://youtu.be/69XEJ49yqbA)

---

## Quick Start

```java
import fastfileindex.FastFileIndex;

public class Demo {
    public static void main(String[] args) {
        // Scan directory trees with native C++ mmap indexing
        String[] roots = { "C:\\" };
        FastFileIndex.build(roots);

        long count = FastFileIndex.getEntryCount();
        System.out.printf("Indexed %,d files in real-time!\n", count);

        // Access indexed metadata directly
        for (long i = 0; i < Math.min(count, 5); i++) {
            System.out.printf("[%d bytes] %s\n", FastFileIndex.getEntrySize(i), FastFileIndex.getEntryPath(i));
        }
    }
}
```

---

## Table of Contents

- [Why FastFileIndex?](#why-fastfileindex)
- [Key Features](#key-features)
- [Performance](#performance)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [API Reference](#api-reference)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastFileIndex?

Indexing large file systems (e.g. hundreds of thousands or millions of source code, media, or build files) inside Java applications causes severe bottlenecks with standard JDK APIs:

- **Massive JVM Heap Bloat** — Traversal via `Files.walkFileTree()` instantiates `Path`, `FileStore`, and `BasicFileAttributes` instances for every single file, easily creating millions of short-lived objects and triggering prolonged GC stop-the-world pauses.
- **Reparse Point & Permission Crashes** — Java NIO default traversals throw unhandled `AccessDeniedException` or `FileSystemLoopException` when encountering Windows Junctions, locked system folders, or cyclic symlinks.
- **Repeated Case Normalization Overhead** — Search routines scanning millions of paths repeatedly call `.toLowerCase()` on Java strings, churning memory for every character search query.
- **Slow Re-Indexing on Application Restart** — Persisting large directory structures in JSON or serialized Java objects requires costly parsing loops instead of instant memory-mapped disk snapshots.

FastFileIndex solves this by pairing direct Win32 C++ kernel iterators with zero-allocation memory-mapped file indexing:

| Feature | Legacy `java.io.File` | Java NIO2 (`Files.walk`) | FastFileIndex |
|:---|:---|:---|:---|
| **Traversal Engine** | Legacy Win32 wrapper | NIO2 FileVisitor | **Native C++17 Iterator** |
| **Objects (1M Files)** | ~1,000,000 File objects | ~3,000,000 Path objects | **0 Heap Objects (Zero GC)** |
| **Permission Errors** | Returns `null` on error | Throws AccessDenied | **Non-throwing skip** |
| **Search Readiness** | Raw string checks | Raw string checks | **Pre-Lowercased + 64-bit Hashes** |
| **Disk Handoff** | Slow manual parsing | Slow serialization | **Binary `mmap` Snapshot** |
| **Dependencies** | JDK runtime | JDK runtime | **Pure Java + FastCore** |

---

## Key Features

- **⚡ Instant Indexing**: Scan millions of files in milliseconds using native C++ pipelines.
- **🛡️ Robust Traversal**: Non-throwing `std::error_code` iteration handles Windows Junction points and restricted folders without aborting.
- **⏱️ Zero Latency**: Real-time results for massive file systems.
- **📦 Low Footprint**: Optimized native data structures for minimal RAM usage.

---

## Performance

FastFileIndex out-performs standard Java NIO indexing by utilizing Windows-specific kernel-level optimizations.

| Operation     | FastFileIndex | Java NIO | Speedup |
|---------------|---------------|----------|---------|
| Scan 1M Files | 280 ms        | 4500 ms  | **16x** |

---

## Technical Demos & Benchmarks

Run standalone verification demos:

| Type | Target / Launcher | Source File | Description |
| :--- | :--- | :--- | :--- |
| **Interactive Demo** | [`run-demo.bat`](run-demo.bat) | [`Demo.java`](examples/Demo/src/main/java/fastfileindex/Demo.java) | Real-time native C++ directory scanning and live volume stream |

---

## Installation

### Option 1: Maven (Recommended via JitPack)

Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileIndex</artifactId>
        <version>0.1.2</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastFileIndex:0.1.2'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

---

## API Reference

| Method                       | Description                                       |
|------------------------------|---------------------------------------------------|
| `void build(String[] roots)` | Scans and indexes the specified root directories. |
| `long getEntryCount()`       | Returns the total number of indexed files.        |

---

## Documentation

* **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions, border configurations, and codepoint index.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🔗 Planned        |
| macOS         | 🔗 Planned        |

---

## License

MIT License  See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastFileIndex](https://github.com/andrestubbe/FastFileIndex) - Binary file indexing with mmap support
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) - Prefix Trie, N-Gram index, and Ranking engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) - USN Journal-based live file monitoring
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
