package org.nashvillecode.mbasic;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * The runtime environment of a running BASIC program.
 * This is where variables live. It's also the program's link to Minecraft.
 */
public class BasicExecutionContext {
	private World world;
	private int x0, y0, z0;
	private int blockID;
	private int metadata;
	private HashMap<String, BasicStatement> commandTable;
	private HashMap<String, Integer> variableTable;

	public BasicExecutionContext(EntityPlayer player) {
		world = player.worldObj;
		x0 = (int) Math.floor(player.posX);
		y0 = (int) Math.floor(player.posY);
		z0 = (int) Math.floor(player.posZ);
		blockID = Block.stone.blockID;
		commandTable = new HashMap<String, BasicStatement>();
		variableTable = new HashMap<String, Integer>();
	}

	private static final int SET_BLOCK_FLAGS = 3;
	
	public void placeBlock(int x, int z) {
		System.out.println("placing block at (" + x + ", " + z + ") blockID:" + blockID + " metadata:" + metadata);
		world.setBlock(x0 + x, y0, z0 + z, blockID, metadata, SET_BLOCK_FLAGS);
	}

	public void moveUp() {
		y0++;
	}
	
	public void define(String name, BasicStatement command) {
		commandTable.put(name, command);
	}
	
	public void run(String name) {
		BasicStatement command = commandTable.get(name);
		if (command == null) {
			// Do nothing (this is horrible but I can't face Java's checked exceptions at the moment)
		} else {
			command.execute(this);
		}
	}

	public void setBlockIDAndMetadata(int blockID, int metadata) {
		this.blockID = blockID;
		this.metadata = metadata;
	}

	public int getVariable(String name) {
		Integer value = variableTable.get(name);
		if (value == null) {
			// This should throw, but again with the checked exceptions.
			return 0;
		}
		return value.intValue();
	}

	public void setVariable(String name, int value) {
		variableTable.put(name, value);
	}
}
