package dev.diona.southside.module.modules.client;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import net.minecraft.client.gui.GuiScreen;

public class Debug extends Module {
    private GuiScreen screen;
    public Debug(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public boolean onEnable() {
        super.onEnable();
        toggle();
        return true;
    }
}
