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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.chat.TableFormatter;
import net.minecraftforge.server.util.SpawnUtils;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandTracking extends CommandBase {

    private static final DecimalFormat df = new DecimalFormat("0.##");

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        SpawnUtils spawnUtils = SpawnUtils.getInstance();
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Spawn Tracking: " + (spawnUtils.isSpawnTracking() ? "running..." : "inactive")));
            return;
        }
        if (args.length != 1) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                spawnUtils.startSpawnTracking();
                sender.sendMessage(new TextComponentString("[Spawn Utils]: Spawn tracking active."));
                break;
            case "stop":
                sender.sendMessage(new TextComponentString("[Spawn Utils]: Spawn tracking finished."));
                spawnUtils.stopSpawnTracking();
                break;
            case "report":
                sender.sendMessage(new TextComponentString(report(new TableFormatter(sender))));
                break;
            default:
                throw new WrongUsageException("Invalid Usage");
        }
    }

    protected String report(TableFormatter tf) {
        StringBuilder builder = new StringBuilder();

        HashMap<World, HashMap<String, Integer>> spawns = SpawnUtils.getInstance().getTrackedSpawns();
        if (spawns.isEmpty()) {
            return "§c[Spawn Utils] No spawn tracking report available.";
        }
        for (World world : SpawnUtils.getInstance().getTrackedSpawns().keySet()) {
            builder.append(String.format("DIM: %d\n", world.provider.getDimension()));
            tf.heading("Mob Type");
            tf.heading("# Spawns");
            tf.heading("%");
            tf.heading("Avg/Tick");
            for (String mob : spawns.get(world).keySet()) {
                tf.row(mob);
                tf.row(spawns.get(world).get(mob));
                tf.row("%" + df.format(100.0D * spawns.get(world).get(mob) / SpawnUtils.getInstance().getSpawnsSucceeded()));
                tf.row((double) spawns.get(world).get(mob)/SpawnUtils.getInstance().getTimeTracking());
            }
            tf.finishTable();
            builder.append(tf.toString());
            builder.append("\n\nTotal Time: ");
            builder.append(SpawnUtils.getInstance().getTimeTracking());
            builder.append(" ticks | ");
            builder.append(df.format(SpawnUtils.getInstance().getTimeTracking() / 20.0D));
            builder.append(" seconds");
        }
        return builder.toString();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>(Arrays.asList("report", "start", "stop"));
    }

    @Override
    public String getName()
    {
        return "tracking";
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
        return "commands.spawn_utils.tracking.usage";
    }
}
