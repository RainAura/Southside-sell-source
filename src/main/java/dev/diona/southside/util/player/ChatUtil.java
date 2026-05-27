package dev.diona.southside.util.player;

import dev.diona.southside.Southside;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import static dev.diona.southside.Southside.MC.mc;

public class ChatUtil {
    public static final String PRIMARY_COLOR = TextFormatting.BLUE.toString();
    public static final String SECONDARY_COLOR = TextFormatting.GRAY.toString();
    private static final String PREFIX = PRIMARY_COLOR + "[" + SECONDARY_COLOR + Southside.CLIENT_NAME + PRIMARY_COLOR + "] ";

    public static void sendComponent(TextComponentString component) {
        if (mc.player == null) return;
        mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(PREFIX).appendSibling(component));
    }

    public static void sendText(String s) {
        sendComponent(new TextComponentString(s));
    }

    public static void info(String s) {
        sendText(s);
    }
}
