package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

import static dev.diona.southside.Southside.MC.mc;

public class PotionComponent extends SubComponent {
    public PotionComponent(InvManager manager) {
        super("Potion", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemPotion;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return InventoryUtil.getPotionScore(a) > InventoryUtil.getPotionScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return InventoryUtil.getPotionScore(a) == InventoryUtil.getPotionScore(b);
    }

    public boolean sort() {
        int bestSlot = this.getBestSlot();
        if (bestSlot == -1) return false;
        ItemStack currentItem = mc.player.inventoryContainer.getSlot(manager.getItemSlot().get(this.name)).getStack();
        if (bestSlot == manager.getItemSlot().get(this.name) || (this.match(currentItem) && this.equal(currentItem, getBest()))) {
            if (InvManager.throwDebuff()) {
                for (int i = 9; i < 45; i++) {
                    Slot slot = mc.player.inventoryContainer.getSlot(i);
                    if (this.match(slot.getStack()) && InventoryUtil.getPotionScore(slot.getStack()) < 0) {
                        manager.drop(i);
                        return true;
                    }
                }
            }
            return false;
        }
        manager.swap(bestSlot, manager.getItemSlot().get(this.name) - 36);
        return true;
    }

    @Override
    public boolean throwInferior() {
        return true;
    }
}
