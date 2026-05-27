package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;

public class SmoothZoom extends Module {
    private static SmoothZoom INSTANCE;

    public SmoothZoom(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public final Switch disableSmoothCamera = new Switch("Disable SmoothCamera", true);

    public static boolean doSmooth() {
        return INSTANCE.isEnabled();
    }

    public static boolean enableSmoothCamera() {
        return !INSTANCE.disableSmoothCamera.getValue() || !INSTANCE.isEnabled();
    }
}
