package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

public class TNTComponent extends SubComponent {
    public TNTComponent(InvManager manager) {
        super("TNT", manager);
    }

    public final static Item TNTItem = Item.getItemFromBlock(Blocks.TNT);

    @Override
    public boolean match(ItemStack itemStack) {
//        return itemStack.getItem() instanceof (Items).TNT_MINECART;
        return itemStack.getItem().equals(TNTItem);
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
