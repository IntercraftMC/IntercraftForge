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

package net.minecraftforge.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CommandHeightMap extends CommandBase {

    @Override
    public String getName()
    {
        return "heightmap";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.heightmap.usage";
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
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Arrays.asList("fix", "height");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1) {
            throw new WrongUsageException(this.getUsage(sender));
        }
        if (args[0].equals("fix")) {
            sender.sendMessage(new TextComponentString("Updating heightmaps..."));
            WorldServer world = (WorldServer) sender.getEntityWorld();
            int chunks = 0;
            for (Chunk chunk : world.getChunkProvider().id2ChunkMap.values()) {
                chunk.generateSkylightMap();
                chunks++;
            }
            sender.sendMessage(new TextComponentString(String.format("%d chunk heightmaps updated", chunks)));
        } else if (args[0].equals("height")) {
            Entity entity = sender.getCommandSenderEntity();
            if (entity != null) {
                BlockPos pos = new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
                int height = sender.getEntityWorld().getChunkFromBlockCoords(pos).getHeight(pos);
                TextComponentString message = new TextComponentString(String.format("Height at (%d, %d, %d): %d", pos.getX(), pos.getY(), pos.getZ(), height));
                sender.sendMessage(message);
            }
        }
    }
}
