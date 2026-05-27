package dev.diona.southside.util.player;


import dev.diona.southside.event.events.StrafeEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static dev.diona.southside.Southside.MC.mc;

public class Rotation implements Cloneable {
    public float yaw, pitch;
    public double distanceSq;

    public Runnable task;
    public Runnable postTask;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation(Vec3d from, Vec3d to) {
        final Vec3d diff = to.subtract(from);

        this.yaw = MathHelper.wrapDegrees(
                (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90F
        );
        this.pitch = MathHelper.wrapDegrees(
                (float) (-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z))))
        );
    }

    public Rotation onApply(Runnable task) {
        this.task = task;
        return this;
    }

    public Rotation onPost(Runnable task) {
        this.postTask = task;
        return this;
    }

    public Rotation(Vec3d to) {
        this(new Vec3d(0, 0, 0), to);
    }

    @Override
    public Rotation clone() throws CloneNotSupportedException {
        return (Rotation) super.clone();
    }

    public void apply() {
        mc.player.rotationYaw = this.yaw;
        mc.player.rotationPitch = this.pitch;
    }

    public void applyStrafe(StrafeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();
        float factor = strafe * strafe + forward * forward;

        int angleDiff = (int) ((MathHelper.wrapDegrees(mc.player.rotationYaw - yaw - 22.5f - 135.0f) + 180.0D) / (45.0D));
        //alert("Diff: " + angleDiff + " friction: " + friction + " factor: " + factor);
        float calcYaw = yaw + 45.0f * angleDiff;

        float calcMoveDir = Math.max(Math.abs(strafe), Math.abs(forward));
        calcMoveDir = calcMoveDir * calcMoveDir;
        var calcMultiplier = MathHelper.sqrt(calcMoveDir / Math.min(1.0f, calcMoveDir * 2.0f));
        switch (angleDiff) {
            case 1, 3, 5, 7, 9 -> {
                boolean b = Math.abs(forward) > 0.005 && Math.abs(strafe) > 0.005;
                if ((Math.abs(forward) > 0.005 || Math.abs(strafe) > 0.005) && !(b)) {
                    friction = friction / calcMultiplier;
                } else if (b) {
                    friction = friction * calcMultiplier;
                }
            }
        }

        if (factor >= 1.0E-4F) {
            factor = MathHelper.sqrt(factor);

            if (factor < 1.0F) {
                factor = 1.0F;
            }

            factor = friction / factor;
            strafe *= factor;
            forward *= factor;

            float yawSin = MathHelper.sin((float) (calcYaw * 0.017453292F));
            float yawCos = MathHelper.cos((float) (calcYaw * 0.017453292F));

            mc.player.motionX += strafe * yawCos - forward * yawSin;
            mc.player.motionZ += forward * yawCos + strafe * yawSin;
        }
//        event.setCancelled(true);
    }

    public void fixPitch() {
        if (this.pitch > 90F) this.pitch = 90F;
        if (this.pitch < -90F) this.pitch = -90F;
    }

    public void fixedSensitivity(float sensitivity) {
        float f = sensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

        yaw -= yaw % gcd;
        pitch -= pitch % gcd;
    }
}