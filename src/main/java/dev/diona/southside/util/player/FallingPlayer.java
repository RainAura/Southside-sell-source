package dev.diona.southside.util.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import static dev.diona.southside.Southside.MC.mc;

public class FallingPlayer {
    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private float yaw;
    private float strafe;
    private float forward;
    private float jumpMovementFactor;

    public FallingPlayer(double x, double y, double z, double motionX, double motionY, double motionZ, float yaw, float strafe, float forward, float jumpMovementFactor) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.yaw = yaw;
        this.strafe = strafe;
        this.forward = forward;
        this.jumpMovementFactor = jumpMovementFactor;
    }

    public FallingPlayer(EntityPlayer player) {
        this(player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ, player.rotationYaw, player.moveStrafing, player.moveForward, player.jumpMovementFactor);
    }

    private void calculateForTick() {
        float sr = strafe * 0.9800000190734863f;
        float fw = forward * 0.9800000190734863f;
        float v = sr * sr + fw * fw;
        if (v >= 0.0001f) {
            v = MathHelper.sqrt(v);
            if (v < 1.0f) {
                v = 1.0f;
            }
            float fixedJumpFactor = jumpMovementFactor;
            if (mc.player.isSprinting()) {
                fixedJumpFactor = fixedJumpFactor * 1.3f;
            }
            v = fixedJumpFactor / v;
            sr *= v;
            fw *= v;
            float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0f);
            float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0f);
            motionX += sr * f2 - fw * f1;
            motionZ += fw * f2 + sr * f1;
        }
        motionY -= 0.08;
        motionY *= 0.9800000190734863;
        x += motionX;
        y += motionY;
        z += motionZ;
        motionX *= 0.91;
        motionZ *= 0.91;
    }

    public void calculate(int ticks) {
        for (int i = 0; i < ticks; i++) {
            calculateForTick();
        }
    }

    public BlockPos findCollision(int ticks) {
        for (int i = 0; i < ticks; i++) {
            Vec3d start = new Vec3d(x, y, z);
            calculateForTick();
            Vec3d end = new Vec3d(x, y, z);
            BlockPos raytracedBlock;
            float w = mc.player.width / 2f;
            if ((raytracedBlock = rayTrace(start, end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(w, 0.0, w), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(-w, 0.0, w), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(w, 0.0, -w), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(-w, 0.0, -w), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(w, 0.0, w / 2f), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(-w, 0.0, w / 2f), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(w / 2f, 0.0, w), end)) != null) return raytracedBlock;
            if ((raytracedBlock = rayTrace(start.add(w / 2f, 0.0, -w), end)) != null) return raytracedBlock;
        }
        return null;
    }

    private BlockPos rayTrace(Vec3d start, Vec3d end) {
        RayTraceResult result = mc.world.rayTraceBlocks(start, end, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit == EnumFacing.UP) {
            return result.getBlockPos();
        } else {
            return null;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public float getYaw() {
        return yaw;
    }

    public float getStrafe() {
        return strafe;
    }

    public float getForward() {
        return forward;
    }

    public float getJumpMovementFactor() {
        return jumpMovementFactor;
    }
}