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

package net.minecraftforge.server.cubitick;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.cubitick.network.CubitickMessage;
import net.minecraftforge.server.cubitick.network.CubitickMessageHandler;

public class Cubitick {
    public static Cubitick instance = new Cubitick();

    public static boolean initialised = false;

    public static final float tickrate = 20F;
    public static float tickrateWorld  = 20F;
    public static boolean synctick     = false;

    public static SimpleNetworkWrapper NETWORK;

    public Cubitick()
    {
        initialised = false;
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("Cubitick");
        NETWORK.registerMessage(CubitickMessageHandler.class, CubitickMessage.class, 0, Side.CLIENT);
        NETWORK.registerMessage(CubitickMessageHandler.class, CubitickMessage.class, 1, Side.SERVER);
    }

    public static void setTickWorldClient(float rate)
    {
        System.out.println("Setting Client Tickrate:" + rate);
        tickrateWorld = rate;
        setTimerWorld(rate);
    }

    public static void setTickWorldServer(float rate)
    {
        tickrateWorld = rate;
        changeClientTickrate();
    }

    @SideOnly(Side.CLIENT)
    private static void setTimerWorld(float rate)
    {
        Minecraft mc = Minecraft.getMinecraft();
        float elapsedPartialTicks = mc.timerWorld.elapsedPartialTicks;
        int elapsedTicks = mc.timerWorld.elapsedTicks;
        float renderPartialTicks = mc.timerWorld.renderPartialTicks;

        mc.timerWorld = new Timer(rate);

        mc.timerWorld.elapsedPartialTicks = elapsedPartialTicks;
        mc.timerWorld.elapsedTicks = elapsedTicks;
        mc.timerWorld.renderPartialTicks = renderPartialTicks;

        mc.timer = new Timer(tickrate);
    }

    public static float getTickms()
    {
        if (Cubitick.tickrateWorld <= 0) return Float.MAX_VALUE;
        return 1000F/Cubitick.tickrateWorld;
    }

    public static void playerChat(String str)
    {
        if (Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendChatMessage(str);
    }

    public static void changeClientTickrate()
    {
        try {
            NETWORK.sendToAll(new CubitickMessage(tickrateWorld, synctick));
        } catch(NullPointerException e) {}
    }
}
