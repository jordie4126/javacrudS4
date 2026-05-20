@echo off
setlocal

set "ROOT_DIR=%~dp0"
cd /d "%ROOT_DIR%"

set "OUT_DIR=%ROOT_DIR%out"
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

rem Compile all sources
dir /s /b "%ROOT_DIR%src\*.java" > "%OUT_DIR%\sources.txt"
javac -d "%OUT_DIR%" -cp "%ROOT_DIR%lib\*" @"%OUT_DIR%\sources.txt"
if errorlevel 1 exit /b 1

rem Run the app
java -cp "%OUT_DIR%;%ROOT_DIR%lib\*" Main

endlocal
