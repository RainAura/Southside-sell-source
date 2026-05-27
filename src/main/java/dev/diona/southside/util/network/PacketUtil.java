package dev.diona.southside.util.network;

import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import org.lwjglx.Sys;

import static dev.diona.southside.Southside.MC.mc;

public class PacketUtil {
    public static boolean isCPacket(Packet<?> packet) {
        if (EnumConnectionState.PLAY.getPacketDirection(packet) == EnumPacketDirection.SERVERBOUND) return true;
        return false;
    }

    public static boolean isSPacket(Packet<?> packet) {
        return !isCPacket(packet);
    }

    public static boolean isEssential(Packet<?> packet) {
        if (mc.player == null) return true;
        if (mc.currentScreen instanceof GuiDownloadTerrain) return true;

        // connection packets
        if (packet instanceof SPacketServerInfo) return true;
        if (packet instanceof SPacketEncryptionRequest) return true;
        if (packet instanceof SPacketPlayerListItem) return true;
        if (packet instanceof SPacketDisconnect) return true;
        if (packet instanceof SPacketChunkData) return true;
        if (packet instanceof SPacketUnloadChunk) return true;
        if (packet instanceof SPacketPong) return true;
        if (packet instanceof SPacketJoinGame) return true;
        if (packet instanceof SPacketLoginSuccess) return true;
        if (packet instanceof SPacketMaps) return true;
        if (packet instanceof SPacketSpawnPosition) return true;

        if (packet instanceof SPacketChat) return true;
        if (packet instanceof SPacketTitle) return true;


        if (mc.player.ticksExisted <= 60) return true;
//        System.out.println(packet.getClass().getSimpleName());

        return false;
    }
}
