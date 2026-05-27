package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class FoodComponent extends SubComponent {
    public FoodComponent(InvManager manager) {
        super("Food", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemFood;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.getCount() > b.getCount() ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return a.getCount() == b.getCount();
    }
}
