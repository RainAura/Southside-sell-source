package dev.diona.southside.module.modules.render;

import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;

import static dev.diona.southside.Southside.MC.mc;

public class FullBright extends Module {
    public FullBright(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private float previousGamma;

    @Override
    public boolean onEnable() {
        previousGamma = mc.gameSettings.gammaSetting;
        return true;
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.gammaSetting = 100;
    }

    @Override
    public boolean onDisable() {
        mc.gameSettings.gammaSetting = previousGamma;
        return true;
    }
}
