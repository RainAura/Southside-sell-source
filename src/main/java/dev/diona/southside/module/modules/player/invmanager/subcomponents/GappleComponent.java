package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GappleComponent extends SubComponent {
    public GappleComponent(InvManager manager) {
        super("Gapple", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemAppleGold;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.getItemDamage() == b.getItemDamage()) {
            return a.getCount() > b.getCount() ? a : b;
        } else {
            return a.getItemDamage() > b.getItemDamage() ? a : b;
        }
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return a.getCount() == b.getCount();
    }
}
