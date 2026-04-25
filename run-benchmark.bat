@echo off
javac -cp target\fastfileindex-v1.0.0.jar examples\Benchmark\src\main\java\fastfileindex\Benchmark.java -d examples\Benchmark\src\main\java
cd build
java --enable-native-access=ALL-UNNAMED -cp ..\target\fastfileindex-v1.0.0.jar;..\examples\Benchmark\src\main\java fastfileindex.Benchmark
