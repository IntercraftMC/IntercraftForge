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

package net.minecraftforge.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;

public class NetherPortalCollection extends WorldSavedData {

    private static final String KEY = "nether_portals";
    private HashSet<BlockPos> portals = new HashSet<>();

    public static NetherPortalCollection fromWorld(World worldIn) {
        NetherPortalCollection collection = (NetherPortalCollection) worldIn.getPerWorldStorage().getOrLoadData(NetherPortalCollection.class, KEY);
        if (collection == null) {
            collection = new NetherPortalCollection(KEY);
            worldIn.getPerWorldStorage().setData(KEY, collection);
            collection.markDirty();
        }
        return collection;
    }

    public NetherPortalCollection(String name) {
        super(name);
    }

    public synchronized NetherPortalCollection add(BlockPos pos) {
        System.out.println(String.format("Adding portal: %s", pos.toString()));
        portals.add(pos);
        markDirty();
        return this;
    }

    public synchronized NetherPortalCollection add(HashSet<BlockPos> portals) {
        this.portals.addAll(portals);
        return this;
    }

    public synchronized NetherPortalCollection remove(BlockPos pos) {
        if (portals.contains(pos)) {
            System.out.println(String.format("Removing portal: %s", pos.toString()));
            portals.remove(pos);
        }
        return this;
    }

    public synchronized NetherPortalCollection remove(HashSet<BlockPos> portals) {
        this.portals.removeAll(portals);
        return this;
    }

    public synchronized NetherPortalCollection update(HashSet<BlockPos> oldPortals, HashSet<BlockPos> newPortals) {
        this.portals.removeAll(oldPortals);
        this.portals.addAll(newPortals);
        markDirty();
        return this;
    }

    public NetherPortalCollection clear() {
        System.out.println("Removed all portals from the dimension.");
        portals.clear();
        markDirty();
        return this;
    }

    public HashSet<BlockPos> getPortals() {
        return portals;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        portals.clear();
        NBTTagList list = nbt.getTagList(KEY, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound portal = list.getCompoundTagAt(i);
            BlockPos pos = new BlockPos(portal.getInteger("X"), portal.getInteger("Y"), portal.getInteger("Z"));
            portals.add(pos);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (BlockPos pos : this.portals) {
            NBTTagCompound portal = new NBTTagCompound();
            portal.setInteger("X", pos.getX());
            portal.setInteger("Y", pos.getY());
            portal.setInteger("Z", pos.getZ());
            list.appendTag(portal);
        }
        compound.setTag(KEY, list);
        return compound;
    }
}
