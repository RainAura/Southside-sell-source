package dev.diona.southside.util.player;

import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.modules.misc.Disabler;
import dev.diona.southside.module.modules.movement.Speed;
import dev.diona.southside.util.misc.MathUtil;
import io.netty.channel.epoll.EpollServerChannelConfig;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dev.diona.southside.Southside.MC.mc;

public class RotationUtil {

    public static void initialize() {
        Southside.eventBus.subscribe(RotationUtil.class);
    }

    public static Rotation targetRotation;
    public static Rotation serverRotation = new Rotation(0, 0);
    public static int keepLength, revTick;

    private static List<Double> xzPercents = Arrays.asList(0.5, 0.4, 0.3, 0.2, 0.1, 0.0, -0.1, -0.2, -0.3, -0.4, -0.5);

    private static Runnable postTask;

    static int randomCount = 0;

    @EventListener
    public static void onPacket(HigherPacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayer packet) {

            if (targetRotation != null && (targetRotation.yaw != serverRotation.yaw || targetRotation.pitch != serverRotation.pitch)) {
                packet.setYaw(targetRotation.yaw);
                packet.setPitch(targetRotation.pitch);
                packet.setRotating(true);

                if (targetRotation.task != null) {
                    targetRotation.task.run();
                }

                postTask = targetRotation.postTask;
            }

            if (packet.isRotating()) {
                if (Disabler.getGrimRotation()) {
                    packet.setYaw(packet.getYaw(0F) + randomCount * 360);
                    randomCount += 5;
                    if (randomCount >= 100) randomCount = 0;
                }

                serverRotation = new Rotation(packet.getYaw(0), packet.getPitch(0));
            }
        }
    }

    @EventListener
    public static void onPost(MotionEvent event) {
        if (event.getState() != EventState.POST) return;
        if (postTask != null) {
            postTask.run();
            postTask = null;
        }
    }

    public static Rotation toRotation(final Vec3d vec, float partialTicks) {
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.getEntityBoundingBox().minY +
                mc.player.getEyeHeight(), mc.player.posZ).add(mc.player.motionX * partialTicks, mc.player.motionY * partialTicks, mc.player.motionZ * partialTicks);
        return new Rotation(eyesPos, vec);
    }

    public static double getRotationDifference(Entity entity) {
        final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), 1.0F);

        return getRotationDifference(rotation, new Rotation(mc.player.rotationYaw, mc.player.rotationPitch));
    }

    public static double getRotationDifference(final Rotation rotation) {
        return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
    }

    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.yaw, b.yaw), a.pitch - b.pitch);
    }

    public static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static Vec3d getCenter(AxisAlignedBB bb) {
        return new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
        final float yawDifference = getAngleDifference(targetRotation.yaw, currentRotation.yaw);
        final float pitchDifference = getAngleDifference(targetRotation.pitch, currentRotation.pitch);

        return new Rotation(
                currentRotation.yaw + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
                currentRotation.pitch + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
                ));
    }

    public static Rotation getPlayerRotation() {
        return new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
    }

    public static Vec3d getVec(Entity entity) {
        return new Vec3d(entity.posX, entity.posY, entity.posZ);
    }

    public static Rotation calculateSimple(final Entity entity, double range, double wallRange, float predict, float predictPlayer) {
        // AxisAlignedBB aabb = entity.getEntityBoundingBox().contract(-0.05, -0.05, -0.05).contract(0.05, 0.05, 0.05);
        // range += 0.05;
        // wallRange += 0.05;
        AxisAlignedBB aabb = entity.getEntityBoundingBox();
        if (predict != 0) {
            aabb = aabb.offset(
                    (entity.posX - entity.lastTickPosX) * predict,
                    (entity.posY - entity.lastTickPosY) * predict,
                    (entity.posZ - entity.lastTickPosZ) * predict
            );
        }
        Vec3d eyePos = mc.player.getPositionEyes(predictPlayer);
        Vec3d nearest = new Vec3d(
                MathUtil.clamp(eyePos.x, aabb.minX, aabb.maxX),
                MathUtil.clamp(eyePos.y, aabb.minY, aabb.maxY),
                MathUtil.clamp(eyePos.z, aabb.minZ, aabb.maxZ)
        );
        Rotation rotation = toRotation(nearest, predictPlayer);
        if (nearest.subtract(eyePos).lengthSquared() <= wallRange * wallRange) {
            return rotation;
        }

        RayTraceResult result = RayCastUtil.rayCast(rotation, range, 0F, mc.player, false, predict, predictPlayer);
        final double maxRange = Math.max(wallRange, range);
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit == entity && result.hitVec.subtract(eyePos).lengthSquared() <= maxRange * maxRange) {
            return rotation;
        }

        return null;
    }

    public static Rotation calculate(Entity entity, final double range, final double wallRange, float predict, float predictPlayer) {
        if (mc.player == null) return null;

        if (entity instanceof EntityPlayer player && player.fakePlayer != null && mc.scheduledTasks.size() <= 1) {
            entity = player.fakePlayer;
            predict -= 1F;
        }

//        final double rangeSq = range * range;
//        final double wallRangeSq = wallRange * wallRange;

        Vec3d entityPos = entity.getPositionEyes(predict);

        Rotation normalRotations = toRotation(
                new Vec3d(
                        entityPos.x,
                        MathUtil.clamp(mc.player.getPositionEyes(predictPlayer).y, entity.getEntityBoundingBox().minY, entity.getEntityBoundingBox().maxY),
                        entityPos.z
                ), predictPlayer + (((Speed) Southside.moduleManager.getModuleByClass(Speed.class)).shouldFollow() ? -1F : 0F)
        );

        RayTraceResult normalResult = RayCastUtil.rayCast(normalRotations, range, 0F, mc.player, false, predict, predictPlayer);
        if (normalResult != null && normalResult.typeOfHit == RayTraceResult.Type.ENTITY) {
            return normalRotations;
        }

        Rotation simpleRotation = calculateSimple(entity, range, wallRange, predict, predictPlayer);
        if (simpleRotation != null) return simpleRotation;

//        double yStart = 1, yEnd = 0, yStep = -0.5;
//        if (randomCenter && MathUtil.secureRandom.nextBoolean()) {
//            yStart = 0;
//            yEnd = 1;
//            yStep = 0.5;
//        }
//        for (double yPercent = yStart; Math.abs(yEnd - yPercent) > 1e-3; yPercent += yStep) {
//            double xzStart = 0.5, xzEnd = -0.5, xzStep = -0.1;
//            if (randomCenter) {
//                Collections.shuffle(xzPercents);
//            }
//            for (double xzPercent : xzPercents) {
//                for (int side = 0; side <= 3; side++) {
//                    double xPercent = 0F, zPercent = 0F;
//                    switch (side) {
//                        case 0 -> {
//                            xPercent = xzPercent;
//                            zPercent = 0.5F;
//                        }
//                        case 1 -> {
//                            xPercent = xzPercent;
//                            zPercent = -0.5F;
//                        }
//                        case 2 -> {
//                            xPercent = 0.5F;
//                            zPercent = xzPercent;
//                        }
//                        case 3 -> {
//                            xPercent = -0.5F;
//                            zPercent = xzPercent;
//                        }
//                    }
//                    Vec3d vec3d = getVec(entity).add(
//                            (entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent,
//                            (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent,
//                            (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent);
//                    double distanceSq = vec3d.squareDistanceTo(mc.player.getPositionEyes(0F));
//
//                    Rotation rotation = toRotation(vec3d, predict);
//                    rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
//                    rotation.distanceSq = distanceSq;
//
//                    if (distanceSq <= wallRangeSq) {
//                        RayTraceResult result = RayCastUtil.rayCast(rotation, wallRange, 0F, true);
//                        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
//                            return rotation;
//                        }
//                    }
//
//                    if (distanceSq <= rangeSq) {
//                        RayTraceResult result = RayCastUtil.rayCast(rotation, range, 0F, false);
//                        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
//                            return rotation;
//                        }
//                    }
//                }
//            }
//        }

        return null;
    }

    public static Rotation turn(Rotation targetRotation, String mode, float speed) {
        switch (mode) {
            case "Smooth" -> {
                return new Rotation(MathUtil.interpolateFloat(serverRotation.yaw, targetRotation.yaw, 0.9), MathUtil.interpolateFloat(serverRotation.pitch, targetRotation.pitch, 0.9));
            }
            case "Normal" -> {
                return limitAngleChange(serverRotation, targetRotation, speed);
            }
        }
        return null;
    }

    public static void reset() {
        keepLength = 0;
        if (revTick > 0) {
            targetRotation = new Rotation(targetRotation.yaw - getAngleDifference(targetRotation.pitch, mc.player.rotationYaw) / revTick
                    , targetRotation.pitch - getAngleDifference(targetRotation.pitch, mc.player.rotationPitch) / revTick);
            targetRotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        } else {
            targetRotation = null;
        }
    }

    public static void setTargetRotation(final Rotation rotation, int keepLength) {
        if (Double.isNaN(rotation.yaw) || Double.isNaN(rotation.pitch)
                || rotation.pitch > 90 || rotation.pitch < -90) {
            return;
        }

//        rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        targetRotation = rotation;
        revTick = 0;
        RotationUtil.keepLength = keepLength;
        mc.player.movementYaw = rotation.yaw;
//        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer());
    }

    @EventListener
    public static void onTick(TickEvent event) {
        if (targetRotation != null) {
            keepLength--;
            if (keepLength < 0) {
                if (revTick > 0) {
                    revTick--;
                }
                reset();
            }
        }
    }

    public static Vec3d getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.pitch * 0.017453292F);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static Rotation getRotationBlock(final BlockPos pos, float predict) {
        return new Rotation(mc.player.getPositionEyes(predict), new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int) ((double) (yaw + (float) (360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }
}
