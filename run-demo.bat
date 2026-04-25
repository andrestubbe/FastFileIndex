@echo off
javac -cp target\fastfileindex-v1.0.0.jar examples\Demo\src\main\java\fastfileindex\Demo.java -d examples\Demo\src\main\java
cd build
java --enable-native-access=ALL-UNNAMED -cp ..\target\fastfileindex-v1.0.0.jar;..\examples\Demo\src\main\java fastfileindex.Demo
