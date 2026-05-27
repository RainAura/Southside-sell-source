package dev.diona.southside.util.quickmacro.metadatas;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import static dev.diona.southside.Southside.MC.mc;

public class GermMetadata {
    private final String path;
//    private final String parentUuid;
    private final String text;

    public GermMetadata(String path, String text) {
        this.path = path;
        this.text = text;
    }

    public void mouseClicked(String parentUuid) {
//        if (hovered) {
            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                    .writeString(parentUuid)
                    .writeString(path)
                    .writeInt(0))
            ));
            whenClick();
            if (doesCloseOnClickButton()) {
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                        .writeString(parentUuid)
                ));
                mc.displayGuiScreen(null);
            }
//        }
    }

    protected void whenClick() {

    }

    protected boolean doesCloseOnClickButton() {
        return true;
    }

    public String getText() {
        return text;
    }

//    public String getParentUuid() {
//        return parentUuid;
//    }

    public String getPath() {
        return path;
    }
}
