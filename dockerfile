FROM maven:3.9.6-eclipse-temurin-21 AS maven-version
WORKDIR /project
COPY pom.xml ./
RUN mvn help:evaluate -Dexpression=project.version -q -DforceStdout > version.txt

FROM gcc:15.1.0 AS linux-builder

COPY --from=maven-version /project/version.txt /tmp/version.txt
RUN apt-get update && apt-get install -y \
    libx11-dev libgl1-mesa-dev libglu1-mesa-dev libxrandr-dev \
    libxi-dev libxcursor-dev libxinerama-dev libxext-dev libpthread-stubs0-dev \
    libpng-dev

WORKDIR /src
COPY ./native/ .

RUN VERSION=$(cat /tmp/version.txt) && \
    g++ -std=c++17 -fPIC -DOLC_PGE_APPLICATION -DVERSION="\"$VERSION\"" -shared \
    -o liborg_kurodev_pixelGameEngineFFM.so org_kurodev_pixelGameEngineFFM.cpp \
    -lX11 -lGL -lpng -lpthread -ldl

FROM mdashnet/mingw:latest AS windows-builder

COPY --from=maven-version /project/version.txt /tmp/version.txt

WORKDIR /src
COPY ./native/ .

RUN VERSION=$(cat /tmp/version.txt) && \
    x86_64-w64-mingw32-g++ -std=c++17 -DOLC_PGE_APPLICATION -DVERSION="\"$VERSION\"" -shared \
    -o org_kurodev_pixelGameEngineFFM.dll org_kurodev_pixelGameEngineFFM.cpp \
    -static-libstdc++ -static-libgcc -lopengl32 -lwinmm -lgdi32 -ldwmapi -lgdiplus -lshlwapi -pthread

FROM scratch AS export-stage

COPY --from=linux-builder /src/liborg_kurodev_pixelGameEngineFFM.so ./lib/
COPY --from=windows-builder /src/org_kurodev_pixelGameEngineFFM.dll ./lib/
