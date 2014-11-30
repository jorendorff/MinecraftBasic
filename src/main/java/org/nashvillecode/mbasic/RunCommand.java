package org.nashvillecode.mbasic;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

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
            sender.addChatMessage(
                new ChatComponentText("only players can use this command").setChatStyle(
                    new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            return;
    	}

    	if (args.length != 1) {
            sender.addChatMessage(
                new ChatComponentText("/run requires one argument, the filename of the BASIC program on the server").setChatStyle(
                    new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            return;
    	}
    	File home = new File(System.getProperties().getProperty("user.home"));
        File file = new File(home, args[0]);

    	BasicStatement program;
    	try {
            program = BasicParser.parseProgram(new BasicLineReader(file));
    	} catch (Exception exc) {
            sender.addChatMessage(
                new ChatComponentText("/run failed to load the program: " + exc.getMessage())
                .setChatStyle(
                    new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            return;
    	}

    	try {
            BasicExecutionContext context = new BasicExecutionContext((EntityPlayer) sender);
            program.execute(context);
    	} catch (RuntimeException exc) {
            sender.addChatMessage(
                new ChatComponentText("Error while running the program: " + exc.getMessage())
                .setChatStyle(
                    new ChatStyle().setColor(EnumChatFormatting.GREEN)));
    	}

        sender.addChatMessage(
            new ChatComponentText("Program executed.")
            .setChatStyle(
                new ChatStyle().setColor(EnumChatFormatting.GREEN)));
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/run <filename> - Runs the given BASIC program on the server. (The file must be on the server, not the client.)";
    }
}

