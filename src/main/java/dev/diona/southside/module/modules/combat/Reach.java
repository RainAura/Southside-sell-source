package dev.diona.southside.module.modules.combat;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;

public class Reach extends Module {
    private static Reach INSTANCE;
    public final Slider reachValue = new Slider("Range", 3f, 3f, 6f, 0.01f);
    public Reach(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public static double getReach() {
        return reachEnabled() ? INSTANCE.reachValue.getValue().doubleValue() : 3;
    }

    public static boolean reachEnabled() {
        return INSTANCE.isEnabled();
    }
}
