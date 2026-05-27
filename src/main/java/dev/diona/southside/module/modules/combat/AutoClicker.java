package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.TimerUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.RandomUtils;

public class AutoClicker extends Module {
    private static AutoClicker INSTANCE;
    public Slider minCpsValue = new Slider("Min CPS", 6.0, 0.0, 20.0, 1.0);
    public Slider maxCpsValue = new Slider("Max CPS", 8.0, 0.0, 20.0, 1.0);
    public Switch leftValue = new Switch("Left Click", true);

    private long nextDelay;
    TimerUtil leftTimer = new TimerUtil();

    public AutoClicker(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        this.addRangedValueRestrict(minCpsValue, maxCpsValue);
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (leftTimer.hasReached(nextDelay) && leftValue.getValue() && mc.gameSettings.keyBindAttack.isKeyDown() && !mc.playerController.getIsHittingBlock()) {
            leftTimer.reset();
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
            nextDelay = (long) (1000 / RandomUtils.nextDouble(minCpsValue.getValue().doubleValue(), maxCpsValue.getValue().doubleValue()));
        }
    }

    public static boolean resetCounter() {
        return INSTANCE.isEnabled();
    }
}
