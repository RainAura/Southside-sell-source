package dev.diona.southside.util.quickmacro.vexview;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

import static dev.diona.southside.Southside.MC.mc;

public class VexViewSender {
    private static void sendJson(JsonObject json) {
        byte[] data = encode(json.toString());
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketCustomPayload("VexView", new PacketBuffer(buf)));
    }

    private static byte[] encode(String json) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                GZIPOutputStream out = new GZIPOutputStream(bout);
                byte[] array = new byte[256];
                int read;
                while ((read = in.read(array)) >= 0) {
                    out.write(array, 0, read);
                }
                out.finish();
                return bout.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void openGui() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet_sub_type", "null");
        jsonObject.addProperty("packet_data", "null");
        jsonObject.addProperty("packet_type", "opengui");
        sendJson(jsonObject);
    }

    public static void closeGui() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet_sub_type", "null");
        jsonObject.addProperty("packet_data", "null");
        jsonObject.addProperty("packet_type", "gui_close");
        sendJson(jsonObject);
    }

    public static void clickButton(String id) {
        openGui();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet_sub_type", id);
        jsonObject.addProperty("packet_data", "null");
        jsonObject.addProperty("packet_type", "button");
        sendJson(jsonObject);
        closeGui();
    }

    public static void joinParty(String text, String fieldId, String id) {
        openGui();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet_sub_type", fieldId);
        jsonObject.addProperty("packet_data", text);
        jsonObject.addProperty("packet_type", "fieldtext");
        sendJson(jsonObject);
        clickButton(id);
        closeGui();
    }
}
