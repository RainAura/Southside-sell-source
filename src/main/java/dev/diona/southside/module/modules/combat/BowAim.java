package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.util.player.PredictionUtil;
import dev.diona.southside.util.player.RayCastUtil;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.player.RotationUtil;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Comparator;
import java.util.Optional;

public class BowAim extends Module {
    public BowAim(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public Slider minRangeValue = new Slider("Min Range", 0, 0, 16, 1F);
    public Slider maxRangeValue = new Slider("Max Range", 16, 3, 128, 1F);
    public Slider rotationCount = new Slider("Rotation Count", 3, 1, 20, 1);
    public Switch autoRelease = new Switch("Auto Release", true);
    public Slider releaseCount = new Slider("Release Count", 20, 1, 20, 1);

    public Slider predictValue = new Slider("Predict", 2F, 0, 2, 0.1);

    public Switch markValue = new Switch("Mark", true);
    public Switch rayCastValue = new Switch("RayCast", false);
    public EntityLivingBase target;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= rotationCount.getValue().intValue()) {
            getTarget(minRangeValue.getValue().floatValue(), maxRangeValue.getValue().floatValue()).ifPresent(target -> {
                faceBow(target, true, true, predictValue.getValue().floatValue());
                this.target = target;
                if (mc.player.getItemInUseMaxCount() > releaseCount.getValue().intValue() && autoRelease.getValue() && RotationUtil.targetRotation != null) {
                    mc.player.stopActiveHand();
                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
            });
            return;
        }
        target = null;
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (target == null || !markValue.getValue()) return;
        final var alpha = target.hurtTime * 5;
        Color color = new Color(135, 206, 250, 100 + alpha);
        RenderUtil.boundingESPBoxFilled(PredictionUtil.PredictedTarget(target, 2), color);
    }

    public void faceBow(Entity target, boolean silent, boolean predict, float predictSize) {
        EntityPlayerSP player = mc.player;
        double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0) - (player.posX + (predict ? player.posX - player.prevPosX : 0));
        double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? player.posY - player.prevPosY : 0)) - player.getEyeHeight();
        double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0) - (player.posZ + (predict ? player.posZ - player.prevPosZ : 0));
        double posSqrt = Math.sqrt(posX * posX + posZ * posZ);
        float velocity = player.getItemInUseMaxCount() / 20f;

        velocity = (velocity * velocity + velocity * 2) / 3;
        if (velocity > 1) velocity = 1f;
        Rotation rotation = new Rotation(
                (float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90,
                (float) (-Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - 0.006f * (0.006f * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006f * posSqrt))))
        );

        RayTraceResult result = RayCastUtil.rayCast(rotation, maxRangeValue.getValue().floatValue(), 0F, mc.player, false, 0F, 1F);
        if ((result != null && (result.entityHit == target || (result.typeOfHit == RayTraceResult.Type.ENTITY && Target.isTarget(result.entityHit)))) || !rayCastValue.getValue()) {
            if (silent) {
                RotationUtil.setTargetRotation(rotation, 0);
            } else {
                player.rotationYaw = rotation.yaw;
                player.rotationPitch = rotation.pitch;
            }
        }

    }

    public static Optional<EntityOtherPlayerMP> getTarget(float min, float max) {
        final double minRange = min * min;
        final double maxRange = max * max;
        Vec3d playerLookVec = mc.player.getLook(1.0f);

        return mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityOtherPlayerMP)
                .filter(Entity::isEntityAlive)
                .map(entity -> (EntityOtherPlayerMP) entity)
                .filter(Target::isTarget)
                .sorted(Comparator.comparingDouble(entity -> {
                    Vec3d targetDirection = ((EntityOtherPlayerMP) entity).getPositionEyes(1.0f).subtract(mc.player.getPositionEyes(1.0f));
                    return playerLookVec.dotProduct(targetDirection);
                }).reversed()) // Reversed to have smallest angle first
                .sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity)))
                .filter(entity -> {
                    double distanceSq = mc.player.getDistanceSq(entity);
                    return distanceSq <= maxRange && distanceSq >= minRange;
                })
                .findFirst();
    }
}
