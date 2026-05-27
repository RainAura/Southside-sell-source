package dev.diona.southside.module.modules.render;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.RotationUpdateEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.event.events.YawUpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

public class Rotations extends Module {

    private float yaw, pitch, prevYaw, prevPitch;

    public Rotations(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }


    @EventListener
    @SuppressWarnings("unused")
    public void onTick(TickEvent event) {
        prevYaw = yaw;
        prevPitch = pitch;
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            yaw = RotationUtil.serverRotation.yaw;
            pitch = RotationUtil.serverRotation.pitch;
        }
    }

    @EventListener
    @SuppressWarnings("unused")
    public void onYawUpdate(YawUpdateEvent event) {
        if (Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled()) return;
        event.setYaw(interpolateAngle(mc.getTimer().renderPartialTicks, prevYaw, yaw));
    }

    @EventListener
    @SuppressWarnings("unused")
    public void onSelfRotation(RotationUpdateEvent event) {
        final Entity entity = event.getEntity();
        float partialTicks = event.getPartialTicks();

        if (entity instanceof EntityPlayerSP && entity.getRidingEntity() == null) {
            if (Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled()) {
                event.setRenderHeadPitch(90);
                return;
            }
            event.setRenderHeadPitch(lerp(partialTicks, prevPitch, pitch));
            event.setRenderHeadYaw(interpolateAngle(partialTicks, prevYaw, yaw) - event.getRenderYawOffset());
        }
    }

    public float interpolateAngle(float p_219805_0_, float p_219805_1_, float p_219805_2_) {
        return p_219805_1_ + p_219805_0_ * MathHelper.wrapDegrees(p_219805_2_ - p_219805_1_);
    }

    public float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }
}
