package dev.diona.southside.module.modules.movement;

import dev.diona.southside.event.events.LookEvent;
import dev.diona.southside.event.events.MoveInputEvent;
import dev.diona.southside.event.events.StrafeEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.DefaultEnabled;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.util.math.MathHelper;

@DefaultEnabled
public class StrafeFix extends Module {
    private static StrafeFix INSTANCE;
    private boolean grimStrafe = false;
    private double minimumDistance;

    public StrafeFix(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public boolean onDisable() {
        this.grimStrafe = false;
        return true;
    }

    @EventListener
    public void onStrafe(StrafeEvent event) {
        if (RotationUtil.targetRotation != null) {
            event.setYaw(RotationUtil.targetRotation.yaw);
        }
    }

    @EventListener
    public void onLook(LookEvent event) {
        if (RotationUtil.targetRotation != null) {
            event.setRotation(RotationUtil.targetRotation);
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
//        grimStrafe = false;
//        for (Entity entity : mc.world.loadedEntityList) {
//            if (entity.getUniqueID().equals(mc.player.getUniqueID())) continue;
//            if (entity instanceof EntityPlayer || entity instanceof EntityMob || entity instanceof EntityAnimal) {
//                double disSq = entity.getDistanceSq(mc.player);
//                if (disSq <= 1.69F) {
//                    if (!grimStrafe) {
//                        minimumDistance = disSq;
//                        grimStrafe = true;
//                    } else {
//                        minimumDistance = Math.min(minimumDistance, disSq);
//                    }
//                }
//            }
//        }
    }

    @EventListener
    public void onMovementInput(MoveInputEvent event) {
        if (RotationUtil.targetRotation != null && !(KillAura.getTarget() != null && Speed.INSTANCE.shouldFollow())) {
            final float yaw = RotationUtil.targetRotation.yaw;
            final float forward = event.getMoveForward();
            final float strafe = event.getMoveStrafe();

            final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(getDirection(mc.player.rotationYaw, forward, strafe)));

            if (forward == 0 && strafe == 0) return;

            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0) continue;

                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(getDirection(yaw, predictedForward, predictedStrafe)));
                    final double difference = Math.abs(angle - predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }

            event.setMoveForward(closestForward);
            event.setMoveStrafe(closestStrafe);
        }
    }

    public static double getDirection(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static boolean doFix() {
        return INSTANCE.isEnabled() && RotationUtil.targetRotation != null;
    }

    public static boolean getGrimStrafe() {
        return INSTANCE.isEnabled() && INSTANCE.grimStrafe;
    }

    public static boolean getGrimStrafe(double distance) {
        return getGrimStrafe() && INSTANCE.minimumDistance <= distance * distance;
    }
}
