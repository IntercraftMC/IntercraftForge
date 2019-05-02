package net.minecraftforge.server.cubitick.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.cubitick.Cubitick;

public class CommandTicksync extends CommandBase {

    public String getName()
    {
        return "ticksync";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "/ticksync [true|false]";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if(args.length < 1) {
            notify(sender,  (Cubitick.synctick) ? "Tickrate is currently synchronised" : "Tickrate is currently desynchronised");
            return;
        } else {
            if(args[0].equals("true") || args[0].equals("on")) {
                Cubitick.synctick = true;
                notify(sender,  "Tickrate is now synchronised");
            } else if(args[0].equals("false") || args[0].equals("off")) {
                Cubitick.synctick = false;
                notify(sender, "Tickrate is now desynchronised");
            } else {
                notify(sender, "Tickrate synchronisation can only be true/on or false/off");
            }
        }
    }

    public void notify(ICommandSender sender, String msg)
    {
        if(sender != null)
            notifyCommandListener(sender, this, msg);
    }
}
