project(GeographicLibJni)

cmake_minimum_required(VERSION 2.4.0)

# verbose makefile
set(CMAKE_VERBOSE_MAKEFILE ON CACHE BOOL "Verbose" FORCE)
# debug build (-g) includes symbols
set(CMAKE_BUILD_TYPE Debug)
# warn all and -fPIC for shared build
set(CMAKE_CXX_FLAGS "-Wall -fPIC -fno-strict-aliasing")

# include directories, $ENV{JAVA_HOME} reads the JAVA_HOME environment variable
# from the users shell. JAVA_HOME contains the include/ and lib/ directories
# needed to compile and link against JNI.
include_directories(
  ${CMAKE_CURRENT_LIST_DIR}/include
  "/usr/local/include"
  "/usr/include"
  "~/downloads/GeographicLib-1.29/include")

link_directories(
  ${CMAKE_CURRENT_BINARY_DIR}
  "/usr/local/lib64"
  "/usr/lib64"
  "/lib64"
  "/usr/local/lib"
  "/usr/lib"
  "/lib"
  "~/downloads/GeographicLib-1.29/build/src")

# Find the Java Native Interfaces (JNI) package and add it to the include and
# link directories
find_package(JNI)
if (JNI_FOUND)  
  include_directories(${JNI_INCLUDE_DIRS})
  link_directories(${JNI_LIBRARIES})
endif (JNI_FOUND)

# search the src/ directory for all files ending in .cpp
file(GLOB SOURCES ${CMAKE_CURRENT_LIST_DIR}/src/*.cpp)
# search the include/GeographicLib/jni/ directory for all files ending in .h
file(GLOB HEADERS ${CMAKE_CURRENT_LIST_DIR}/include/GeographicLib/jni/*.h)

# debug print to make sure we globbed the correct source/headers
message("sources: ${SOURCES}")
message("headers: ${HEADERS}")

# define the GeographicJni library and specify whether it is shared or not.
add_library(GeographicJni SHARED ${SOURCES} ${HEADERS})

# link the library against GeographicLib
target_link_libraries(GeographicJni Geographic)

