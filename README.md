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

    The first time you run this, it'll download a ton of software over HTTP
    that will later run on your computer.
    Sorry, that's how the Java ecosystem is.

5.  `./gradlew runClient` to run Minecraft with the mod installed.

6.  If you're planning on hacking on this module, you might want to do
    `./gradlew setupDecompWorkspace`, which
    decompiles Minecraft to Java sources,
    and also unzips Minecraft Forge sources from somewhere,
    and puts them all into `./build/tmp/recompSrc`.

Those last two steps are pretty speculative as I don't have things all set up yet.
