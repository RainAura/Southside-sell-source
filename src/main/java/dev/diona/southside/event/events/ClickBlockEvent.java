package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ClickBlockEvent extends Event {
    private final BlockPos blockPos;
    private final EnumFacing enumFacing;

    public ClickBlockEvent(BlockPos blockPos, EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public EnumFacing getEnumFacing() {
        return enumFacing;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
