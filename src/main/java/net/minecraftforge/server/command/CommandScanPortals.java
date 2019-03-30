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

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CommandScanPortals extends CommandBase {

    private PortalScanner portalScanner;

    public CommandScanPortals() {
    }

    @Override
    public String getName() {
        return "scan-portals";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.forge.scanportals.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int i;
        int dimensions[] = {-1, 0};

        if (args.length > 1 || args.length == 1 && !args[0].equals("complete")) {
            throw new WrongUsageException("commands.forge.scanportals.usage");
        }

        if (this.portalScanner != null && this.portalScanner.scanning) {
            sender.sendMessage(new TextComponentString("[Portal Scanner] Already scanning portals..."));
            return;
        }

        if (args.length == 1) {
            portalScanner = new GlobalPortalScanner(server, dimensions);
        } else {
            portalScanner = new LocalPortalScanner(server, (WorldServer) sender.getEntityWorld());
        }
        portalScanner.start();
    }

    public static abstract class PortalScanner extends Thread
    {
        protected MinecraftServer server;
        public boolean scanning = false;

        public PortalScanner(MinecraftServer server) {
            this.server = server;
        }

        @Override
        public void run() {
            int portalsFound = 0;
            server.getPlayerList().sendMessage(new TextComponentString("[Portal Scanner] Scanning for portals..."));
            scanning = true;
            try {
                portalsFound = scan();
            } catch (Exception e) {}
            scanning = false;
            server.getPlayerList().sendMessage(new TextComponentString(String.format("[Portal Scanner] Scan finished. %d portals found", portalsFound)));
        }

        abstract int scan();
    }

    public static class LocalPortalScanner extends PortalScanner
    {

        private WorldServer world;

        public LocalPortalScanner (MinecraftServer server, WorldServer world) {
            super(server);
            this.world = world;
        }

        @Override
        public int scan() {
            HashMap<BlockPos, PortalBlock> portalBlocks = new HashMap<>();
            HashSet<BlockPos> toRemove = new HashSet<>();
            // Remove portals that no longer exist
            for (BlockPos portal : this.world.netherPortalCollection.getPortals()) {
                long pos = ChunkPos.asLong(portal.getX() >> 4, portal.getZ() >> 4);
                if (this.world.getChunkProvider().id2ChunkMap.containsKey(pos)) {
                    if (this.world.getBlockState(portal).getBlock() != Blocks.PORTAL) {
                        toRemove.add(portal);
                    }
                }
            }

            for (Chunk chunk : this.world.getChunkProvider().getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < this.world.getHeight(); y++) {
                        for (int z = 0; z < 16; z++) {
                            if (chunk.getBlockState(x, y, z).getBlock() == Blocks.PORTAL) {
                                BlockPos pos = new BlockPos((chunk.x << 4) | x, y, (chunk.z << 4) | z);
                                portalBlocks.put(pos, new PortalBlock(pos, (EnumFacing.Axis) chunk.getBlockState(x, y, z).getProperties().get(BlockPortal.AXIS)));
                            }
                        }
                    }
                }
            }
            HashSet<BlockPos> newPortals = PortalBlock.merge(portalBlocks);
            this.world.netherPortalCollection.update(toRemove, newPortals);
            return newPortals.size();
        }
    }

    public static class GlobalPortalScanner extends PortalScanner
    {
        private File[] regions;
        private WorldServer[] worlds;
        protected HashSet<BlockPos>[] portals;

        public GlobalPortalScanner(MinecraftServer server, int[] dimensions) {
            super(server);

            portals = new HashSet[dimensions.length];
            worlds = new WorldServer[dimensions.length];
            regions = new File[dimensions.length];
            for (int i = 0; i < dimensions.length; i++) {
                worlds[i] = server.getWorld(dimensions[i]);
                regions[i] = new File(worlds[i].getChunkSaveLocation(), "region");
                portals[i] = (HashSet<BlockPos>) worlds[i].netherPortalCollection.getPortals().clone();
            }
        }

        public int scan() {
            int i;
            WorldPortalScanner[] threads = new WorldPortalScanner[regions.length];
            for (i = 0; i < threads.length; i++) {
                threads[i] = new WorldPortalScanner(regions[i]);
                threads[i].start();
            }

            for (i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch(InterruptedException e) {}
            }

            int result = 0;
            for (i = 0; i < worlds.length; i++) {
                worlds[i].netherPortalCollection.update(portals[i], threads[i].portals);
                result += threads[i].portals.size();
            }
            return result;
        }
    }

    public static class WorldPortalScanner extends Thread
    {
        private static final Logger LOGGER = LogManager.getLogger();
        private HashMap<BlockPos, PortalBlock> portalBlocks = new HashMap<>();
        private File regions;
        public HashSet<BlockPos> portals = new HashSet<>();

        public WorldPortalScanner(File regions)
        {
            this.regions = regions;
        }

        public void run()
        {
            int cx, cz;
            int regionX, regionZ;
            for (File file : regions.listFiles()) {
                RegionFile region = new RegionFile(file);
                try {
                    String[] nameParts = file.getName().split("\\.");
                    regionX = parseInt(nameParts[1]) << 5;
                    regionZ = parseInt(nameParts[2]) << 5;
                } catch (NumberInvalidException e) { return; }

                for (cx = 0; cx < 32; cx++) {
                    for (cz = 0; cz < 32; cz++) {
                        if (region.isChunkSaved(cx, cz)) {
                            try {
                                DataInputStream stream = region.getChunkDataInputStream(cx, cz);
                                NBTTagCompound data = CompressedStreamTools.read(stream);
                                if (!data.hasKey("Level", Constants.NBT.TAG_COMPOUND)) {
                                    LOGGER.error("Chunk at {} {} is missing key `Level`", cx, cz);
                                    continue;
                                }
                                NBTTagCompound level = data.getCompoundTag("Level");
                                if (!level.hasKey("Sections", Constants.NBT.TAG_LIST)) {
                                    LOGGER.error("Chunk at {} {} is missing block data", cx, cz);
                                    continue;
                                }
                                findPortalBlocks(regionX + cx, regionZ + cz, level.getTagList("Sections", Constants.NBT.TAG_COMPOUND));
                            } catch (IOException e) {}
                        }
                    }
                }
            }
            portals = PortalBlock.merge(portalBlocks);
            for (BlockPos pos : portals) {
                LOGGER.info("Found {} portal(s) at location {}", portals.size(), pos);
            }
        }

        private void findPortalBlocks(int x, int z, NBTTagList sections)
        {
            for (int subchunk = 0; subchunk < sections.tagCount(); subchunk++) {
                NBTTagCompound section = sections.getCompoundTagAt(subchunk);
                byte[] blockIds = section.getByteArray("Blocks");
                NibbleArray data = new NibbleArray(section.getByteArray("Data"));
                NibbleArray blockIdExtension = section.hasKey("Add", Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(section.getByteArray("Add")) : null;
                for (int i = 0; i < 4096; ++i) {
                    int j = i & 15;
                    int k = i >> 8 & 15;
                    int l = i >> 4 & 15;
                    int i1 = blockIdExtension == null ? 0 : blockIdExtension.get(j, k, l);
                    int j1 = i1 << 12 | (blockIds[i] & 255) << 4 | data.get(j, k, l);
                    IBlockState blockState = Block.BLOCK_STATE_IDS.getByValue(j1);
                    if (blockState.getBlock() == Blocks.PORTAL) {
                        BlockPos pos = new BlockPos((x << 4) | (i & 0xf), (subchunk << 4) | (i >> 8), (z << 4) | ((i >> 4) & 0xf));
                        portalBlocks.put(pos, new PortalBlock(pos, (EnumFacing.Axis) blockState.getProperties().get(BlockPortal.AXIS)));
                    }
                }
            }
        }
    }

    public static class PortalBlock implements Comparable<PortalBlock>
    {
        public BlockPos pos;
        public EnumFacing.Axis axis;

        public PortalBlock(BlockPos pos, EnumFacing.Axis axis) {
            this.pos = pos;
            this.axis = axis;
        }

        public int hashCode() { return pos.hashCode(); }

        @Override
        public int compareTo(PortalBlock other) {
            return pos.compareTo(other.pos);
        }

        public static HashSet<BlockPos> merge(HashMap<BlockPos, PortalBlock> portalBlocks)
        {
            HashSet<BlockPos> portals = new HashSet<>();
            int i, j;
            BlockPos t, b, pos;
            PortalBlock block;
            EnumFacing facing;
            while (!portalBlocks.isEmpty()) {
                block = ((PortalBlock)portalBlocks.values().toArray()[0]);
                facing = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, block.axis);

                // Find the top and bottom opposite corners
                for (t = block.pos.offset(facing, 1); portalBlocks.containsKey(t); t = t.offset(facing, 1)) {}
                for (t = t.offset(facing, -1).add(0, 1, 0); portalBlocks.containsKey(t); t = t.add(0, 1, 0)) {}
                for (b = block.pos.offset(facing, -1); portalBlocks.containsKey(b); b = b.offset(facing, -1)) {}
                for (b = b.offset(facing, 1).add(0, -1, 0); portalBlocks.containsKey(b); b = b.add(0, -1, 0)) {}

                // Correct the offsets
                t = t.down();
                b = b.up();

                // Remove the portal blocks bounded by the given corners.
                int width = 1 + t.getX() - b.getX() + t.getZ() - b.getZ();
                int height = 1 + t.getY() - b.getY();
                for (i = 0; i < height; i++) {
                    for (j = 0; j < width; j++) {
                        portalBlocks.remove(b.up(i).offset(facing, j));
                    }
                }

                // Get the center point of the portal and save it
                int offset = (int)(facing.getAxis() == EnumFacing.Axis.Z ? Math.ceil((width-1)/2.0) : (width-1)/2);
                portals.add(b.offset(facing, offset));
            }
            return portals;
        }
    }
}
