package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;

public class SharpAxeComponent extends SubComponent {
    public SharpAxeComponent(InvManager manager) {
        super("SharpAxe", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return InventoryUtil.isSharpAxe(itemStack);
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return InventoryUtil.getSharpAxeScore(a) > InventoryUtil.getSharpAxeScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return InventoryUtil.getSharpAxeScore(a) == InventoryUtil.getSharpAxeScore(b);
    }
}
