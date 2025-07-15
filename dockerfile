# Linux build with GCC 15.1.0
FROM gcc:15.1.0 AS linux-builder

RUN apt-get update && apt-get install -y \
    libx11-dev libgl1-mesa-dev libglu1-mesa-dev libxrandr-dev \
    libxi-dev libxcursor-dev libxinerama-dev libxext-dev libpthread-stubs0-dev

WORKDIR /src
COPY ./native/ .

RUN g++ -std=c++17 -fPIC -DOLC_PGE_APPLICATION -shared \
    -o liborg_kurodev_pixelGameEngineFFM.so org_kurodev_pixelGameEngineFFM.cpp \
    -lX11 -lGL -lpthread -ldl


# MSYS2 + MinGW-w64
FROM mdashnet/mingw:latest AS windows-builder

WORKDIR /src
COPY ./native/ .

RUN x86_64-w64-mingw32-g++ -std=c++17 -DOLC_PGE_APPLICATION -shared \
    -o org_kurodev_pixelGameEngineFFM.dll org_kurodev_pixelGameEngineFFM.cpp \
    -static-libstdc++ -static-libgcc -lopengl32 -lwinmm -lgdi32 -pthread \
    -lopengl32 -lwinmm -lgdi32 -ldwmapi -lgdiplus -lshlwapi -pthread


# Export stage: gather all artifacts
FROM scratch AS export-stage

COPY --from=linux-builder /src/liborg_kurodev_pixelGameEngineFFM.so ./lib/
COPY --from=windows-builder /src/org_kurodev_pixelGameEngineFFM.dll ./lib/
