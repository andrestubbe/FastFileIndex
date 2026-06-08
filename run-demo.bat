@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo [FastFileIndex] Running Demo (via JitPack)...
echo 🚀 Running Demo...
cd examples\Demo
call mvn -U compile exec:java -Dexec.mainClass=fastfileindex.Demo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Demo failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
pause
