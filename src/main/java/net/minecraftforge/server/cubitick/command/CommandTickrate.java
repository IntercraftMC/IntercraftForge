package net.minecraftforge.server.cubitick.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.cubitick.Cubitick;

public class CommandTickrate extends CommandBase
{
    public String getName()
    {
        return "tickrate";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "/tickrate [rate]";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if(args.length < 1) {
            notify(sender, "Tickrate is " + Cubitick.tickrateWorld + " ticks per second");
            return;
        } else {
            float tickspeed = (float) parseFloat(args[0]);
            if (tickspeed >= 0) {
                Cubitick.setTickWorld(tickspeed);
                notify(sender, "Tickrate set to " + tickspeed);
            } else {
                notify(sender, "Tickrate should be a non-negative floating point number");
                return;
            }
        }
    }

    public void notify(ICommandSender sender, String msg)
    {
        if(sender != null)
            notifyCommandListener(sender, this, msg);
    }

    public static float parseFloat(String input)
    {
        try {
            return (float)Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return -1F;
        }
    }
}
