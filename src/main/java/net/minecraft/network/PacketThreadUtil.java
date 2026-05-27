package net.minecraft.network;

import dev.diona.southside.Southside;
import dev.diona.southside.util.misc.disablerMagic.PacketCancelledException;
import dev.diona.southside.event.PacketType;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.util.misc.disablerMagic.PacketProcessRunnable;
import dev.diona.southside.util.network.PacketUtil;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.util.IThreadListener;
import net.optifine.Config;

public class PacketThreadUtil
{
    public static int lastDimensionId = Integer.MIN_VALUE;

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packetIn, final T processor, IThreadListener scheduler) throws ThreadQuickExitException, PacketCancelledException {
        if (!scheduler.isCallingFromMinecraftThread())
        {
            scheduler.addScheduledTask(new PacketProcessRunnable(packetIn)
            {
                public void run()
                {
                    PacketThreadUtil.clientPreProcessPacket(packetIn);
                    packetIn.processPacket(processor);
                }
            });
            throw ThreadQuickExitException.INSTANCE;
        }
        else
        {
            if (PacketUtil.isSPacket(packetIn)) {
                PacketEvent event = new PacketEvent(PacketType.RECEIVE, packetIn);
                Southside.eventBus.post(event);
                if (event.isCancelled()) {
                    throw new PacketCancelledException();
                }
            }

            clientPreProcessPacket(packetIn);
        }
    }

    protected static void clientPreProcessPacket(Packet p_clientPreProcessPacket_0_)
    {
        if (p_clientPreProcessPacket_0_ instanceof SPacketPlayerPosLook)
        {
            Config.getRenderGlobal().onPlayerPositionSet();
        }

        if (p_clientPreProcessPacket_0_ instanceof SPacketRespawn)
        {
            SPacketRespawn spacketrespawn = (SPacketRespawn)p_clientPreProcessPacket_0_;
            lastDimensionId = spacketrespawn.getDimensionID();
        }
        else if (p_clientPreProcessPacket_0_ instanceof SPacketJoinGame)
        {
            SPacketJoinGame spacketjoingame = (SPacketJoinGame)p_clientPreProcessPacket_0_;
            lastDimensionId = spacketjoingame.getDimension();
        }
        else
        {
            lastDimensionId = Integer.MIN_VALUE;
        }
    }
}
