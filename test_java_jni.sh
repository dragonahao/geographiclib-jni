#! /bin/bash

# Is a useful one-liner which will give you the full directory name of the
# script no matter where it is being called from
# http://stackoverflow.com/questions/59895/can-a-bash-script-tell-what-directory-its-stored-in
DIR="$( cd "$( dirname "$0" )" && pwd )"

# Build C++ Library
mkdir $DIR/build
cd $DIR/build
cmake ..
make
cd ..

# Ensure the libGeographicJni.so is in our LD_LIBRARY_PATH
export LD_LIBRARY_PATH=$DIR/lib:$LD_LIBRARY_PATH

# Build Java
cd $DIR/java
ant jar.bin

# Run Test class
java -cp $DIR/java/dist/GeographicLib-1.29.jar net.sf.geographiclib.Test
