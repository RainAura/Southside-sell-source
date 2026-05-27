package dev.diona.southside.module.modules.misc;

import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.SPacketChat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiSpammer extends Module {
    public AntiSpammer(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    Pattern pattern = Pattern.compile("(\\([0-9]+\\)) §r§f<([^>]+)> (.*)");

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketChat chat) {
            String message = chat.getChatComponent().getFormattedText();
            Matcher matcher = pattern.matcher(message.trim());
            if (matcher.find()) {
                if (matcher.group(2).contains("SilenceFix") || matcher.group(2).contains("xinxin.fan")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
