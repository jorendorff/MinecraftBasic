#!/usr/bin/env bash

set -eu

VERSION=1.7.10-10.13.2.1230

# Extract gradle stuff from the zip file.
# We don't need all the files, just gradle.
FORGE_SRC_ZIP_FILENAME=forge-$VERSION-src.zip
mkdir tmp-forge-src
cd tmp-forge-src
unzip ../vendor/${FORGE_SRC_ZIP_FILENAME}
cd ..
mv tmp-forge-src/gradlew tmp-forge-src/gradle .
rm -rf tmp-forge-src

echo
echo "Gradle installed!"
echo "To see what gradle can do, type:     ./gradlew tasks"
echo "In particular you'll find these useful:"
echo "    ./gradlew jar         # Build the mod"
echo "    ./gradlew runClient   # Run Minecraft $VERSION with the mod"

## gradlew, optionally gradlew.bat, build.gradle, and gradle/**.



## # Copy some random version of gradle from the MinecraftForger source zip into
## # this directory. There is no guarantee this is a recent version, so install
## # your own gradle if you can.
## cp forge-src/gradlew .
## mkdir -p gradle/wrapper
## cp forge-src/gradle/wrapper/gradle-wrapper.jar forge-src/gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
## 
## # Copy the gradle build file.
## cp forge-src/build.gradle .

## mkdir tmp
## cd tmp
## wget http://files.minecraftforge.net/maven/net/minecraftforge/forge/$VERSION/forge-$VERSION-installer.jar
## java -jar forge-$VERSION-installer.jar
# click server, install
# it'll say files are already installed or something
# i dunno, maybe that's ok
# could also try --installServer which is *alleged* to install to the current working directory.
# i don't know if any of the above is even necessary


