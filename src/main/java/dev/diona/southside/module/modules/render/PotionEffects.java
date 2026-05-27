package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.HUD;
import dev.diona.southside.gui.hud.PotionHud;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;

public class PotionEffects extends Module {
    public final HUD hud = new HUD("Potion HUD", new PotionHud(10, 80, 1, 1));
    public PotionEffects(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }
}
