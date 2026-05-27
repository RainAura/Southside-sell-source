package dev.diona.southside.module.modules.combat;

import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.settings.GameSettings;

public class JumpCritical extends Module {
    public JumpCritical(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() == EventState.PRE) {
            if (canCrit() && KillAura.getTarget() != null && !KillAura.getTargets().isEmpty() && (KillAura.getTarget().hurtTime > 6 || KillAura.getTarget().hurtTime == 0)) {
                mc.gameSettings.keyBindJump.setPressed(true);
            } else {
                mc.gameSettings.keyBindJump.setPressed(GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
            }
        }
    }

    boolean canCrit() {
        return mc.player.onGround && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isOnLadder() && !mc.player.isInWeb() && !mc.player.isSneaking() && mc.player.fallDistance == 0F && mc.player.motionY <= 0F && !mc.gameSettings.keyBindJump.isPressed();
    }
}
