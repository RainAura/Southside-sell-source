package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.Southside;
import dev.diona.southside.module.modules.player.AutoWeapon;
import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.HashSet;
import java.util.Set;

import static dev.diona.southside.Southside.MC.mc;

public class SwordComponent extends SubComponent {
    public SwordComponent(InvManager manager) {
        super("Sword", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemSword;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return InventoryUtil.getDamageScore(a) > InventoryUtil.getDamageScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return InventoryUtil.getDamageScore(a) == InventoryUtil.getDamageScore(b);
    }

//    @Override
//    public boolean sort() {
//        AutoWeapon autoWeapon = (AutoWeapon) Southside.moduleManager.getModuleByClass(AutoWeapon.class);
//        if (!autoWeapon.overrideSwordSorting()) {
//            return super.sort();
//        }
//
//        int bestSlot = this.getBestSlot();
//        if (bestSlot == -1) return false;
//        Set<Float> damages = new HashSet<>();
//        for (int i = 0; i < 45; i++) {
//            if (i == bestSlot) continue;
//            Slot slot = mc.player.inventoryContainer.getSlot(i);
//            if (this.match(slot.getStack())) {
//                float score = InventoryUtil.getDamageScore(slot.getStack());
//                if (damages.contains(score)) {
//                    manager.drop(i);
//                    return true;
//                }
//                damages.add(score);
//            }
//        }
//        return false;
//    }

    @Override
    public boolean throwInferior() {
        AutoWeapon autoWeapon = (AutoWeapon) Southside.moduleManager.getModuleByClass(AutoWeapon.class);
        return !autoWeapon.overrideSwordSorting();
    }
}
