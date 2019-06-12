/**
 * Cubitick
 *
 * Updated implementation by SirDavidLudwig
 * https://github.com/SirDavidLudwig
 *
 * Original mod created by Cubitect
 * https://github.com/Cubitect
 */
package cubimod.cubitick;

import cubimod.cubitick.network.CubitickMessage;
import cubimod.cubitick.network.CubitickMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Cubitick
{
    public static Cubitick instance = new Cubitick();

    public static final float tickrate = 20F;
    public static float tickrateWorld = 20F;
    public static boolean syncTick = false;

    public static SimpleNetworkWrapper NETWORK;

    /**
     * Initialize Cubitick and set the network up
     */
    public Cubitick()
    {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("Cubitick");
        NETWORK.registerMessage(CubitickMessageHandler.class, CubitickMessage.class, 0, Side.CLIENT);
        NETWORK.registerMessage(CubitickMessageHandler.class, CubitickMessage.class, 1, Side.SERVER);
    }

    /**
     * Set the tick rate on the client
     * @param rate
     */
    public static void setWorldTickRateClient(float rate)
    {
        tickrateWorld = rate;
        setTimerWorld(rate);
    }

    /**
     * Set the tick rate on the server
     * @param rate
     */
    public static void setWorldTickRateServer(float rate)
    {
        tickrateWorld = rate;
        changeClientTickrate();
    }

    /**
     * Set the tick rate for the client world
     * @param rate
     */
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

    /**
     * Get the tick rate in milliseconds
     * @return
     */
    public static float getTickMs()
    {
        if (Cubitick.tickrateWorld <= 0) {
            return Float.MAX_VALUE;
        }
        return 1000F/Cubitick.tickrateWorld;
    }

    /**
     * Update the client's world tick rate
     */
    public static void changeClientTickrate()
    {
        try {
            NETWORK.sendToAll(new CubitickMessage(tickrateWorld, syncTick));
        } catch (NullPointerException e) {}
    }

    /**
     * Update the client's world tick rate
     */
    public static void changeClientTickrate(EntityPlayerMP player)
    {
        try {
            NETWORK.sendTo(new CubitickMessage(tickrateWorld, syncTick), player);
        } catch (NullPointerException e) {}
    }
}
