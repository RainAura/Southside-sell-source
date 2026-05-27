package dev.diona.southside.module.modules.misc.hytprotocols;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.gui.container.HytMainMenu1;
import dev.diona.southside.module.modules.misc.HytProtocol;
import dev.diona.southside.module.modules.player.AutoPlay;
import dev.diona.southside.util.quickmacro.metadatas.GermMetadata;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.apache.commons.codec.binary.Base32;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.StringTokenizer;

import static dev.diona.southside.Southside.MC.mc;
import static net.minecraft.util.text.ITextComponent.Serializer.GSON;

public class GermModProcessor implements HytProtocol.HytProtocolProcessor {
    private boolean shouldSend = false;
    private final Base32 base32 = new Base32();
    private String lastScreen;
    private byte[] totalBytes = null;
    private int counter;

    @Override
    public String getChannel() {
        return "germplugin-netease";
    }

    // 什么？你问我为什么取消警告？那你删掉试试。
    @SuppressWarnings("all")
    @Override
    public void process(PacketBuffer packetBuffer) {
        AutoPlay ap = (AutoPlay) Southside.moduleManager.getModuleByClass(AutoPlay.class);
        int packetId = packetBuffer.readInt();
        if (packetId == 73) {
            if (ap.isEnabled() && ap.stage != AutoPlay.Stage.IDLE && ap.stage != AutoPlay.Stage.FINISH) {
                return;
            }
            // Gui
            PacketBuffer packetBuffer1 = new PacketBuffer(packetBuffer.copy());
            String identity = packetBuffer1.readString(Short.MAX_VALUE);
            if (identity.equalsIgnoreCase("gui")) {
                String guiUuid = packetBuffer1.readString(Short.MAX_VALUE);
                String yml = packetBuffer1.readString(9999999);
                Yaml yaml = new Yaml();
                Map<String, Object> objectMap = yaml.load(yml);
                if (objectMap == null) return;
                lastScreen = guiUuid;
                objectMap = (Map<String, Object>) objectMap.get(guiUuid);
                if (objectMap == null) return;
                ArrayList<GermMetadata> buttons = new ArrayList<>();
                for (String key : objectMap.keySet()) {
                    if (key.equalsIgnoreCase("options") || key.endsWith("_bg")) continue;
                    Map<String, Object> context = (Map<String, Object>) objectMap.get(key);
                    if (guiUuid.equalsIgnoreCase("mainmenu")) {
                        if (context.containsKey("relativeParts")) {
                            context = (Map<String, Object>) context.get("relativeParts");
                            if (context.containsKey("主分类")) {
                                context = (Map<String, Object>) context.get("主分类");
                                if (context.containsKey("relativeParts")) {
                                    context = (Map<String, Object>) context.get("relativeParts");
                                    for (String k1 : context.keySet()) {
                                        String buttonText =
                                                switch (k1) {
                                                    case "subject_bedwar" -> "起床战争";
                                                    case "subject_skywar" -> "空岛战争";
                                                    case "subject_leisure" -> "休闲游戏";
                                                    case "subject_fight" -> "竞技游戏";
                                                    case "subject_survive" -> "生存";
                                                    case "subject_fight_team" -> "战争";
                                                    default -> "";
                                                };
                                        if (buttonText.isEmpty()) continue;
                                        buttons.add(new GermMetadata("自适应背景$主分类$" + k1, buttonText) {
                                            @Override
                                            protected void whenClick() {
                                                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                                        new PacketBuffer(Unpooled.buffer()
                                                                .writeInt(26))
                                                                .writeString("GUI$mainmenu@subject/" + k1.substring(8).replace("fight_team", "team_fight"))
                                                                .writeString("{\"click\":\"1\"}")));
                                            }

                                            @Override
                                            protected boolean doesCloseOnClickButton() {
                                                return false;
                                            }
                                        });
                                    }
                                    mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                            new PacketBuffer(Unpooled.buffer()
                                                    .writeInt(4)
                                                    .writeInt(0)
                                                    .writeInt(0))
                                                    .writeString(guiUuid)
                                                    .writeString(guiUuid)
                                                    .writeString(guiUuid)
                                    ));
                                    mc.displayGuiScreen(new HytMainMenu1(guiUuid, buttons));
                                }
                            }
                        }
                    } else {
                        for (String k : context.keySet()) {
                            if (!k.equalsIgnoreCase("scrollableParts")) continue;
                            context = (Map<String, Object>) context.get("scrollableParts");
                            for (String uuid : context.keySet()) {
                                Map<String, Object> scrollableSubMap = (Map<String, Object>) context.get(uuid);
                                if (scrollableSubMap.containsKey("relativeParts")) {
                                    scrollableSubMap = (Map<String, Object>) scrollableSubMap.get("relativeParts");
                                    for (String k1 : scrollableSubMap.keySet()) {
                                        scrollableSubMap = (Map<String, Object>) scrollableSubMap.get(k1);
                                        if (scrollableSubMap.containsKey("texts")) {
                                            String buttonText = ((ArrayList<String>) scrollableSubMap.get("texts")).get(0);
                                            buttons.add(new GermMetadata(key + "$" + uuid + "$" + k1, buttonText));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                new PacketBuffer(Unpooled.buffer()
                                        .writeInt(4)
                                        .writeInt(0)
                                        .writeInt(0))
                                        .writeString(guiUuid)
                                        .writeString(guiUuid)
                                        .writeString(guiUuid)
                        ));
//                        ChatUtil.info("got a strange mainmenu");
//                        for (GermMetadata metadata : buttons) {
//                            ChatUtil.info(metadata.getText());
//                        }
//                        mc.displayGuiScreen(new GuiButtonPage(guiUuid, buttons));
                        return;
                    }
                }
            }
            //} else if (packetId == 737) {
            //    // Damage Display
            //    //if (Managers.moduleManager.renderModules.get("DamageParticles").isEnabled()) {
            //    //    String damage = packetBuffer.readString(30000);
            //    //    System.out.println("Damage:" + damage);
            //    //}
            //} else if (packetId == -1) {
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //} else if (packetId == 2141) { // 大喇叭
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //} else if (packetId == 78) {
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //    //System.out.println(packetBuffer.readInt());
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //} else {
            //    //int size = packetBuffer.readableBytes();
            //    //if (size > 0) {
            //        //System.out.println("Unknown packet id: " + packetId + ", size=" + size);
            //    //}
        } else if (packetId == 731) {
            shouldSend = true;
        } else if (packetId == 72) {
            if (shouldSend) {
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeInt(16);
                buffer.writeString("3.4.2");
                buffer.writeString(Base64.getEncoder().encodeToString(encode("花雨庭你为什么要获取我们的计算器信息？").replace("\r\n", "").getBytes(StandardCharsets.UTF_8)));
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", buffer));
                shouldSend = false;
            }
        } else if (packetId == -1) {
            // Game Menu
            // Divide an byte array to multiple byte-array-packets
            boolean start = packetBuffer.readBoolean();
            int length = packetBuffer.readInt();
            boolean end = packetBuffer.readBoolean();
            byte[] tempBytes = packetBuffer.readByteArray();
            if (start) {
                totalBytes = new byte[length];
            }
            try {
                System.arraycopy(tempBytes, 0, totalBytes, counter, tempBytes.length);
                counter += tempBytes.length;
                if (end) {
                    ByteBuf buf = Unpooled.wrappedBuffer(totalBytes);
                    process(new PacketBuffer(buf));
                    counter = 0;
                    totalBytes = null;
                }
            } catch (Exception e) {
                counter = 0;
                totalBytes = null;
            }
        } else if (packetId == 79) {
            if (ap.isEnabled() && ap.stage == AutoPlay.Stage.CLICKING_SUB) {
                ap.receiveSub();
                return;
            }
            String type = packetBuffer.readString(Short.MAX_VALUE);
            int action = packetBuffer.readInt();
            String content = packetBuffer.readString(3276700);
            if (type.equals("GUI") && action == 0) {
                if (lastScreen.equals("mainmenu")) {
                    StringTokenizer token = new StringTokenizer(content, "@");
                    token.nextToken();
                    if (!token.nextToken().equals("data")) return;
                    String json = token.nextToken();
                    JsonObject subs = GSON.fromJson(json, JsonObject.class);
                    JsonArray jsonArray = subs.get("subs").getAsJsonArray();
                    ArrayList<GermMetadata> buttons = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Object o = jsonArray.get(i);
                        if (o instanceof JsonObject JsonObject) {
                            String sid = JsonObject.get("sid").getAsString();
                            String name = JsonObject.get("name").getAsString().replace("&e&l", "");
                            int finalI = i;
                            buttons.add(new GermMetadata("自适应背景$细分分类$游戏" + finalI, name) {
                                @Override
                                protected void whenClick() {
                                    mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                            new PacketBuffer(Unpooled.buffer()
                                                    .writeInt(26))
                                                    .writeString("GUI$mainmenu@entry/" + finalI)
                                                    .writeString("{\"entry\":" + finalI + ",\"sid\":\"" + sid + "\"}")
                                    ));
                                }
                            });
                        }
                    }
                    mc.displayGuiScreen(new HytMainMenu1("mainmenu", buttons));
                }
            }
        } else {
            int size = packetBuffer.readableBytes();
            if (size > 0) {
                System.out.println("Unknown packet id: " + packetId + ", size=" + size);
            }
        }
    }

    public String encode(String string) {
        String s = encode("1qaz2wsx3edc4ds6g4f4g65a7ujm8ik,9ol.0p;/", string);
        return encodeAgain("!QAZ@WSX#EDC$RFV%TGB^YHN&UJM*IK<(OL>)P:?", s);
    }

    public String encodeAgain(String key, String s) {
        return base32.encodeAsString(getKeyBytes(key, s.getBytes())).replaceAll("=", "");
    }

    public static String encode(String key, String s) {
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(getKeyBytes(key, s.getBytes()));
    }

    public static byte[] getKeyBytes(String string, byte[] byArray) {
        byte[] byArray2 = null;
        try {
            Key key = generateKey(string);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(1, key);
            byArray2 = cipher.doFinal(byArray);
            return byArray2;
        } catch (Exception exception) {
            return byArray2;
        }
    }

    private static Key generateKey(String string) {
        SecureRandom secureRandom;
        KeyGenerator keyGenerator = null;
        SecureRandom secureRandom2 = null;
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
            secureRandom = secureRandom2 = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            secureRandom = secureRandom2;
            noSuchAlgorithmException.printStackTrace();
        }
        secureRandom.setSeed(string.getBytes());
        KeyGenerator keyGenerator2 = keyGenerator;
        keyGenerator2.init(secureRandom2);
        return keyGenerator2.generateKey();
    }
}