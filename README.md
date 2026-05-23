# FastFileIndex — Ultra-Fast Native File Indexing for Java

**Scan and search millions of files in milliseconds with zero latency.**

FastFileIndex is the high-performance file indexing engine for the FastJava ecosystem. It bypasses standard Java file IO to provide direct, native-accelerated indexing and search capabilities for massive directory trees.

```java
// Quick Start — Indexing a directory
import fastfileindex.FastFileIndex;

public class Demo {
    public static void main(String[] args) {
        String[] roots = { "C:\\" };
        FastFileIndex.build(roots);
        
        long count = FastFileIndex.getEntryCount();
        System.out.println("Indexed " + count + " files!");
    }
}
```

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastFileIndex/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

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

- **🚀 Instant Indexing** — Scan millions of files in milliseconds using native C++ pipelines.
- **⚡ Zero Latency** — Real-time results for massive file systems.
- **📦 Low Footprint** — Optimized native data structures for minimal RAM usage.

---

## Performance

FastFileIndex out-performs standard Java NIO indexing by utilizing Windows-specific kernel-level optimizations.

| Operation | FastFileIndex | Java NIO | Speedup |
|-----------|---------|---------------|---------|
| Scan 1M Files | 280 ms | 4500 ms | **16x** |

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
    <!-- FastFileIndex Library -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastfileindex</artifactId>
        <version>v0.1.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>v0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastfileindex:v0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. 📦 **[fastfileindex-v0.1.0.jar](https://github.com/andrestubbe/FastFileIndex/releases/download/v0.1.0/fastfileindex-v0.1.0.jar)** (The Core Library)
2. ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/v0.1.0/fastcore-v0.1.0.jar)** (The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.


## API Reference

| Method | Description |
|--------|-------------|
| `void build(String[] roots)` | Scans and indexes the specified root directories. |
| `long getEntryCount()` | Returns the total number of indexed files. |

---

## Platform Support

| Platform | Status |
|----------|--------|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux | 🚧 Planned |

---

## Building from Source

For detailed instructions on compiling the C++ JNI code, see [COMPILE.md](COMPILE.md).

---

## License
MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects
- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader for Java
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) — Real-time fuzzy search engine
- [FastThumb](https://github.com/andrestubbe/FastThumb) — Native Shell Image Engine

---
**Made with ⚡ by Andre Stubbe**

<!-- 
SEO Keywords: java, jni, indexing, file system, windows api, performance
-->
