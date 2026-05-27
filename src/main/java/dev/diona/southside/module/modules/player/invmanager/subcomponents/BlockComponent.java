package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class BlockComponent extends SubComponent {
    public BlockComponent(InvManager manager) {
        super("Block", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return InventoryUtil.isFullBlock(itemStack) && !itemStack.getItem().equals(TNTComponent.TNTItem);
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
