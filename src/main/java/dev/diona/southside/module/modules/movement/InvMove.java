package dev.diona.southside.module.modules.movement;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjglx.input.Keyboard;

public class InvMove extends Module {
    public InvMove(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public Switch sprintValue = new Switch("Sprint", true);

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            KeyBinding[] key = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                    mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight,
                    mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump,mc.gameSettings.keyBindSprint};
            KeyBinding[] array;
            if (sprintValue.getValue()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
            }
            for (int length = (array = key).length, i = 0; i < length; ++i) {
                KeyBinding b = array[i];
                KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
            }
        }
    }
}
