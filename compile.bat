@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.12.1
call "C:\Program Files\Microsoft Visual Studio\18\Community\VC\Auxiliary\Build\vcvars64.bat"
if not exist build mkdir build
cl.exe /LD /EHsc /O2 /Fe:build\fastfileindex.dll native\FastFileIndex.cpp /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" /std:c++17 /DEF:native\FastFileIndex.def
if not exist src\main\resources\native mkdir src\main\resources\native
copy /y build\fastfileindex.dll src\main\resources\native\
echo Compilation complete