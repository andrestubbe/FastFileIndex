# Building FastFileIndex from Source

## Prerequisites

- **JDK 17+** — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **Visual Studio 2022** — Community/Professional/Enterprise/BuildTools

## Quick Build

```bash
# Build native DLL first
compile.bat

# Build JAR
mvn clean package -DskipTests
```

## Build Commands

| Command | Purpose |
|---------|---------|
| `compile.bat` | Build native DLL (Windows) |
| `mvn clean compile` | Compile Java only |
| `mvn clean package` | Build JAR with DLL |
| `mvn clean package -DskipTests` | Fast build |

## Native DLL Build

The `compile.bat` script:
- Auto-detects Visual Studio 2022
- Auto-detects JAVA_HOME
- Uses `native\FastFileIndex.def` for JNI exports
- Outputs to `build\fastfileindex.dll`

## Maven Build

```bash
mvn clean package
```

This creates:
- `target/fastfileindex-0.1.1.jar` - FatJAR with all dependencies and native DLL (includes fastfileindex.dll)

## Running Examples

```bash
cd build
javac -cp ..\target\fastfileindex-0.1.1.jar ..\examples\Demo\src\main\java\fastfileindex\Demo.java -d ..\examples\Demo\src\main\java
java -cp ..\target\fastfileindex-0.1.1.jar;..\examples\Demo\src\main\java fastfileindex.Demo
```

## Troubleshooting

**"Cannot find DLL"** — Run `compile.bat` first

**"UnsatisfiedLinkError"** — Check:
1. DLL built successfully (`build\fastfileindex.dll` exists)
2. DLL included in JAR (check `pom.xml` resources)
3. JNI exports defined in `native\FastFileIndex.def`

**"Java version mismatch"** — Ensure JDK 17+ and JAVA_HOME set
