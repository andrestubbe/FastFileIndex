@echo off
cd build
java --enable-native-access=ALL-UNNAMED -cp ..\target\fastfileindex-v1.0.0.jar;..\examples\FileRush\src\main\java fastfileindex.FileRush
