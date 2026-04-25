# FastFileIndex v1.0.0 — Binary File Indexing with mmap Support

## 🚀 What's New
- Initial release of FastFileIndex
- Memory-mapped I/O for instant index loading (<1-3 ms)
- FNV-1a hashing for stable file IDs
- Type detection (images, PDFs, text, code, video, audio, archives)
- Path-Blob storage for zero-copy access
- Parallel filesystem scanning
- Visual progress bar for indexing operations
- FileRush demo for rapid file display
- FastCore Integration — Unified JNI loader
- Native DLL bundled — fastfileindex.dll included in JAR
- Maven/JitPack support

## 📦 Installation

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

### Direct Download
- fastfileindex-v1.0.0.jar — Main library with DLL
- fastcore-v1.0.0.jar — JNI loader (required)
→ [Download from FastCore releases](https://github.com/andrestubbe/FastCore/releases)

```
fastfileindex-v1.0.0.jar
```

```
fastcore-v1.0.0.jar
```

## ⚡ Quick Start
```java
import fastfileindex.FastFileIndex;

public class Example {
    public static void main(String[] args) {
        String[] roots = { "C:\\Users\\YourName\\Documents" };
        FastFileIndex.build(roots);
        FastFileIndex.save("files.idx");
        FastFileIndex.load("files.idx");
        long count = FastFileIndex.getEntryCount();
        System.out.println("Indexed files: " + count);
    }
}
```

## ✨ Key Features
- **mmap-based loading** — CreateFileMapping + MapViewOfFile for zero-copy access
- **Path-Blob storage** — All paths in single contiguous blob for better cache behavior
- **Parallel build** — Threadpool with lock-free append buffer
- **FNV-1a hashing** — Stable file IDs based on path hash
- **Type detection** — Automatic detection of common file types
- **Unix timestamps** — Converts file timestamps to Unix seconds
- **Append-only format** — No fragmentation, no locks needed
- **Instant loading** — Loads in <1-3 ms with memory-mapped I/O
- **Zero-copy access** — Path access via string_view without copying

## 📁 Assets
- fastfileindex-v1.0.0.jar — FatJAR with all dependencies and native DLL
