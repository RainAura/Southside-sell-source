package dev.diona.southside.module.modules.player;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;

public class NoJumpDelay extends Module {
    private static NoJumpDelay INSTANCE;
    public NoJumpDelay(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public static boolean isNoDelay() {
        return INSTANCE.isEnabled();
    }
}
