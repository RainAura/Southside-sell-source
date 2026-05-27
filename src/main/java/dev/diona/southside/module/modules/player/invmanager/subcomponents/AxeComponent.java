package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.item.*;

public class AxeComponent extends SubComponent {
    public AxeComponent(InvManager manager) {
        super("Axe", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemAxe && !InventoryUtil.isSharpAxe(itemStack);
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        if (a == null) return b;
        if (b == null) return a;
        return InventoryUtil.getToolScore(a) > InventoryUtil.getToolScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return InventoryUtil.getToolScore(a) == InventoryUtil.getToolScore(b);
    }

    @Override
    public boolean throwInferior() {
        return true;
    }
}
