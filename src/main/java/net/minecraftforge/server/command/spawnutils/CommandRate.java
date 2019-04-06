/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.server.command.spawnutils;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.util.SpawnUtils;

public class CommandRate extends CommandBase {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        SpawnUtils spawnUtils = SpawnUtils.getInstance();
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(String.format("[Spawn Utils:] Spawn Rate: %d", SpawnUtils.getInstance().getSpawnRate())));
            return;
        } else if (args.length == 1) {
            if (args[0].equals("reset")) {
                SpawnUtils.getInstance().setSpawnRate(1);
                return;
            }
            try {
                SpawnUtils.getInstance().setSpawnRate(parseInt(args[0]));
                sender.sendMessage(new TextComponentString(String.format("[Spawn Utils:] Spawn Rate: %d", SpawnUtils.getInstance().getSpawnRate())));
                return;
            } catch (NumberInvalidException e) {}
        }
        throw new WrongUsageException("[Spawn Utils]: Invalid usage!");
    }

    @Override
    public String getName()
    {
        return "rate";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 1;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public String getUsage(ICommandSender icommandsender)
    {
        return "commands.spawn_utils.rate.usage";
    }
}
