package cubimod.cubitick.network;

import cubimod.cubitick.Cubitick;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CubitickMessageHandler implements IMessageHandler<CubitickMessage, IMessage> {

    @Override
    public IMessage onMessage(CubitickMessage message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            Cubitick.syncTick = message.syncTick;
            Cubitick.setWorldTickRateClient(message.tickRate);
        }
        return null;
    }
}
