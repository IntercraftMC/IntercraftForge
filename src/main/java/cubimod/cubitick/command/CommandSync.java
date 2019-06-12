package cubimod.cubitick.command;

import cubimod.cubitick.Cubitick;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSync extends CommandBase
{

    public String getName()
    {
        return "sync";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.cubitick.sync.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1) {
            notify(sender, (Cubitick.syncTick) ? "Tickrate is currently synchronised" : "Tickrate is currently desynchronised");
            return;
        } else {
            if (args[0].equals("true") || args[0].equals("on")) {
                Cubitick.syncTick = true;
                notify(sender, "Tickrate is now synchronised");
            } else if (args[0].equals("false") || args[0].equals("off")) {
                Cubitick.syncTick = false;
                notify(sender, "Tickrate is now desynchronised");
            } else {
                notify(sender, "Tickrate synchronisation can only be true/on or false/off");
            }
        }
    }

    public void notify(ICommandSender sender, String msg)
    {
        if (sender != null)
            notifyCommandListener(sender, this, msg);
    }
}