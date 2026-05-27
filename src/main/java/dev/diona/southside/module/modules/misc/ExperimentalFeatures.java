package dev.diona.southside.module.modules.misc;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;

public class ExperimentalFeatures extends Module {
    public static ExperimentalFeatures INSTANCE;

    public ExperimentalFeatures(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }
}
