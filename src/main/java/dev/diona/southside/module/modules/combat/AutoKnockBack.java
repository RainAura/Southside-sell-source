package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.module.modules.player.Blink;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.*;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;
import java.util.Comparator;
import java.util.Optional;

public class AutoKnockBack extends Module {
    public AutoKnockBack(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public Slider minRangeValue = new Slider("Min Range", 3, 2, 10, 0.1F);
    public Slider maxRangeValue = new Slider("Max Range", 8, 2, 16, 0.1F);

    public Slider delayValue = new Slider("Delay", 500, 0, 1000, 50);
    public Slider predictValue = new Slider("Predict", 1.5, 0, 2, 0.1);

    public Switch autoSwitchValue = new Switch("AutoSwitch", true);

    public Switch markValue = new Switch("Mark", true);

    TimerUtil delay = new TimerUtil();

    int currentSlot = -1;

    EntityLivingBase target;

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        this.addRangedValueRestrict(minRangeValue, maxRangeValue);
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (!delay.hasReached(delayValue.getValue().doubleValue()) || Blink.isInstanceEnabled()
                || !((KillAura)Southside.moduleManager.getModuleByClass(KillAura.class)).targets.isEmpty()
                || RotationUtil.targetRotation != null
                || Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled()
                || mc.player.isHandActive()
        )
            return;
        Optional<EntityOtherPlayerMP> target = getTarget(minRangeValue.getValue().floatValue(), maxRangeValue.getValue().floatValue());
        this.target = null;
        if (target.isEmpty()) return;
        if (mc.player.inventory.getCurrentItem().getItem() == Items.SNOWBALL || mc.player.inventory.getCurrentItem().getItem() == Items.EGG || autoSwitchValue.getValue() && getThrowSlot() != -1) {
            this.target = target.get();
            EntityOtherPlayerMP player = target.get();
            double distance = mc.player.getDistance(player);
            float predict = (float) distance * predictValue.getValue().floatValue();
            if (player.fakePlayer != null) {
                player = player.fakePlayer;
                predict -= 1;
            }
            Rotation targetRotation = RotationUtil.toRotation(
                    player.getPositionEyes(predict), 0F
            );
            RayTraceResult rayCast = RayCastUtil.rayCast(targetRotation, distance, 0f, mc.player, false, 0F, 0F);
            if (rayCast == null) {
                ChatUtil.info("RayTraceResult == null !!!!!!!!!!!");
                Thread.dumpStack();
                return;
            }
            if (rayCast.typeOfHit == RayTraceResult.Type.BLOCK) {
                return;
            }
            targetRotation.pitch = (float) MathUtil.clamp(targetRotation.pitch, -90, 90);
            RotationUtil.setTargetRotation(targetRotation, 0);
        }


    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (Blink.isInstanceEnabled() || Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled()) return;
        if (event.getState() == EventState.POST) {
            if (autoSwitchValue.getValue()) {
                if (mc.player.inventory.getCurrentItem().getItem() == Items.SNOWBALL || mc.player.inventory.getCurrentItem().getItem() == Items.EGG || mc.player.inventory.getCurrentItem().getItem() == Items.AIR) {
                    if (currentSlot != -1) {
                        mc.player.inventory.currentItem = currentSlot;
                        currentSlot = -1;
                    }
                } else if (RotationUtil.targetRotation != null && target != null && ((KillAura) Southside.moduleManager.getModuleByClass(KillAura.class)).targets.isEmpty() && mc.player.inventory.getCurrentItem().getItem() != Items.BOW) {
                    int slot = getThrowSlot();
                    if (slot != -1) {
                        currentSlot = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = slot;
                    }
                }
            }
            if (mc.player.inventory.getCurrentItem().getItem() == Items.SNOWBALL || mc.player.inventory.getCurrentItem().getItem() == Items.EGG) {
                if (RotationUtil.targetRotation != null) {
                    mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                    delay.reset();
                }
            }
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (target == null || !markValue.getValue()) return;
        final var alpha = target.hurtTime * 5;
        Color color = new Color(255, 255, 153, 100 + alpha);
        RenderUtil.boundingESPBoxFilled(PredictionUtil.PredictedTarget(target, 2), color);
    }

    int getThrowSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.SNOWBALL || mc.player.inventory.getStackInSlot(i).getItem() == Items.EGG) {
                return i;
            }
        }
        return -1;
    }

    public static Optional<EntityOtherPlayerMP> getTarget(float min, float max) {
        final double minRange = min * min;
        final double maxRange = max * max;
        return mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityOtherPlayerMP)
                .filter(Entity::isEntityAlive)
                .map(entity -> (EntityOtherPlayerMP) entity)
                .filter(Target::isTarget)
                .filter(entityLivingBase -> mc.player.getDistanceSq(entityLivingBase) <= maxRange && mc.player.getDistanceSq(entityLivingBase) >= minRange)
                .min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity)));
    }
}
