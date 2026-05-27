package dev.diona.southside.module.modules.movement;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;

public class KeepSprint extends Module {
    private static KeepSprint INSTANCE;

    public KeepSprint(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public static boolean isInstanceEnabled() {
        return INSTANCE.isEnabled();
    }
}
