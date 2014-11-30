# Minecraft BASIC

## Getting started

I don't know enough about Minecraft modding yet to be able to explain this well.
But here's what you need to do to get started.

1.  Make sure Java is installed.
    Minecraft and Minecraft mods are written in Java.

    Mojang wants you to run Minecraft with the Oracle JDK,
    but OpenJDK has worked for me so far.

2.  Clone this repository.

3.  `cd` into this directory and run `./setup.sh`.
    (This is not doing anything scary to your computer&mdash;it just
    extracts a few files from `vendor/forge-$VERSION-src.zip`,
    which I got from [files.minecraftforge.net](http://files.minecraftforge.net/),
    to the current directory.)

4.  `./gradlew jar` to build the mod.

5.  `./gradlew runClient` to run Minecraft with the mod installed.

Those last two steps are pretty speculative as I don't have things all set up yet.
