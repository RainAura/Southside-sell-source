package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import net.minecraft.item.*;

public class ProjectileComponent extends SubComponent {
    public ProjectileComponent(InvManager manager) {
        super("Projectile", manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemEgg || itemStack.getItem() instanceof ItemSnowball;
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
