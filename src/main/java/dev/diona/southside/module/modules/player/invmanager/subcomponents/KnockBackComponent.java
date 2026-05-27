package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class KnockBackComponent extends SubComponent {
    public KnockBackComponent(InvManager manager) {
        super("KnockBack", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return InventoryUtil.isKnockBackSlimeball(itemStack);
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a != null && a.getItem() == Items.SLIME_BALL) return a; // Hack: 防止由于为了不丢剑而传入比较得剑被选中
        if (b != null && b.getItem() == Items.SLIME_BALL) return b;
        return a;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return a.getItem() == Items.SLIME_BALL && b.getItem() == Items.SLIME_BALL;
    }
}
