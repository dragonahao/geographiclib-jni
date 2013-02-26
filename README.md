geographiclib-jni
=================

A Java interface for GeographicLib (http://geographiclib.sourceforge.net/) implemented by wrapping the GeographicLib calls using JNI.

####################################################
# Folder Strucutre                                 #
####################################################
- CMakeListst.txt : The CMake file for building the C++ JNI library 
                    (doesn't build the java JARs)
- include/ : contains all header files for the JNI implementations
- src/ : contains all source files for the JNI implementations
- java/ : contains the java source and build files.
  - build.xml : the Ant build script
  - build.properties : file that contains some basic properties about the java build
  - src/ : contains the java source files for the java wrappers

####################################################
# Building                                         #
####################################################

// Either execute the following script
$ ./test_java_jni.sh

// OR follow the build process below

###############
# C++ library #
###############

// I prefer putting all my cmake files inside build/, this is simply preference
$ mkdir build; cd build
$ cmake ../
$ make

// libGeographicJni.so will now live inside build/

//////////////
// Java JAR //
//////////////

$ cd java/
$ ant jar

// 3 JARs have been created and placed into java/dist
// - GeographicLib-1.29.jar: Contains the compiled java binary classes
//                           which wrap GeographicLib classes/functions
// - GeographicLib-1.29-src.jar: Contains the .java source files for GeographicLib
// - GeographicLib-1.29-doc.jar: Contains the JavaDoc html files generated for
//                               the source files in the java source tree.
//                               Documentation can be viewed by opening doc/index.html

/////////////////////////////////
// Executing Java Test Program //
/////////////////////////////////

// Ensure that libGeographicJni.so is placed somewhere in your LD_LIBRARY_PATH
$ export LD_LIBRARY_PATH=~/geographiclib-jni/build:$LD_LIBRARY_PATH
$ cd java/
// execute the test program (net.sf.geographiclib.Test.java)
$ ant test
