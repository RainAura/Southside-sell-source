package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.modules.misc.HackerDetector;
import dev.diona.southside.module.modules.misc.hackerdetector.check.PacketCheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBypass implements PacketCheck {
    HackerDetector hackerDetector = (HackerDetector) Southside.moduleManager.getModuleByClass(HackerDetector.class);
    final String regex = "\\((\\d+)\\)\\s*<([^<>]+)>\\s*(.*)";

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketChat chat) {
//            if (chat.getType() != ChatType.CHAT) return;
            String cleanStr = chat.getChatComponent().getUnformattedText().replace("§r", "").trim();
            if (containsNonWhitelistedCharacters(cleanStr)) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(cleanStr);
                if (matcher.find()) {
                    String speaker = matcher.group(2); // 获取 XXX 部分
                    if (Minecraft.getMinecraft().world.getPlayerEntityByName(speaker) != null) {
                        UUID uuid = Minecraft.getMinecraft().world.getPlayerEntityByName(speaker).getUniqueID();
                        hackerDetector.getPlayData(uuid).addVl(3, "ChatBypass");
                    }
                }
            }
        }
    }

    public static boolean containsNonWhitelistedCharacters(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!isAllowedCharacter(c)) {
                return true; // 字符串中包含不在白名单中的字符
            }
        }
        return false; // 字符串中所有字符都在白名单中
    }

    public static boolean isAllowedCharacter(char c) {
        // 如果字符是中文、英文或者特殊符号，则返回 true，否则返回 false
        return isChineseCharacter(c) || isEnglishCharacter(c) || isSpecialCharacter(c);
    }

    public static boolean isChineseCharacter(char c) {
        // 中文字符的 Unicode 范围通常在 0x4E00 至 0x9FFF
        return (c >= '\u4E00' && c <= '\u9FFF');
    }

    public static boolean isEnglishCharacter(char c) {
        // 英文字符的 Unicode 范围通常在 0x0020 至 0x007E，以及其他特殊字符
        return ((c >= '\u0020' && c <= '\u007E') || (c >= '\u00A0' && c <= '\u00FF'));
    }

    public static boolean isSpecialCharacter(char c) {
        // 特殊符号的 Unicode 范围可根据实际需求进行调整，这里仅列出了一些常见的特殊符号
        return ((c >= '!' && c <= '/') || (c >= ':' && c <= '@') || (c >= '[' && c <= '`')
                || (c >= '{' && c <= '~') || c == '、' || c == '。' || c == '，' || c == '？' || c == '！');
    }

    public static void main(String[] args) {
        System.out.println(containsNonWhitelistedCharacters("(1686) <Key8> 防下号J 我喜欢你♥"));
        ;
        if (containsNonWhitelistedCharacters(" (1686) <Key8> 防下号J 我喜欢你♥")) {
            final String regex = "\\((\\d+)\\)\\s*<([^<>]+)>\\s*(.*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher("(1686) <Key8> 防下号J 我喜欢你♥");
            if (matcher.find()) {
                String speaker = matcher.group(2); // 获取 XXX 部分
                System.out.println(speaker);
            }
        }
    }
}
