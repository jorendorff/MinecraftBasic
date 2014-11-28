#!/usr/bin/env bash

set -eu

VERSION=1.7.10-10.13.2.1230
FILENAME=forge-$VERSION-src.zip
wget http://files.minecraftforge.net/maven/net/minecraftforge/forge/$VERSION/$FILENAME
mkdir forge-src
cd forge-src
unzip ../$FILENAME
./gradlew setupDevWorkspace
./gradlew setupDecompWorkspace

