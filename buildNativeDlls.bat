@echo off
setlocal

:: Path to MSYS2 bash
set "MSYS2_BASH=C:\msys64\usr\bin\bash.exe"

:: Get directory of this script, remove trailing backslash
set "LOCAL_DIR=%~dp0"
set "LOCAL_DIR=%LOCAL_DIR:~0,-1%"

:: Convert to MSYS2-friendly path
set "LOCAL_DIR_UNIX=%LOCAL_DIR:\=/%"
set "LOCAL_DIR_UNIX=/%LOCAL_DIR_UNIX:~0,1%%LOCAL_DIR_UNIX:~2%"

:: Check if bash exists
if not exist "%MSYS2_BASH%" (
    echo Error: %MSYS2_BASH% not found
    endlocal
    exit /b 1
)

:: Run build script inside MSYS2 bash
"%MSYS2_BASH%" -l -c "cd \"%LOCAL_DIR_UNIX%\" && ./buildNativeDlls.sh"

endlocal
exit /b 0
