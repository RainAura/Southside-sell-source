package dev.diona.southside.module.modules.player;

import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;

import static dev.diona.southside.Southside.MC.mc;

public class AutoTool extends Module {
    public AutoTool(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() == EventState.POST) return;
        if (mc.playerController.getIsHittingBlock() && mc.objectMouseOver.getBlockPos() != null) {
            Block block = mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
            int bestSlot = mc.player.inventory.currentItem;
            float f = mc.player.getHeldItemMainhand().getDestroySpeed(block);
            for (int i = 36; i < 45; i++) {
                ItemStack curSlot = mc.player.inventoryContainer.getSlot(i).getStack();
                if ((((curSlot.getItem() instanceof net.minecraft.item.ItemTool) && !InventoryUtil.isSharpAxe(curSlot)) || ((curSlot.getItem() instanceof ItemSword)) || ((curSlot.getItem() instanceof net.minecraft.item.ItemShears))) &&
                        (curSlot.getDestroySpeed(block) > f)) {
                    bestSlot = i - 36;
                    f = curSlot.getDestroySpeed(block);
                }
            }

            if (mc.player.inventory.currentItem != bestSlot) {
                mc.player.inventory.currentItem = bestSlot;
                mc.playerController.updateController();
            }
        }
    }
}
