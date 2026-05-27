package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.MoveInputEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.BlockPos;

import static dev.diona.southside.Southside.MC.mc;

public class Eagle extends Module {
    Dropdown sneakMode = new Dropdown("Sneak Mode", "Always", "Always", "Hold");

    public Eagle(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private boolean shouldSneak = false;

    @EventListener
    public void onUpdate(MotionEvent event) {
        if (event.getState() != EventState.PRE) return;
        if (!mc.player.onGround) {
            this.shouldSneak = false;
            return;
        }
        boolean onEdge = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockAir;
//        if (onEdge && sneakMode.getValue().equals("H"))
        this.shouldSneak = onEdge && !(sneakMode.getMode().equals("Hold") && !mc.gameSettings.keyBindSneak.isKeyDown());
    }

    @EventListener
    public void onMoveInput(MoveInputEvent event) {
        if (this.shouldSneak) {
            event.setSneak(true);
        } else if (event.isSneak() && this.sneakMode.getMode().equals("Hold")) {
            event.setSneak(false);
        }
    }
}
