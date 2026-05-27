package dev.diona.southside.util.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import static dev.diona.southside.Southside.MC.mc;

public class MovementUtil {
    public static boolean isMoving() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);
    }
    public static boolean isMoving(EntityPlayer player) {
        return player != null && (player.moveForward != 0f || player.moveStrafing != 0f);
    }

    public static double getDirection() {
        var rotationYaw = mc.player.rotationYaw;
        if (mc.player.movementInput.moveForward < 0f) rotationYaw += 180f;
        var forward = 1f;
        if (mc.player.movementInput.moveForward < 0f) forward = -0.5f;
        else if (mc.player.movementInput.moveForward > 0f) forward = 0.5f;
        if (mc.player.movementInput.moveStrafe > 0f) rotationYaw -= 90f * forward;
        if (mc.player.movementInput.moveStrafe < 0f) rotationYaw += 90f * forward;
        return Math.toRadians(rotationYaw);
    }

    public static float getMovingYaw() {
        return (float) (getDirection() * 180F / Math.PI);
    }

    public static double getPlayerSpeed(EntityPlayer player) {
        return Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ); // 返回水平速度
    }
}
