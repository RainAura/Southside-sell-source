package dev.diona.southside.module.modules.player;

import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemBlock;

import static dev.diona.southside.Southside.MC.mc;

public class FastPlace extends Module {
    public final Slider delayValue = new Slider("Delay", 0, 0, 3, 1);

    public FastPlace(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (!(event.getState() == EventState.PRE)) return;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
            mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, this.delayValue.getValue().intValue());
        }
    }
}
