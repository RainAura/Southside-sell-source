package cc.polyfrost.oneconfig.libs.universal;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

import static dev.diona.southside.Southside.MC.mc;

public class UChat {
    public static void chat(String s) {
        mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(s));
    }

    public static void say(String s) {
        mc.player.sendChatMessage(s);
    }
}
