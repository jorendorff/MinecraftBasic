package org.nashvillecode.mbasic;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = BasicMod.MODID, version = BasicMod.VERSION)
public class BasicMod
{
    public static final String MODID = "Minecraft BASIC";
    public static final String VERSION = "0.0";

    @EventHandler
    public void init(FMLServerStartingEvent event)
    {
        System.out.println(MODID + " v" + VERSION + " installed!");
        event.registerServerCommand(new RunCommand());
    }
}
