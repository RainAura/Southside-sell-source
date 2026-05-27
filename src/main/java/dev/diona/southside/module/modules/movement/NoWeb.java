package dev.diona.southside.module.modules.movement;


import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;


public class NoWeb extends Module {
    public final Switch range = new Switch("Range", true);

    public NoWeb(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public static boolean canSprint = true;

    private static NoWeb INSTANCE;

    @Override
    public boolean onDisable() {
        canSprint = true;
        return super.onDisable();
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.isInWeb && (mc.gameSettings.keyBindJump.isKeyDown() || mc.player.motionY > 0)) {
            canSprint = false;
        } else {
            canSprint = true;
        }
        if (range.getValue()) {
            runSearch(this::handle);
        } else {
            BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            handle(blockPos);
        }
        mc.player.isInWeb = false;
        mc.player.inWater = false;
    }

    private void handle(BlockPos blockPos) {
        Block block = mc.world.getBlockState(blockPos).getBlock();
        if (block instanceof BlockWeb || block instanceof BlockLiquid) {
            mc.player.connection.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
            mc.player.connection.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
        }
    }

    private void runSearch(Consumer<BlockPos> action) {
        for (int x = -2; x < 2; x++) {
            for (int y = -2; y < 4; y++) {
                for (int z = -2; z < 2; z++) {
                    final BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                    action.accept(pos);
                }
            }
        }
    }

    public static boolean isEnable() {
        return INSTANCE.isEnabled();
    }
}
