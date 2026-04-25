@echo off
echo === FastFileIndex Demo ===
echo.

REM Compile Demo
echo Compiling Demo...
javac -cp target\fastfileindex-v1.0.0.jar examples\Demo\src\main\java\fastfileindex\Demo.java -d examples\Demo\src\main\java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Run Demo from build directory
echo.
echo Running Demo...
cd build
java -cp ..\target\fastfileindex-v1.0.0.jar;..\examples\Demo\src\main\java fastfileindex.Demo
cd ..

echo.
echo === Demo Complete ===
pause
