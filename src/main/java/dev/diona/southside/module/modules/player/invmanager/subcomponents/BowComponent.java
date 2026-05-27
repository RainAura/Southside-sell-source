package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class BowComponent extends SubComponent {
    public BowComponent(InvManager manager) {
        super("Bow", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBow;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return InventoryUtil.getBowScore(a) > InventoryUtil.getBowScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return InventoryUtil.getBowScore(a) == InventoryUtil.getBowScore(b);
    }

    @Override
    public boolean throwInferior() {
        return true;
    }
}
