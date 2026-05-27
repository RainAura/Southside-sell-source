package dev.diona.southside.module.modules.misc.hytprotocols;

import com.google.common.collect.Maps;
import dev.diona.southside.module.modules.misc.HytProtocol;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.io.IOException;
import java.util.Map;

import static dev.diona.southside.Southside.MC.mc;

public class ForgeProcessor implements HytProtocol.HytProtocolProcessor {

    @Override
    public String getChannel() {
        return "FML|HS";
    }

    @Override
    public void process(PacketBuffer buf) throws IOException {
        int packetId = buf.readByte();
        System.out.println(packetId);
        if (packetId == 0) {
            mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketCustomPayload("FML|HS", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{0x01, 0x02}))));
        } else if (packetId == 2) {
            String mods = "minecraft,1.12.2\n" +
                    "mcp,9.42\n" +
                    "FML,8.0.99.99\n" +
                    "forge,14.23.5.2768\n" +
                    "skincoremod,1.12.2\n" +
                    "departcoremod,1.12.2\n" +
                    "basemodneteasecore,1.9.4\n" +
                    "neteasecore,1.12.2\n" +
                    "foamfixcore,7.7.4\n" +
                    "mercurius_updater,1.0\n" +
                    "networkmod,1.11.2\n" +
                    "antimod,2.0\n" +
                    "friendplaymod,1.0\n" +
                    "playermanager,1.0\n" +
                    "mcbasemod,1.0\n" +
                    "fullscreenpopup,1.12.2.38000\n" +
                    "skinmod,1.0\n" +
                    "screenshotmod,1.0\n" +
                    "filtermod,1.0\n" +
                    "departmod,1.0\n" +
                    "libs,1.0.2\n" +
                    "ess,1.0.2\n" +
                    "germmod,3.4.2\n" +
                    "vexview,2.6.10\n" +
                    "foamfix,@VERSION@\n" +
                    "promotion,1.0.0-SNAPSHOT\n" +
                    "sidebarmod,1.0\n" +
                    "storemod,1.0";

            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            Map<String,String> modTags = Maps.newHashMap();
            for(String sb : mods.split("\n")){
                String[] modInfo = sb.split(",");
                modTags.put(modInfo[0],modInfo[1]);
            }
            buffer.writeVarInt(2); // Magic
            buffer.writeByte(modTags.size());
            for (Map.Entry<String, String> modTag : modTags.entrySet()) {
                buffer.writeString(modTag.getKey());
                buffer.writeString(modTag.getValue());
            }
            mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketCustomPayload("FML|HS",buffer));
        }
    }
}
