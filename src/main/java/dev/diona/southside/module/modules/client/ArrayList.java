package dev.diona.southside.module.modules.client;

import cc.polyfrost.oneconfig.config.options.impl.HUD;
import dev.diona.southside.gui.hud.ArrayListHud;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.DefaultEnabled;

@DefaultEnabled
public class ArrayList extends Module {
    public final HUD hud = new HUD("ArrayList", new ArrayListHud(
            0, 0, 2, 1
    ));
    public ArrayList(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(((ArrayListHud) hud.getValue()).customColor1.getLabel(), () -> ((ArrayListHud) hud.getValue()).colorValue.getMode().equals("Custom"));
        addDependency(((ArrayListHud) hud.getValue()).customColor2.getLabel(), () -> ((ArrayListHud) hud.getValue()).colorValue.getMode().equals("Custom"));
        addDependency(((ArrayListHud) hud.getValue()).customSpeed.getLabel(), () -> ((ArrayListHud) hud.getValue()).colorValue.getMode().equals("Custom"));
        addListener(((ArrayListHud) hud.getValue()).customColor1.getLabel(), ((ArrayListHud) hud.getValue())::reloadRainbow);
        addListener(((ArrayListHud) hud.getValue()).customColor2.getLabel(), ((ArrayListHud) hud.getValue())::reloadRainbow);
    }
}
