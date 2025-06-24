#!/usr/bin/env bash
set -e

# Add MinGW to PATH
export PATH="/mingw64/bin:$PATH"

# Verify CMake is available
if ! command -v cmake &> /dev/null; then
    echo "CMake not found. Please ensure it's installed and in your PATH."
    exit 1
fi

# Change to native directory
cd native || { echo "Failed to enter native directory"; exit 1; }

# Build for current platform by default
PLATFORMS=("win32" "linux" "darwin")
ARCHS=("x86-64")  # Simplified to just x86-64 for now

for platform in "${PLATFORMS[@]}"; do
    for arch in "${ARCHS[@]}"; do
        echo "Building for $platform-$arch"

        # Create separate build directory for each platform/arch
        build_dir="build-$platform-$arch"

        case "$platform" in
            win32)
                # Windows build using MinGW
                cmake -B "$build_dir" -G "MinGW Makefiles" \
                    -DCMAKE_C_COMPILER=gcc \
                    -DCMAKE_MAKE_PROGRAM=mingw32-make \
                    -DPLATFORM="$platform" -DARCH="$arch" \
                    -S .
                ;;
            linux)
                # Linux build
                cmake -B "$build_dir" \
                    -DPLATFORM="$platform" -DARCH="$arch" \
                    -S .
                ;;
            darwin)
                # macOS build (would need proper toolchain)
                echo "Skipping macOS build - requires proper toolchain setup"
                continue
                ;;
        esac

        cmake --build "$build_dir"
    done
done

# Clean up
rm -rf ../../out