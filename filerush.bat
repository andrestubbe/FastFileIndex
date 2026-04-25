@echo off
cd build
java -cp ..\target\fastfileindex-v1.0.0.jar;..\examples\FileRush\src\main\java fastfileindex.FileRush test-index.idx
