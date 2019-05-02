package net.minecraftforge.server.cubitick;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class Cubitick {

    public static Cubitick instance = new Cubitick();
    public static boolean initialised = false;

    private String author = "Cubitect";
    private String name = "Cubitick";
    private String versionName = "[1.10]v1.5.2";
    private String versionType = "main";
    private String mcVersion   = "1.10";
    private String mcType      = "release";

    public static final float tickrate = 20F;
    public static float tickrateWorld = 20F;
    public static boolean synctick = true;


    public Cubitick()
    {
        initialised = false;
    }

    public static void setTickWorld(float rate)
    {
        tickrateWorld = rate;
        setTimerWorld(rate);
    }

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
        if(Cubitick.tickrateWorld <= 0) return Float.MAX_VALUE;
        return 1000F/Cubitick.tickrateWorld;
    }

    public static void playerChat(String str)
    {
        if(Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendChatMessage(str);
    }
}
