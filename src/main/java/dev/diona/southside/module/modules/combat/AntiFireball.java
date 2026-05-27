package dev.diona.southside.module.modules.combat;

import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

public class AntiFireball extends Module {

    public AntiFireball(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() != EventState.PRE) return;
        mc.world.getEntities(EntityFireball.class, entityFireball -> entityFireball.getDistanceSq(mc.player) <= 36).forEach(entityFireball -> {
            mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.getConnection().sendPacket(new CPacketUseEntity(entityFireball));
        });
    }
}
