package dev.diona.southside.module.modules.movement;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import dev.diona.southside.event.events.StrafeEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.Binding;
import dev.diona.southside.module.annotations.DefaultEnabled;
import dev.diona.southside.util.player.MovementUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import org.lwjglx.input.Keyboard;

@DefaultEnabled
public class Sprint extends Module {
    private static Sprint INSTANCE;
    public Dropdown modeValue = new Dropdown( "Mode", "Normal", "Normal", "Omni");

    public Sprint(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public void onUpdate(StrafeEvent event) {
        mc.gameSettings.keyBindSprint.setPressed(true);
    }

    @Override
    public boolean onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(false);
        return true;
    }

    @Override
    public String getSuffix() {
        return modeValue.getMode();
    }

    public static boolean getOmni() {
         return MovementUtil.isMoving() && INSTANCE.modeValue.getMode().equals("Omni");
    }

    public static boolean getSprint() {
        return INSTANCE.isEnabled() && MovementUtil.isMoving();
    }
}
