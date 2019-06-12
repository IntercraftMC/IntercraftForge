package cubimod.cubitick.command;

import cubimod.cubitick.Cubitick;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandRate extends CommandBase
{
    public String getName()
    {
        return "rate";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.cubitick.rate.usage";
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
            float tickspeed = parseFloat(args[0]);
            if (tickspeed > 0.0) {
                Cubitick.setWorldTickRateServer(tickspeed);
                notify(sender, "Tickrate set to " + tickspeed);
            } else {
                notify(sender, "Tickrate floating point number greater than 0");
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