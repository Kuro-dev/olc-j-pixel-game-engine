#!/usr/bin/env bash
set -e

# Determine current platform
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    PLATFORM="linux"
    export JAVA_HOME="${HOME}/.jdks/openjdk-24.0.1"
    echo "Building for Linux"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OSTYPE" == "cygwin" ]]; then
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

# Verify compiler is available
if ! command -v gcc &> /dev/null; then
    echo "gcc not found. Please ensure it's installed and in your PATH."
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
        -DCMAKE_CXX_COMPILER=g++ \
        -DCMAKE_MAKE_PROGRAM=mingw32-make \
        -S .
else
    cmake -B "$build_dir" -S .
fi

# Actually build the project
cmake --build "$build_dir" --verbose

echo "Build completed for $PLATFORM"