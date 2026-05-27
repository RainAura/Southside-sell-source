package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.text.TextUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatBypass extends Module {
    public final Switch advanced = new Switch("Print Messages Beginning With '!'", true);
    public final Slider minDelayValue = new Slider("Min Delay", 0, 0, 5000, 1);
    public final Slider maxDelayValue = new Slider("Max Delay", 0, 0, 5000, 1);
    private final List<String> scheduled = new LinkedList<>();
    private final TimerUtil timer = new TimerUtil();
    private int delay = 0;

    public ChatBypass(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(minDelayValue.getLabel(), advanced.getValue());
        addDependency(maxDelayValue.getLabel(), advanced.getValue());
        addRangedValueRestrict(minDelayValue, maxDelayValue);
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        timer.reset();
        scheduled.clear();
        delay = 0;
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (timer.hasReached(delay) && !scheduled.isEmpty()) {
            timer.reset();
            delay = MathUtil.getRandomInRange(minDelayValue.getValue().intValue(), maxDelayValue.getValue().intValue());
            String message = scheduled.remove(0);
            mc.getConnection().sendPacketNoEvent(new CPacketChatMessage(message));
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;

        if (event.getPacket() instanceof CPacketChatMessage packet) {
            String message = packet.getMessage();
            if (message.startsWith("/")) return;

            if (advanced.getValue()) {
                String remain = "";
                if (message.startsWith("@!")) {
                    remain = message.substring(2);
                } else if (message.startsWith("!")) {
                    remain = message.substring(1);
                }
                if (remain.isEmpty()) return;
                String[] strings = TextUtil.getGlyph(remain);
                assert strings != null;
                for (int i = 0; i < 4; i++) {
                    if (message.startsWith("@")) {
                        scheduled.add("@" + strings[i]);
                    } else {
                        scheduled.add(strings[i]);
                    }
                }
                event.setCancelled(true);
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (char c : message.toCharArray()) {
                if (c == '@') {
                    stringBuilder.append(c);
                } else if (c >= 33 && c <= 128) {
                    stringBuilder.append(Character.toChars(c + 65248));
                } else {
                    stringBuilder.append(c);
                }
            }
            packet.setMessage(stringBuilder.toString());
        }
    }
}
