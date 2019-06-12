package cubimod.cubitick.command;


import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

public class CommandTick extends CommandTreeBase
{
    public CommandTick()
    {
        super.addSubcommand(new CommandRate());
        super.addSubcommand(new CommandSync());
        super.addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getName()
    {
        return "tick";
    }

    @Override
    public void addSubcommand(ICommand command)
    {
        throw new UnsupportedOperationException("Don't add sub-commands to /tick, create your own command.");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public String getUsage(ICommandSender icommandsender)
    {
        return "commands.cubitick.usage";
    }
}
