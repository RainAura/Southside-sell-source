package dev.diona.southside.util.player;


import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;

import java.util.concurrent.atomic.AtomicInteger;

import static dev.diona.southside.Southside.MC.mc;

public class InventoryUtil {

    public static int getItemSlot(Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.player.inventory.mainInventory.get(i);
            if (is.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static int getBlockSlot(Block block) {
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.player.inventory.mainInventory.get(i);
            if (is.getItem() instanceof ItemBlock && ((ItemBlock) is.getItem()).getBlock() == block) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isHoldingSword() {
        return mc.player.getHeldItemMainhand().getItem() instanceof ItemSword;
    }

    public static void click(int slot, int mouseButton, boolean shiftClick) {
        try {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, mouseButton, shiftClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, mc.player);
        } catch (IndexOutOfBoundsException ignored) {

        }
    }

    public static void drop(int slot) {
        try {
            mc.playerController.windowClick(0, slot, 1, ClickType.THROW, mc.player);

//            mc.playerController.windowClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
//            mc.playerController.windowClick(mc.player.openContainer.windowId, -999, 0, ClickType.PICKUP, mc.player);
        } catch (IndexOutOfBoundsException ignored) {

        }
    }

    public static void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, hSlot, ClickType.SWAP, mc.player);
        mc.getConnection().sendPacketNoEvent(new CPacketCustomPayload("test", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
    }

    public static float getSwordStrength(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword) stack.getItem();
            float sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) * 1.25F;
            float fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) * 1.5F;
            return sword.getAttackDamage() + sharpness + fireAspect + 1F; // 1.8.9 swords deals 1 more damage
        }
        return 0;
    }

    public static float getToolScore(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            String material = tool.getToolMaterialName();
            if (item instanceof ItemPickaxe) {
                score = tool.getDestroySpeed(stack, Blocks.STONE) - (material.equalsIgnoreCase("gold") ? 5 : 0);
            } else if (item instanceof ItemSpade) {
                score = tool.getDestroySpeed(stack, Blocks.DIRT) - (material.equalsIgnoreCase("gold") ? 5 : 0);
            } else {
                if (!(item instanceof ItemAxe)) return 1;
                score = tool.getDestroySpeed(stack, Blocks.LOG) - (material.equalsIgnoreCase("gold") ? 5 : 0);
            }
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) * 0.0075F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) / 100F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) * 1F;
        }
        return score;
    }



    public static float getDamageScore(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0;

        float damage = 1;
        Item item = stack.getItem();

        if (item instanceof ItemSword sword) {
            switch (sword.material) {
                case WOOD, GOLD -> damage = 5;
                case STONE -> damage = 6;
                case IRON -> damage = 7;
                case DIAMOND -> damage = 8;
            }
        }
        if (item instanceof ItemTool tool) {
            switch (tool.toolMaterial) {
                case WOOD, GOLD -> damage = 0;
                case STONE -> damage = 1;
                case IRON -> damage = 2;
                case DIAMOND -> damage = 3;
            }
            if (tool instanceof ItemAxe) {
                damage += 4;
            }
            if (tool instanceof ItemPickaxe) {
                damage += 3;
            }
            if (tool instanceof ItemSpade) {
                damage += 2;
            }
        }

        damage += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) * 1.5F +
                EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) * 0.1F;

        return damage;
    }

    public static float getBowScore(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemBow) {
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) * 0.5F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack) * 3F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) * 0.1F;
        }
        return score;
    }

    public static float getPotionScore(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemPotion potion) {
            for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
                int baseScore = getBaseScore(effect);
                if (baseScore != 0 && mc.player.isPotionActive(effect.getPotion())) {
                    score += 1;
                    continue;
                }
                score += baseScore * effect.getDuration() / 20f * (effect.getAmplifier() + 1);
            }
        }
        return score;
    }

    private static int getBaseScore(PotionEffect effect) {
        int baseScore;
        switch (effect.getPotion().getName()) {
            case "effect.heal" -> baseScore = 1000;
            case "effect.damageBoost" -> baseScore = 5;
            case "effect.jump", "effect.moveSpeed", "effect.regeneration", "effect.resistance" -> baseScore = 3;
            case "effect.fireResistance" -> baseScore = 2;
            case "effect.blindness", "effect.weakness", "effect.hunger" -> baseScore = -2;
            case "effect.harm" -> baseScore = -1000;
            default -> baseScore = 0;
        }
        return baseScore;
    }

    public static float getArmorScore(ItemStack stack) {
        float score = 0;
        if (stack.getItem() instanceof ItemArmor armor) {
            score += armor.damageReduceAmount + ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack)) * 0.0025F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack) / 100F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) / 100F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack) / 100F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) / 25.F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack) / 100F;
        }
        return score;
    }

    public static boolean isSharpAxe(ItemStack stack) {
        if (stack.getItem() instanceof ItemAxe) {
            return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 10;
        }
        return false;
    }

    public static boolean isGodAxe(ItemStack stack) {
        if (stack.getItem() instanceof ItemAxe) {
            return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 666;
        }
        return false;
    }

    public static boolean isKnockBackSlimeball(ItemStack stack) {
        return stack.getItem() == Items.SLIME_BALL && EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack) == 3;
    }

    public static float getSharpAxeScore(ItemStack stack) {
        float score = 0;
        if (stack.getItem() instanceof ItemAxe axe) {
            score += axe.getToolMaterial().getAttackDamage();
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        }
        return score;
    }

    public static boolean isFullBlock(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return itemBlock.getBlock().getDefaultState().isFullBlock();
        }
        return false;
    }

    public static void useSlot(int slot) {
        int currentItem = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slot;
        if (currentItem != slot) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
        }
        mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (currentItem != slot) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(currentItem));
        }
        mc.player.inventory.currentItem = currentItem;
    }

    public static void useSlotNoEvent(int slot) {
        int currentItem = mc.player.inventory.currentItem;
        if (currentItem != slot) {

            mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(slot));
        }
        mc.getConnection().sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (currentItem != slot) {

            mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(currentItem));
        }
    }

    public static int count(ContainerChest chest) {
        IInventory inv = chest.getLowerChestInventory();
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public static boolean switchBlock() {
        if (InventoryUtil.isFullBlock(mc.player.getHeldItemMainhand())) return true;
        int bestSlot = -1, count = 0;
        for (int i = 36; i <= 44; i++) {
            ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
            if (InventoryUtil.isFullBlock(is)) {
                if (bestSlot == -1 || is.getCount() > count) {
                    bestSlot = i - 36;
                    count = is.getCount();
                }
            }
        }
        if (bestSlot == -1) return false;
        if (bestSlot != mc.player.inventory.currentItem) {
            mc.player.inventory.currentItem = bestSlot;
        }
        return true;
    }
}
