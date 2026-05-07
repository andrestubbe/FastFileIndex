@echo off
echo === FastFileIndex Benchmark ===
echo.
echo Building FastFileIndex...
call mvn clean package -f pom.xml
echo.
echo Building Benchmark...
cd examples\Benchmark
call mvn clean package -f pom.xml
echo.
echo Running Benchmark...
java --enable-native-access=ALL-UNNAMED -cp "target\fastfileindex-benchmark-v1.0.0.jar;..\..\target\fastfileindex-v1.0.0.jar" fastfileindex.Benchmark
cd ..\..
echo.
echo === Benchmark Complete ===
pause
