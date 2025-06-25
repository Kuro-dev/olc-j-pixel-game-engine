@echo off
setlocal

set "CURRENT_DIRECTORY=%~dp0"
set "PROJECT_ROOT=%CURRENT_DIRECTORY%/src/main/java"
set "TARGET_CLASS=%PROJECT_ROOT%/org/kurodev/jpixelgameengine/impl/PixelGameEngineNativeImpl.java"
set "TARGET_COMPILE_OUT=%CURRENT_DIRECTORY%/target/classes/javac"

echo Native class: %TARGET_CLASS%
echo compile path: %TARGET_COMPILE_OUT%

if not exist "%TARGET_COMPILE_OUT%" (
    echo Creating output directory...
    mkdir "%TARGET_COMPILE_OUT%"
)

if not exist "%TARGET_CLASS%" (
    echo Error: Source file not found at:
    echo %TARGET_CLASS%
    echo Directory contents:
    dir "%PROJECT_ROOT%/org/kurodev/jpixelgameengine/"
    exit /b 1
)

 javac -h native -sourcepath %PROJECT_ROOT% -cp %TARGET_CLASS% -d %TARGET_COMPILE_OUT% %TARGET_CLASS%

endlocal