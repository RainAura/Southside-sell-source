package dev.diona.southside.util.player;

import com.google.common.base.Predicates;
import dev.diona.southside.util.misc.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

import static dev.diona.southside.Southside.MC.mc;

public final class RayCastUtil {

    public static RayTraceResult rayCast(final Rotation rotation, final double range, final float expand, Entity entity, boolean throughWall, float predict, float predictPlayer) {
//        final float partialTicks = mc.getTimer().renderPartialTicks;
        RayTraceResult objectMouseOver;
        if (entity != null && mc.world != null) {
            objectMouseOver = entity.rayTraceCustom(range, rotation.yaw, rotation.pitch, predictPlayer);
            double d1 = range;
            final Vec3d vec3 = entity.getPositionEyes(predictPlayer);

            if (objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && !throughWall) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
                // RayCastUtil.rayCast(new Rotation(mc.player.rotationYaw, mc.player.rotationPitch), 3, 0F, false)
            }

            final Vec3d vec31 = mc.player.getVectorForRotation(rotation.pitch, rotation.yaw);
            final Vec3d vec32 = vec3.add(vec31.x * range, vec31.y * range, vec31.z * range);
            Entity pointedEntity = null;
            Vec3d vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().add(vec31.x * range, vec31.y * range, vec31.z * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                if (entity1 instanceof FakePlayer) continue;
                if (entity1.getUniqueID().equals(mc.player.getUniqueID())) continue;
                float predict2 = predict;
                Entity target = entity1;
                if (entity1 instanceof EntityPlayer player && player.fakePlayer != null) {
                    target = player.fakePlayer;
                    predict2 -= 1F;
                }

                final float f1 = target.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand(f1, f1, f1);

                if (predict2 != 0) {
                    axisalignedbb = axisalignedbb.offset(
                            (target.posX - target.lastTickPosX) * predict2,
                            (target.posY - target.lastTickPosY) * predict2,
                            (target.posZ - target.lastTickPosZ) * predict2
                    );
                }

                final RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new RayTraceResult(pointedEntity, vec33);
            }

            return objectMouseOver;
        }

        return null;
    }

    public static List<RayTraceResult> rayCastList(final Rotation rotation, final double range, final float expand, Entity entity, boolean throughWall, float predict, float predictPlayer) {
        List<RayTraceResult> result = new ArrayList<>();
        if (entity != null && mc.world != null) {
            double d1 = range;
            final Vec3d vec3 = entity.getPositionEyes(predictPlayer);

            final Vec3d vec31 = mc.player.getVectorForRotation(rotation.pitch, rotation.yaw);
            final Vec3d vec32 = vec3.add(vec31.x * range, vec31.y * range, vec31.z * range);
            Vec3d vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().add(vec31.x * range, vec31.y * range, vec31.z * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                Entity pointedEntity = null;

                if (entity1 instanceof FakePlayer) continue;
                if (entity1.getUniqueID().equals(mc.player.getUniqueID())) continue;

                float predict2 = predict;
                Entity target = entity1;
                if (entity1 instanceof EntityPlayer player && player.fakePlayer != null) {
                    target = player.fakePlayer;
                    predict2 -= 1F;
                }

                final float f1 = target.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand(f1, f1, f1);

                if (predict != 0) {
                    axisalignedbb = axisalignedbb.offset(
                            (target.posX - target.lastTickPosX) * predict2,
                            (target.posY - target.lastTickPosY) * predict2,
                            (target.posZ - target.lastTickPosZ) * predict2
                    );
                }

                final RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                } else if (movingobjectposition != null) {
                    pointedEntity = entity1;
                    vec33 = movingobjectposition.hitVec;
                }

                if (pointedEntity != null) {
                    result.add(new RayTraceResult(pointedEntity, vec33));
                }
            }
        }

        return result;
    }

    public static boolean overBlock(final Rotation rotation, final EnumFacing enumFacing, final BlockPos pos, final boolean strict) {
        final RayTraceResult movingObjectPosition = mc.player.rayTraceCustom(4.5f, rotation.yaw, rotation.pitch);

        if (movingObjectPosition == null) return false;

        final Vec3d hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) return false;

        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }
}