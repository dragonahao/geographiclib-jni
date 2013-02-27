cmake_minimum_required(VERSION 2.6)

project(GeographicLibJni)

# verbose makefile
set(CMAKE_VERBOSE_MAKEFILE ON CACHE BOOL "Verbose" FORCE)
# debug build (-g) includes symbols
set(CMAKE_BUILD_TYPE Debug)
# warn all and -fPIC for shared build
set(CMAKE_CXX_FLAGS "-Wall -fPIC -fno-strict-aliasing")
# place the GeographicLibJni.so in the lib/ directory by default
set(LIBRARY_OUTPUT_PATH ${CMAKE_CURRENT_LIST_DIR}/lib)

###############################
#        Find Packages        #
###############################

# Find GeographicLib
find_package(GeographicLib)
if (GEOGRAPHICLIB_FOUND)
  include_directories(${GeographicLib_INCLUDE_DIRS})
  link_directories(${GeographicLib_LIBRARY_DIRS})
endif (GEOGRAPHICLIB_FOUND)

# Find the Java Native Interfaces (JNI) package and add it to the include and
# link directories
find_package(JNI)
if (JNI_FOUND)  
  include_directories(${JNI_INCLUDE_DIRS})
  link_directories(${JNI_LIBRARIES})
endif (JNI_FOUND)

########################################
# GeographicLibJni Sources and Headers #
########################################

# CMAKE_CURRENT_LIST_DIR is the directory that contains the CMakeLists.txt file
# which in our case is the root directory of the project so add include/ to
# the include directories
include_directories(${CMAKE_CURRENT_LIST_DIR}/include)

# search the src/ directory for all files ending in .cpp
file(GLOB SOURCES ${CMAKE_CURRENT_LIST_DIR}/src/*.cpp)
# search the include/GeographicLib/jni/ directory for all files ending in .h
file(GLOB HEADERS ${CMAKE_CURRENT_LIST_DIR}/include/GeographicLib/jni/*.h)

# debug print to make sure we globbed the correct source/headers
message("GeographicLibJni Sources: ${SOURCES}")
message("GeographicLibJni Headers: ${HEADERS}")

# define the GeographicJni library and specify whether it is shared or not.
add_library(GeographicJni SHARED ${SOURCES} ${HEADERS})

# link the library against GeographicLib
target_link_libraries(GeographicJni Geographic)

##################
# Install Target #
##################
# useage: make install

# install headers
install (FILES ${HEADERS} DESTINATION include/GeographicLib/jni)

# install library file
install(TARGETS GeographicJni
  RUNTIME DESTINATION bin
  LIBRARY DESTINATION lib${LIB_SUFFIX}
  ARCHIVE DESTINATION lib${LIB_SUFFIX}
)

####################
# Uninstall target #
####################
# useage: make uninstall

# uninstalls everything that install installed
configure_file(
    "${CMAKE_CURRENT_SOURCE_DIR}/cmake/cmake_uninstall.cmake.in"
    "${CMAKE_CURRENT_BINARY_DIR}/cmake_uninstall.cmake"
    IMMEDIATE @ONLY)

add_custom_target(uninstall
    COMMAND ${CMAKE_COMMAND} -P ${CMAKE_CURRENT_BINARY_DIR}/cmake_uninstall.cmake)