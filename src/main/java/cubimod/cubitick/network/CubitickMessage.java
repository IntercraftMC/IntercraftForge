package cubimod.cubitick.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CubitickMessage implements IMessage
{
    public float   tickRate;
    public boolean syncTick;

    public CubitickMessage()
    {
    }

    public CubitickMessage(float tickRate, boolean syncTick)
    {
        this.tickRate = tickRate;
        this.syncTick = syncTick;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        tickRate = buf.readFloat();
        syncTick = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(tickRate);
        buf.writeBoolean(syncTick);
    }
}