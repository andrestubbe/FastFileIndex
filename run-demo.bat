@echo off
echo === FastFileIndex Demo ===
echo.
echo Building FastFileIndex...
call mvn clean package -f pom.xml
echo.
echo Running Demo...
java --enable-native-access=ALL-UNNAMED -cp "target\fastfileindex-v1.0.0.jar" fastfileindex.Demo
echo.
echo === Demo Complete ===
pause
