#!/usr/bin/env bash
set -e

# Determine current platform
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    PLATFORM="linux"
    echo "Building for Linux"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OSTYPE" == "cygwin" ]] then
    PLATFORM="win32"
    # Add MinGW to PATH if on Windows
    export PATH="/mingw64/bin:$PATH"
    echo "Building for Windows (MinGW)"
else
    echo "Unsupported platform: $OSTYPE"
    exit 1
fi

# Verify CMake is available
if ! command -v cmake &> /dev/null; then
    echo "CMake not found. Please ensure it's installed and in your PATH."
    exit 1
fi

# Change to native directory
cd native || { echo "Failed to enter native directory"; exit 1; }

# Create build directory
build_dir="build"
mkdir -p "$build_dir"

# Configure and build
if [[ "$PLATFORM" == "win32" ]]; then
    cmake -B "$build_dir" -G "MinGW Makefiles" \
        -DCMAKE_C_COMPILER=gcc \
        -DCMAKE_MAKE_PROGRAM=mingw32-make \
        -S .
else
    cmake -B "$build_dir" -S .
fi

cmake --build "$build_dir"

echo "Build completed for $PLATFORM"