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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.util.SpawnUtils;

public class CommandMocking extends CommandBase {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        SpawnUtils spawnUtils = SpawnUtils.getInstance();
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Spawn Mocking: " + (spawnUtils.isSpawnMocking() ? "true" : "false")));
        } else if (args.length == 1 && args[0].toLowerCase().equals("true")) {
            spawnUtils.setSpawnMocking(true);
        } else if (args.length == 1 && args[0].toLowerCase().equals("false")) {
            spawnUtils.setSpawnMocking(false);
        } else {
            throw new WrongUsageException("commands.spawn_utils.mocking.invalid");
        }
    }

    @Override
    public String getName()
    {
        return "mocking";
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
        return "commands.spawn_utils.mocking.usage";
    }
}
