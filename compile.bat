@echo off
call "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat"
cl.exe /LD /EHsc /Fe:build\fastfileindex.dll native\FastFileIndex.cpp /I"C:\Program Files\Java\jdk-25\include" /I"C:\Program Files\Java\jdk-25\include\win32" /std:c++17 /DEF:native\FastFileIndex.def
copy build\fastfileindex.dll src\main\resources\native\
echo Compilation complete
