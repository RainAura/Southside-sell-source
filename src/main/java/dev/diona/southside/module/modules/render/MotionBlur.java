package dev.diona.southside.module.modules.render;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;

public class MotionBlur extends Module {
    private static MotionBlur INSTANCE;
    public Slider strengthValue = new Slider("Strength", 1, 0, 3, 0.1);
    public MotionBlur(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public static boolean getEnabled() {
        return INSTANCE.isEnabled();
    }

    public static int getStrength() {
        return INSTANCE.strengthValue.getValue().intValue();
    }
}
