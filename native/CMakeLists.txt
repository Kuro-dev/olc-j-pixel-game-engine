cmake_minimum_required(VERSION 3.21)
project(jni_example LANGUAGES C)

# Use explicitly specified platform/arch or detect
if(NOT DEFINED PLATFORM)
    if(WIN32)
        set(PLATFORM "win32")
    elseif(APPLE)
        set(PLATFORM "darwin")
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

# Find all source files
file(GLOB NATIVE_SOURCES "org_kurodev_*.c")

# Output directory
set(JAVA_RESOURCES_DIR "../src/main/resources/${PLATFORM}-${ARCH}")
file(MAKE_DIRECTORY "${JAVA_RESOURCES_DIR}")

# Platform-specific library settings
if(PLATFORM STREQUAL "win32")
    set(LIB_PREFIX "")
    set(LIB_SUFFIX ".dll")
elseif(PLATFORM STREQUAL "darwin")
    set(LIB_PREFIX "lib")
    set(LIB_SUFFIX ".dylib")
else() # Linux
    set(LIB_PREFIX "lib")
    set(LIB_SUFFIX ".so")
endif()

# For each source file, create a separate library
foreach(SRC_FILE ${NATIVE_SOURCES})
    get_filename_component(LIB_NAME ${SRC_FILE} NAME_WE)

    add_library(${LIB_NAME} SHARED ${SRC_FILE})

    set_target_properties(${LIB_NAME} PROPERTIES
        OUTPUT_NAME "${LIB_PREFIX}${LIB_NAME}"
        PREFIX ""
        SUFFIX "${LIB_SUFFIX}"
        LIBRARY_OUTPUT_DIRECTORY "${JAVA_RESOURCES_DIR}"
        RUNTIME_OUTPUT_DIRECTORY "${JAVA_RESOURCES_DIR}"
    )

    # Link against JVM if needed
    target_link_libraries(${LIB_NAME} ${JNI_LIBRARIES})
endforeach()