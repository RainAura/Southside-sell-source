package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.misc.hytprotocols.ForgeProcessor;
import dev.diona.southside.module.modules.misc.hytprotocols.GermModProcessor;
import dev.diona.southside.module.modules.misc.hytprotocols.VexViewProcessor;
import dev.diona.southside.util.player.ChatUtil;
import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketJoinGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import static dev.diona.southside.Southside.MC.mc;

public class HytProtocol extends Module {
    private static HytProtocol INSTANCE;
    private final ArrayList<HytProtocolProcessor> processors = new ArrayList<>();
    private final LinkedList<SPacketCustomPayload> storedPayloads = new LinkedList<>();
    public final Switch registerValue = new Switch("Register", false);

    public HytProtocol(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        processors.add(new ForgeProcessor());
        processors.add(new GermModProcessor());
        processors.add(new VexViewProcessor());
        INSTANCE = this;
    }

    @EventListener
    public void onTick(TickEvent event) throws IOException {
        while (!storedPayloads.isEmpty()) {
            SPacketCustomPayload packet = storedPayloads.pollFirst();
            for (HytProtocolProcessor processor : processors) {
                if (processor.getChannel().equals(packet.getChannelName())) {
                    try {
                        processor.process(packet.getBufferData());
                    } catch (Exception ignored) {

                    }
                }
            }
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketCustomPayload packet) {
            mc.addScheduledTask(() -> storedPayloads.add(packet));
        }
    }

    public static boolean isInstanceEnabled() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

    static byte[] hexToByteArray2(String hex)
    {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static void sendRegisterPacket() {
        if (!INSTANCE.isEnabled()) return;
        if (!INSTANCE.registerValue.getValue()) return;
        assert mc.getConnection() != null;
//        mc.getConnection().sendPacket(new CPacketCustomPayload("FML|HS", (new PacketBuffer(Unpooled.buffer().writeBytes(new byte[] {2, 26, 9, 109, 105, 110, 101, 99, 114, 97, 102, 116, 6, 49, 46, 49, 50, 46, 50, 9, 100, 101, 112, 97, 114, 116, 109, 111, 100, 3, 49, 46, 48, 13, 115, 99, 114, 101, 101, 110, 115, 104, 111, 116, 109, 111, 100, 3, 49, 46, 48, 3, 101, 115, 115, 5, 49, 46, 48, 46, 50, 7, 118, 101, 120, 118, 105, 101, 119, 6, 50, 46, 54, 46, 49, 48, 18, 98, 97, 115, 101, 109, 111, 100, 110, 101, 116, 101, 97, 115, 101, 99, 111, 114, 101, 5, 49, 46, 57, 46, 52, 10, 115, 105, 100, 101, 98, 97, 114, 109, 111, 100, 3, 49, 46, 48, 11, 115, 107, 105, 110, 99, 111, 114, 101, 109, 111, 100, 6, 49, 46, 49, 50, 46, 50, 15, 102, 117, 108, 108, 115, 99, 114, 101, 101, 110, 112, 111, 112, 117, 112, 12, 49, 46, 49, 50, 46, 50, 46, 51, 56, 48, 48, 48, 8, 115, 116, 111, 114, 101, 109, 111, 100, 3, 49, 46, 48, 3, 109, 99, 112, 4, 57, 46, 52, 50, 7, 115, 107, 105, 110, 109, 111, 100, 3, 49, 46, 48, 13, 112, 108, 97, 121, 101, 114, 109, 97, 110, 97, 103, 101, 114, 3, 49, 46, 48, 13, 100, 101, 112, 97, 114, 116, 99, 111, 114, 101, 109, 111, 100, 6, 49, 46, 49, 50, 46, 50, 9, 109, 99, 98, 97, 115, 101, 109, 111, 100, 3, 49, 46, 48, 17, 109, 101, 114, 99, 117, 114, 105, 117, 115, 95, 117, 112, 100, 97, 116, 101, 114, 3, 49, 46, 48, 3, 70, 77, 76, 9, 56, 46, 48, 46, 57, 57, 46, 57, 57, 11, 110, 101, 116, 101, 97, 115, 101, 99, 111, 114, 101, 6, 49, 46, 49, 50, 46, 50, 7, 97, 110, 116, 105, 109, 111, 100, 3, 50, 46, 48, 10, 110, 101, 116, 119, 111, 114, 107, 109, 111, 100, 6, 49, 46, 49, 49, 46, 50, 5, 102, 111, 114, 103, 101, 12, 49, 52, 46, 50, 51, 46, 53, 46, 50, 55, 54, 56, 13, 102, 114, 105, 101, 110, 100, 112, 108, 97, 121, 109, 111, 100, 3, 49, 46, 48, 4, 108, 105, 98, 115, 5, 49, 46, 48, 46, 50, 9, 102, 105, 108, 116, 101, 114, 109, 111, 100, 3, 49, 46, 48, 7, 103, 101, 114, 109, 109, 111, 100, 5, 51, 46, 52, 46, 50, 9, 112, 114, 111, 109, 111, 116, 105, 111, 110, 14, 49, 46, 48, 46, 48, 45, 83, 78, 65, 80, 83, 72, 79, 84})))));
//        mc.getConnection().sendPacket(new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().writeBytes(new byte[] {70, 77, 76, 124, 72, 83, 0, 70, 77, 76, 0, 70, 77, 76, 124, 77, 80, 0, 70, 77, 76, 0, 97, 110, 116, 105, 109, 111, 100, 0, 67, 104, 97, 116, 86, 101, 120, 86, 105, 101, 119, 0, 66, 97, 115, 101, 54, 52, 86, 101, 120, 86, 105, 101, 119, 0, 72, 117, 100, 66, 97, 115, 101, 54, 52, 86, 101, 120, 86, 105, 101, 119, 0, 70, 79, 82, 71, 69, 0, 103, 101, 114, 109, 112, 108, 117, 103, 105, 110, 45, 110, 101, 116, 101, 97, 115, 101, 0, 86, 101, 120, 86, 105, 101, 119, 0, 104, 121, 116, 48, 0, 97, 114, 109, 111, 117, 114, 101, 114, 115, 0, 112, 114, 111, 109, 111, 116, 105, 111, 110})))));
        sendHexString("REGISTER", "464D4C7C485300464D4C00464D4C7C4D5000464D4C00616E74696D6F640043686174566578566965770042617365363456657856696577004875644261736536345665785669657700464F524745006765726D706C7567696E2D6E657465617365005665785669657700687974300061726D6F75726572730070726F6D6F74696F6E");
//        sendHexString("germmod-netease", "0000000B0C67616D655F6C6F6164696E67");
    }

    private static void sendHexString(String channel, String payload) {
        mc.getConnection().sendPacket(new CPacketCustomPayload(channel, new PacketBuffer(Unpooled.wrappedBuffer(hexToByteArray2(payload)))));
    }

    public interface HytProtocolProcessor {
        String getChannel();

        void process(PacketBuffer buf) throws IOException;
    }
}
