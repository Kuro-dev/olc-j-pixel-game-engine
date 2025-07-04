cmake_minimum_required(VERSION 3.21)
project(jni_example LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
add_compile_definitions(OLC_PGE_APPLICATION)
set(CMAKE_VERBOSE_MAKEFILE ON)

# Platform detection
if(NOT DEFINED PLATFORM)
    if(WIN32)
        set(PLATFORM "win32")
    else()
        set(PLATFORM "linux")
    endif()
endif()

if(NOT DEFINED ARCH)
    if(CMAKE_SIZEOF_VOID_P EQUAL 8)
        if(APPLE AND CMAKE_SYSTEM_PROCESSOR MATCHES "arm64")
            set(ARCH "aarch64")
        else()
            set(ARCH "x86-64")
        endif()
    else()
        set(ARCH "x86")
    endif()
endif()

# JNI Configuration
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Specify the single source file to build
set(SRC_FILE "org_kurodev_pixelGameEngineFFM.cpp")

# Platform settings
if(PLATFORM STREQUAL "win32")
    set(LIB_PREFIX "")
    set(LIB_SUFFIX ".dll")
    set(COMPILE_TARGET_DIR "../../lib/${PLATFORM}-${ARCH}")
else()
    set(LIB_PREFIX "lib")
    set(LIB_SUFFIX ".so")
    set(COMPILE_TARGET_DIR "../../lib/${PLATFORM}-${ARCH}")
endif()

# Create output directory
file(MAKE_DIRECTORY "${COMPILE_TARGET_DIR}")

# Build the specific source file
get_filename_component(LIB_NAME ${SRC_FILE} NAME_WE)
add_library(${LIB_NAME} SHARED ${SRC_FILE})

set_target_properties(${LIB_NAME} PROPERTIES
    OUTPUT_NAME "${LIB_PREFIX}${LIB_NAME}"
    PREFIX ""
    SUFFIX "${LIB_SUFFIX}"
    LIBRARY_OUTPUT_DIRECTORY "${COMPILE_TARGET_DIR}"
    RUNTIME_OUTPUT_DIRECTORY "${COMPILE_TARGET_DIR}"
)

# Link libraries
if(WIN32)
    target_link_libraries(${LIB_NAME}
        ${JNI_LIBRARIES}
        opengl32.lib
        gdi32.lib
        gdiplus.lib
        dwmapi.lib
        shlwapi.lib
    )
else()

    target_link_libraries(${LIB_NAME}
        ${JNI_LIBRARIES}
        GL
        opengl32
        gdiplus
        shlwapi
        dwmapi
    )
endif()
