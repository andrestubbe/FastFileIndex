@echo off
chcp 65001 >nul

echo 🚀 Running Demo (JitPack-only)...
cd examples\Demo
set "FASTFILEINDEX_MAVEN_REPO=%TEMP%\FastFileIndex-jitpack-m2"
call mvn -U -Dmaven.repo.local="%FASTFILEINDEX_MAVEN_REPO%" compile exec:java -Dexec.mainClass=fastfileindex.Demo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Demo failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
pause