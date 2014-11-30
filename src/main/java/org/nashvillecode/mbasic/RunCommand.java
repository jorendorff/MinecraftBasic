package org.nashvillecode.mbasic;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class RunCommand extends CommandBase {
    public String getCommandName()
    {
        return "run";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
    	if (!(sender instanceof EntityPlayer)) {
            sender.sendChatToPlayer(
            	ChatMessageComponent.createFromText("only players can use this command")
            		.setColor(EnumChatFormatting.GREEN));
            return;
    	}

    	if (args.length != 1) {
    		sender.sendChatToPlayer(
                ChatMessageComponent.createFromText("/run requires one argument, the filename of the BASIC program on the server")
            		.setColor(EnumChatFormatting.GREEN));
            return;
    	}
    	File home = new File(System.getProperties().getProperty("user.home"));
		File file = new File(home, args[0]);

    	BasicStatement program;
    	try {
    		program = BasicParser.parseProgram(new BasicLineReader(file));
    	} catch (Exception exc) {
    		sender.sendChatToPlayer(
    			ChatMessageComponent.createFromText("/run failed to load the program: " + exc.getMessage())
    				.setColor(EnumChatFormatting.GREEN));
    		return;
    	}

    	try {
    		BasicExecutionContext context = new BasicExecutionContext((EntityPlayer) sender);
    		program.execute(context);
    	} catch (RuntimeException exc) {
    		sender.sendChatToPlayer(
    				ChatMessageComponent.createFromText("Error while running the program: " + exc.getMessage())
    				.setColor(EnumChatFormatting.GREEN));
    	}

		sender.sendChatToPlayer(
    			ChatMessageComponent.createFromText("Program executed.")
    				.setColor(EnumChatFormatting.GREEN));
    }

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/run <filename> - Runs the given BASIC program on the server. (The file must be on the server, not the client.)";
	}
}

