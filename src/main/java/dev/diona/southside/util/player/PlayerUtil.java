package dev.diona.southside.util.player;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import static dev.diona.southside.Southside.MC.mc;

public class PlayerUtil {
    public static boolean isBlockUnder(final double height) {
        for (int offset = 0; offset < height; offset += 2) {
            final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.world.getCollisionBoxes(mc.player, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockUnder(final double height, final EntityLivingBase entity) {
        for (int offset = 0; offset < height; offset += 2) {
            final AxisAlignedBB bb = entity.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.world.getCollisionBoxes(entity, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockUnder(BlockPos blockPos) {
        for (int i = (int) (blockPos.getY() - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(blockPos.getX(),
                    i, blockPos.getZ());
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return true;
        }
        return false;
    }
}
