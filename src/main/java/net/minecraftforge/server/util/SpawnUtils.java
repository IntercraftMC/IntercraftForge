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

package net.minecraftforge.server.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class SpawnUtils {

    private static SpawnUtils instance;
    protected final Logger LOGGER = LogManager.getLogger();

    protected boolean spawnMocking = false;
    protected boolean spawnTracking = false;
    protected int spawnsSucceeded = 0;
    protected long tick = 0;
    protected long startTick = 0;
    protected long stopTick = 0;
    protected int spawnRate = 1;
    protected HashMap<World, HashMap<String, Integer>> spawns = new HashMap<>();

    public static synchronized SpawnUtils getInstance() {
        if (instance == null) {
            instance = new SpawnUtils();
        }
        return instance;
    }

    /**
     * Invoked each time a mob is spawned. Used for mob tracking
     * @param world
     * @param entityLiving
     */
    public boolean onLivingEntitySpawn(World world, EntityLiving entityLiving) {
        if (spawnTracking) {
            if (!spawns.containsKey(world)) {
                spawns.put(world, new HashMap<>());
            }
            HashMap<String, Integer> worldSpawns = spawns.get(world);
            String name = entityLiving.getDisplayName().getUnformattedText();
            if (!worldSpawns.containsKey(name)) {
                worldSpawns.put(name, 0);
            }
            spawnsSucceeded++;
            worldSpawns.put(name, worldSpawns.get(name) + 1);
        }
        if (spawnMocking) {
            entityLiving.setDead();
        }
        return !spawnMocking;
    }

    /**
     * Get the tracking results
     */
    public HashMap<World, HashMap<String, Integer>> getTrackedSpawns() {
        return (HashMap<World, HashMap<String, Integer>>) spawns.clone();
    }

    /**
     * Start tracking mob spawning
     */
    public boolean startSpawnTracking() {
        if (spawnTracking) {
            return false;
        }
        spawnTracking = true;
        spawnsSucceeded = 0;
        startTick = tick;
        spawns.clear();
        return true;
    }

    /**
     * Stop tracking mob spawning
     */
    public boolean stopSpawnTracking() {
        if (!spawnTracking) {
            return false;
        }
        spawnTracking = false;
        stopTick = tick;
        return true;
    }

    /**
     * Simulate mob spawning and build some statistics
     */
    public void setSpawnMocking(boolean mocking) {
        spawnMocking = mocking;
    }

    public int getSpawnRate() {
        return spawnRate;
    }

    /**
     * Set the mob spawn rate (between 1 and 1000 inclusive)
     */
    public void setSpawnRate(int rate) {
        spawnRate = Math.max(Math.min(rate, 1000), 1);
    }

    /**
     * Get the number of successful spawn attempts
     */
    public int getSpawnsSucceeded() {
        return spawnsSucceeded;
    }

    /**
     * Get the amount of time spent tracking
     */
    public long getTimeTracking() {
        if (spawnTracking) {
            return tick - startTick;
        }
        return stopTick - startTick;
    }

    /**
     * Check if spawn mocking is enabled
     */
    public boolean isSpawnMocking() {
        return spawnMocking;
    }

    /**
     * Check if spawn tracking is active
     */
    public boolean isSpawnTracking() {
        return spawnTracking;
    }

    public void tick() {
        tick += spawnRate;
    }
}
