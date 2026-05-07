# FastFileIndex — Ultra-Fast Native File Indexing for Java [v0.1.0]

**The high-performance file indexing engine for the FastJava ecosystem. Scan and search millions of files in milliseconds with zero latency.**

[![Status](https://img.shields.io/badge/status-v0.1.0--alpha-orange.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

**FastFileIndex** is built for raw speed. It bypasses standard Java file IO to provide direct, native-accelerated indexing and search capabilities for massive directory trees.

## Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Build from Source](#build-from-source)
- [License](#license)

## Features
- **⚡ Instant Indexing**: Scan millions of files in milliseconds using native C++ pipelines.
- **🔎 Zero-Latency Search**: Real-time results for massive file systems.
- **📦 Low Memory Footprint**: Optimized native data structures for indexing.
- **🚀 Raw Performance**: Built for automation, agents, and high-speed system tools.

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
        <groupId>io.github.andrestubbe</groupId>
        <artifactId>fastfileindex</artifactId>
        <version>0.1.0</version>
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
    implementation 'io.github.andrestubbe:fastfileindex:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1.  📦 **[fastfileindex-v0.1.0.jar](https://github.com/andrestubbe/fastfileindex/releases)** (The Core Library)
2.  ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases)** (The Mandatory Native Loader)

> [!IMPORTANT]
> Both JARs must be in your classpath for the native JNI calls to function correctly.

---

## License
MIT License — See [LICENSE](LICENSE) for details.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*
