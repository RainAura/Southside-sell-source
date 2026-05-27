package dev.diona.southside.module.modules.player.invmanager;

import dev.diona.southside.module.modules.player.InvManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static dev.diona.southside.Southside.MC.mc;

public abstract class SubComponent {
    public final String name;
    protected InvManager manager;

    public SubComponent(String name, InvManager manager) {
        this.name = name;
        this.manager = manager;
    }

    public abstract boolean match(ItemStack itemStack);

    public abstract ItemStack choose(ItemStack a, ItemStack b);

    public abstract boolean equal(ItemStack a, ItemStack b);
    public ItemStack getBest() {
        ItemStack itemStack = null;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot.getHasStack() && this.match(slot.getStack())) {
                itemStack = this.choose(itemStack, slot.getStack());
            }
        }
        return itemStack;
    }

    public int getBestSlot() {
        ItemStack bestItem = getBest();
        if (bestItem == null) return -1;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (bestItem == slot.getStack()) {
                return i;
            }
        }
        return -1;
    }

    public boolean sort() {
        int bestSlot = this.getBestSlot();
        if (bestSlot == -1) return false;
        ItemStack currentItem = mc.player.inventoryContainer.getSlot(manager.getItemSlot().get(this.name)).getStack();
        if (bestSlot == manager.getItemSlot().get(this.name) || (this.match(currentItem) && this.equal(currentItem, getBest()))) {
            if (this.throwInferior() && InvManager.throwInferior()) {
                for (int i = 9; i < 45; i++) {
                    if (i == bestSlot) continue;
                    Slot slot = mc.player.inventoryContainer.getSlot(i);
                    if (this.match(slot.getStack())) {
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

    public boolean throwInferior() {
        return false;
    }
}