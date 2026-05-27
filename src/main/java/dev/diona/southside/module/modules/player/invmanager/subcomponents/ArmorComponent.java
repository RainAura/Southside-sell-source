package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import static dev.diona.southside.Southside.MC.mc;

public class ArmorComponent extends SubComponent {
    private final int armorSlot;

    public ArmorComponent(int armorSlot, String name, InvManager manager) {
        super(name, manager);
        this.armorSlot = armorSlot;
    }

    public int getArmorSlot() {
        return armorSlot;
    }

    @Override
    public boolean match(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemArmor armor) {
            switch (this.armorSlot) {
                case 5 -> {
                    return armor.armorType == EntityEquipmentSlot.HEAD;
                }
                case 6 -> {
                    return armor.armorType == EntityEquipmentSlot.CHEST;
                }
                case 7 -> {
                    return armor.armorType == EntityEquipmentSlot.LEGS;
                }
                case 8 -> {
                    return armor.armorType == EntityEquipmentSlot.FEET;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return InventoryUtil.getArmorScore(a) > InventoryUtil.getArmorScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return InventoryUtil.getArmorScore(a) == InventoryUtil.getArmorScore(b);
    }

    @Override
    public boolean sort() {
        int bestSlot = this.getBestSlot();
        if (bestSlot == -1) return false;
        ItemStack bestItem = mc.player.inventoryContainer.getSlot(bestSlot).getStack();
        Slot armor = mc.player.inventoryContainer.getSlot(armorSlot);
        if (this.equal(bestItem, armor.getStack())) {
            manager.drop(bestSlot);
        } else if (this.choose(armor.getStack(), bestItem) == bestItem) {
            if (armor.getHasStack()) {
                manager.drop(armorSlot);
            }
            manager.click(bestSlot, 1, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean throwInferior() {
        return true;
    }
}
