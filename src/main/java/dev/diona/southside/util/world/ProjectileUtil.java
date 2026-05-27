package dev.diona.southside.util.world;

import dev.diona.southside.module.modules.player.Blink;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import org.lwjglx.opengl.GL11;

import java.awt.*;

import static dev.diona.southside.Southside.MC.mc;

public class ProjectileUtil {
    public static ProjectileHit predict(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, double motionSlowdown, double size, double gravity, boolean draw) {
        RayTraceResult landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;
        int ticks = 0;

        if (draw) {
            RenderUtil.enableRender3D(true);
            RenderUtil.color((new Color(230, 230, 230)).getRGB());
            GL11.glLineWidth(2.0F);
            GL11.glBegin(3);
        }

        while (!hasLanded && posY > -60.0D) {
            ticks++;
            Vec3d posBefore = new Vec3d(posX, posY, posZ);
            Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = mc.world.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3d(posX, posY, posZ);
            posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3d(landingPosition.hitVec.x, landingPosition.hitVec.y, landingPosition.hitVec.z);
            }


            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);


            var entityList = mc.world.getEntitiesWithinAABB(Entity.class, arrowBox.add(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));

            for (int i = 0; i < entityList.size(); i++) {
                Entity var18 = entityList.get(i);
                if (var18.canBeCollidedWith() && var18 != mc.player) {
                    AxisAlignedBB var2 = var18.getEntityBoundingBox().expand(size, size, size);

                    RayTraceResult possibleEntityLanding = var2.calculateIntercept(posBefore, posAfter);
                    if (possibleEntityLanding != null) {
                        hitEntity = true;
                        hasLanded = true;
                        landingPosition = possibleEntityLanding;
                        possibleEntityLanding.entityHit = var18;
                    }
                }
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            BlockPos var35 = new BlockPos(posX, posY, posZ);
            Block var36 = mc.world.getBlockState(var35).getBlock();
            if (var36.getBlockState().getBaseState().getMaterial() == Material.WATER) {
                motionX *= 0.6D;
                motionY *= 0.6D;
                motionZ *= 0.6D;
            } else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }

            motionY -= gravity;
            if (draw) {
                GL11.glVertex3d(posX - mc.getRenderManager().getRenderPosX(), posY - mc
                        .getRenderManager().getRenderPosY(), posZ - mc
                        .getRenderManager().getRenderPosZ());
            }
        }

        return new ProjectileHit(posX, posY, posZ, hitEntity, hasLanded, landingPosition, ticks);
    }
    public static boolean predictBox(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, double motionSlowdown, double size, double gravity, java.util.List<Vec3d> pos, float boxWidth, float boxHeight) {
        RayTraceResult landingPosition = null;
        boolean hasLanded = false;
        int ticks = 0, ticksReleased = 0;

        int posCount = pos.size();
//        RenderUtil.enableRender3D(true);
//        RenderUtil.color((new Color(230, 230, 230)).getRGB());
//        GL11.glLineWidth(2.0F);
//        GL11.glBegin(3);

        while (!hasLanded && posY > -60.0D) {
            if (ticks >= pos.size()) {
                break;
            }
            Vec3d posBefore = new Vec3d(posX, posY, posZ);
            Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = mc.world.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3d(posX, posY, posZ);
            posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                return false;
            }


            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);

            Vec3d vec = pos.get(ticksReleased);
            if (arrowBox.intersects(new AxisAlignedBB(
                    vec.x - boxWidth,
                    vec.y - boxHeight / 2F,
                    vec.z - boxWidth,
                    vec.x + boxWidth,
                    vec.y + boxHeight,
                    vec.z + boxWidth
            ))) return true;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            BlockPos var35 = new BlockPos(posX, posY, posZ);
            Block var36 = mc.world.getBlockState(var35).getBlock();
            if (var36.getBlockState().getBaseState().getMaterial() == Material.WATER) {
                motionX *= 0.6D;
                motionY *= 0.6D;
                motionZ *= 0.6D;
            } else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }

//            GL11.glVertex3d(posX - mc.getRenderManager().getRenderPosX(), posY - mc
//                    .getRenderManager().getRenderPosY(), posZ - mc
//                    .getRenderManager().getRenderPosZ());

            motionY -= gravity;
            ticks++;
            posCount++;
            ticksReleased += Blink.getReleaseSpeedModifier(ticks, posCount - ticksReleased);
        }
//        GL11.glEnd();
//        RenderUtil.disableRender3D(true);

        return false;
    }

    public record ProjectileHit(double posX, double posY, double posZ, boolean hitEntity, boolean hasLanded,
                                RayTraceResult landingPosition, int ticks) {
    }

    public static class EnderPearlPredictor {
        public double predictX, predictY, predictZ, minMotionY, maxMotionY;

        public EnderPearlPredictor(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            this.predictX = predictX;
            this.predictY = predictY;
            this.predictZ = predictZ;
            this.minMotionY = minMotionY;
            this.maxMotionY = maxMotionY;
        }

        public double assessRotation(Rotation rotation) {
            double mul = 1;
            int cnt = 0;
            for (double rate = 0; rate <= 1; rate += 0.3333) {
                for (int yaw = -1; yaw <= 1; yaw += 1) {
                    for (int pitch = -1; pitch <= 1; pitch += 1) {
                        mul *= assessSingleRotation(new Rotation(rotation.yaw + yaw * 0.5F, rotation.pitch + pitch * 0.5F), MathUtil.interpolate(minMotionY, maxMotionY, rate));
                        cnt++;
                    }
                }
                if (minMotionY == maxMotionY) {
                    break;
                }
            }
            return Math.pow(mul, 1D / cnt);
        }

        private double assessSingleRotation(Rotation rotation, double motionYOffset) {
            rotation.fixPitch();
            final float motionFactor = 1.5F;
            final float gravity = 0.03F;
            final float size = 0.25F;
            final float motionSlowdown = 0.99F;

            double posX = predictX - (MathHelper.cos(rotation.yaw / 180.0F * 3.1415927F) * 0.16F);
            double posY = predictY + mc.player.getEyeHeight() - 0.10000000149011612D;
            double posZ = predictZ - (MathHelper.sin(rotation.yaw / 180.0F * 3.1415927F) * 0.16F);

            double motionX = (-MathHelper.sin(rotation.yaw / 180.0F * 3.1415927F) * MathHelper.cos(rotation.pitch / 180.0F * 3.1415927F)) * 0.4D;

            double motionY = -MathHelper.sin((rotation.pitch) / 180.0F * 3.1415927F) * 0.4D;


            double motionZ = (MathHelper.cos(rotation.yaw / 180.0F * 3.1415927F) * MathHelper.cos(rotation.pitch / 180.0F * 3.1415927F)) * 0.4D;
            float distance = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;

            motionY += motionYOffset;

            ProjectileUtil.ProjectileHit projectileHit = ProjectileUtil.predict(posX, posY, posZ, motionX, motionY, motionZ, motionSlowdown, size, gravity, false);

            if (!projectileHit.hasLanded()) return 0.05D;

            EnumFacing facing = projectileHit.landingPosition().sideHit;

            BlockPos landPos = projectileHit.landingPosition().getBlockPos().add(facing.getDirectionVec());

            return ((facing == EnumFacing.UP || facing == EnumFacing.DOWN) ? assessPlainBlockPos(landPos) : assessSideBlockPos(landPos, facing)) * distanceFunction(projectileHit.ticks);
        }

        private double assessPlainBlockPos(BlockPos pos) {
            double mul = 1;
            mul *= Math.pow(assessSingleBlockPos(pos.add(0, 0, 0)), 2);
            mul *= assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= assessSingleBlockPos(pos.add(-1, 0, 0));
            mul *= assessSingleBlockPos(pos.add(0, 0, 1));
            mul *= assessSingleBlockPos(pos.add(0, 0, -1));
            return Math.pow(mul, 1 / 6D);
        }

        private double assessSideBlockPos(BlockPos pos, EnumFacing facing) {
            double mul = 1;
            mul *= Math.pow(assessSingleBlockPos(pos.add(0, 0, 0)), 2);
            mul *= assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= assessSingleBlockPos(pos.add(facing.getDirectionVec()));
            return Math.pow(mul, 1 / 3D);
        }

        private double assessSingleBlockPos(BlockPos pos) {
            for (int y = 0; y >= -5; y--) {
                IBlockState blockState = mc.world.getBlockState(pos.add(0, y, 0));
                if (y == 0 && blockState.isFullBlock()) return 0.4D;
                if (blockState.isFullBlock()) return 1D;
                else if (blockState.isTopSolid()) return 0.99D;
            }
            return 0.05D;
        }

        private double distanceFunction(double d) {
            d /= 1000;
            return (d + 3) / (d + 2) / (3 / 2D);
        }
    }
}
