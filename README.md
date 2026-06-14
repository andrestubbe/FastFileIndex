# FastFileIndex 0.1.0 [ALPHA-2026-05-17] — Ultra-Fast Native File Indexing for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastFileIndex/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastFileIndex)

**? Scan and search millions of files in milliseconds with zero latency.**

FastFileIndex is the high-performance file indexing engine for the FastJava ecosystem. It bypasses standard Java file IO
to provide direct, native-accelerated indexing and search capabilities for massive directory trees.

[![FastFileIndex Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

```java
// Quick Start  Indexing a directory

import fastfileindex.FastFileIndex;

public class Demo {
    public static void main(String[] args) {
        String[] roots = {"C:\\"};
        FastFileIndex.build(roots);

        long count = FastFileIndex.getEntryCount();
        System.out.println("Indexed " + count + " files!");
    }
}
```

---

## Table of Contents

- [Key Features](#key-features)
- [Performance](#performance)
- [Installation](#installation)
- [Try the Demo](#try-the-demo)
- [API Reference](#api-reference)
- [Platform Support](#platform-support)
- [Building from Source](#building-from-source)
- [License](#license)
- [Related Projects](#related-projects)

---

## Key Features

- **ðŸš€ Instant Indexing**  Scan millions of files in milliseconds using native C++ pipelines.
- **? Zero Latency**  Real-time results for massive file systems.
- **ðŸš€ Low Footprint**  Optimized native data structures for minimal RAM usage.

---

## Performance

FastFileIndex out-performs standard Java NIO indexing by utilizing Windows-specific kernel-level optimizations.

| Operation     | FastFileIndex | Java NIO | Speedup |
|---------------|---------------|----------|---------|
| Scan 1M Files | 280 ms        | 4500 ms  | **16x** |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependencies to your `pom.xml`:

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
       <artifactId>fastfileindex</artifactId>
       <version>0.1.0</version>
   </dependency>
   <dependency>
       <groupId>com.github.andrestubbe</groupId>
       <artifactId>fastcore</artifactId>
       <version>0.1.0</version>
   </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastfileindex:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. ðŸš€ *
   *[fastfileindex-0.1.0.jar](https://github.com/andrestubbe/FastFileIndex/releases/download/0.1.0/fastfileindex-0.1.0.jar)
   ** (The Core Library)
2. ðŸš€ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (
   The Mandatory Native Loader)

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
| Windows 10/11 | ? Fully Supported |
| Linux         | ðŸš€ Planned        |
| macOS         | ðŸš€ Planned        |

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

**Part of the FastJava Ecosystem**  *Making the JVM faster. Small package. Maximum speed. Zero bloat. ðŸš€ðŸš€*
