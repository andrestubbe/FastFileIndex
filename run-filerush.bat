@echo off
echo === FastFileIndex FileRush ===
echo.

REM Compile FileRush
echo Compiling FileRush...
javac -cp target\fastfileindex-v1.0.0.jar examples\FileRush\src\main\java\fastfileindex\FileRush.java -d examples\FileRush\src\main\java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Run FileRush from build directory
echo.
echo Running FileRush (real-time C: drive scan)...
cd build
java --enable-native-access=ALL-UNNAMED -cp ..\target\fastfileindex-v1.0.0.jar;..\examples\FileRush\src\main\java fastfileindex.FileRush
cd ..

echo.
echo === FileRush Complete ===
pause
