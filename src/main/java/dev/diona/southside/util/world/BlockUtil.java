package dev.diona.southside.util.world;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import static dev.diona.southside.Southside.MC.mc;

public class BlockUtil {
    public static boolean isValidBock(BlockPos blockPos) {
        final Block block = mc.world.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }

    public static boolean isAirBlock(final BlockPos blockPos) {
        final Block block = Minecraft.getMinecraft().world.getBlockState(blockPos).getBlock();
        return block instanceof BlockAir;
    }
}
