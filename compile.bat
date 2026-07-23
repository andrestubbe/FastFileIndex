@echo off
call "C:\Program Files\Microsoft Visual Studio\18\Community\VC\Auxiliary\Build\vcvars64.bat"
cl.exe /LD /EHsc /Fe:build\fastfileindex.dll native\FastFileIndex.cpp /I"C:\Program Files\Java\jdk-25.0.3\include" /I"C:\Program Files\Java\jdk-25.0.3\include\win32" /std:c++17 /DEF:native\FastFileIndex.def
copy build\fastfileindex.dll src\main\resources\native\
echo Compilation complete
