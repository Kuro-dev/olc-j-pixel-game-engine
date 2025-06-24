@echo off
setlocal

:: Set path to MSYS2 bash
set "MSYS2_BASH=C:\msys64\usr\bin\bash.exe"

:: Check if bash exists
if not exist "%MSYS2_BASH%" (
    echo "Error: %MSYS2_BASH% not found"
    exit /b 1
)

:: Run CMake commands inside MinGW bash
"%MSYS2_BASH%" -l -c "cd /c/Users/Kuro/IdeaProjects/JNI-Test && ./buildNativeDlls.sh"

endlocal
