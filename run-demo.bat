@echo off
echo === FileIndex Demo ===
echo.
echo Building FastFileIndex...
call mvn clean package -f pom.xml
echo.
echo Building Demo...
cd examples\Demo
call mvn clean package -f pom.xml
echo.
echo Running Demo...
java --enable-native-access=ALL-UNNAMED -cp "target\fastfileindex-demo-v1.0.0.jar;..\..\target\fastfileindex-v1.0.0.jar" fastfileindex.Demo
cd ..\..
echo.
echo === Demo Complete ===
pause
