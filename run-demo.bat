@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo [FastFileIndex] Running Demo (via JitPack)...
echo 🚀 Running Demo...
cd examples\Demo
set "FASTFILEINDEX_MAVEN_REPO=%TEMP%\FastFileIndex-jitpack-m2"
call mvn -U -Dmaven.repo.local="%FASTFILEINDEX_MAVEN_REPO%" compile exec:java -Dexec.mainClass=fastfileindex.Demo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Demo failed. & pause & exit /b %ERRORLEVEL% )

echo Using local Maven repository: %FASTFILEINDEX_MAVEN_REPO%
cd ..\..
pause
