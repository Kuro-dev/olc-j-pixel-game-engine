#!/usr/bin/env bash
set -e

export PATH="/mingw64/bin:$PATH"
cd native

cmake -B build -G "MinGW Makefiles" \
  -DCMAKE_C_COMPILER=gcc \
  -DCMAKE_CXX_COMPILER=g++ \
  -DCMAKE_MAKE_PROGRAM=mingw32-make

cmake --build build
