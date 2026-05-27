package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BlockBBEvent extends Event {
    private final BlockPos blockPos;
    private AxisAlignedBB axisAlignedBB;

    public BlockBBEvent(BlockPos blockPos, AxisAlignedBB axisAlignedBB) {
        this.blockPos = blockPos;
        this.axisAlignedBB = axisAlignedBB;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
