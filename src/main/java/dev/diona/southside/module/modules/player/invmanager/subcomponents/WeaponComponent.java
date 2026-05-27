package dev.diona.southside.module.modules.player.invmanager.subcomponents;

import dev.diona.southside.Southside;
import dev.diona.southside.module.modules.combat.PreferWeapon;
import dev.diona.southside.module.modules.player.AutoWeapon;
import dev.diona.southside.module.modules.player.InvManager;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.util.player.InventoryUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.HashSet;
import java.util.Set;

import static dev.diona.southside.Southside.MC.mc;

public class WeaponComponent extends SubComponent {
    private final SwordComponent swordComponent;
    private final SharpAxeComponent sharpAxeComponent;
    private final KnockBackComponent knockBackComponent;
    public WeaponComponent(InvManager manager) {
        super("Weapon", manager);
        this.swordComponent = new SwordComponent(manager);
        this.sharpAxeComponent = new SharpAxeComponent(manager);
        this.knockBackComponent = new KnockBackComponent(manager);
    }

    @Override
    public boolean match(ItemStack itemStack) {
        if (this.getComponent() != this.swordComponent) {
            if (itemStack.getItem() instanceof ItemSword) {
                return itemStack == this.swordComponent.getBest();
            }
        }
        return this.getComponent().match(itemStack);
//        return itemStack.getItem() instanceof ItemSword;
    }

    @Override
    public ItemStack choose(ItemStack a, ItemStack b) {
        return this.getComponent().choose(a, b);
//        if (a == null) return b;
//        if (b == null) return a;
//        return InventoryUtil.getDamageScore(a) > InventoryUtil.getDamageScore(b) ? a : b;
    }

    @Override
    public boolean equal(ItemStack a, ItemStack b) {
        return this.getComponent().equal(a, b);
//        if (a == null || b == null) return a == b;
//        return InventoryUtil.getDamageScore(a) == InventoryUtil.getDamageScore(b);
    }

    @Override
    public boolean throwInferior() {
        return this.getComponent().throwInferior();
    }

    private SubComponent getComponent() {
        PreferWeapon preferWeapon = (PreferWeapon) Southside.moduleManager.getModuleByClass(PreferWeapon.class);
        PreferWeapon.WeaponType type = preferWeapon.getPreferring();
        if (type == PreferWeapon.WeaponType.SWORD) {
            return swordComponent;
        }
        if (type == PreferWeapon.WeaponType.SHARP_AXE) {
            return sharpAxeComponent;
        }
        if (type == PreferWeapon.WeaponType.KNOCKBACK_SLIMEBALL) {
            return knockBackComponent;
        }
        return swordComponent;
    }

    @Override
    public boolean sort() {
        AutoWeapon autoWeapon = (AutoWeapon) Southside.moduleManager.getModuleByClass(AutoWeapon.class);
        ItemStack currentItem = mc.player.inventoryContainer.getSlot(manager.getItemSlot().get(this.name)).getStack();
        if (!(currentItem.getItem() instanceof ItemSword) || !autoWeapon.overrideSwordSorting()) {
            return super.sort();
        }

        int bestSlot = this.getBestSlot();
        if (bestSlot == -1) return false;
        Set<Integer> damages = new HashSet<>();

        for (int i = 0; i < 45; i++) {
            if (i == bestSlot) continue;
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (this.match(slot.getStack())) {
                int score = (int) (InventoryUtil.getDamageScore(slot.getStack()) * 10);
                if (damages.contains(score) && EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, slot.getStack()) == 0) {
                    manager.drop(i);
                    return true;
                }
                damages.add(score);
            }
        }
        return false;
    }
}
