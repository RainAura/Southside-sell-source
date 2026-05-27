package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectObjectImmutablePair;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.AttackEvent;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.module.modules.combat.PreferWeapon;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.InventoryUtil;
import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.List;

import static dev.diona.southside.Southside.MC.mc;

public class AutoWeapon extends Module {
    public final Dropdown modeValue = new Dropdown("Mode", "ArmorBreak", "Switch", "Spoof", "ArmorBreak");
    private int previousSlot = -1;
    public static TimerUtil timer = new TimerUtil();

    public AutoWeapon(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onAttack(AttackEvent event) {
        if (event.getState() == EventState.PRE) {
            previousSlot = -1;
            if (InventoryUtil.isSharpAxe(mc.player.getHeldItemMainhand())) return;
            if (modeValue.getMode().equals("ArmorBreak")) {
                return;
            }
            int bestSlot = mc.player.inventory.currentItem;
            float f = InventoryUtil.getDamageScore(mc.player.getHeldItemMainhand());
            for (int i = 36; i < 45; i++) {
                ItemStack curSlot = mc.player.inventoryContainer.getSlot(i).getStack();
                if (InventoryUtil.isSharpAxe(curSlot)) continue;
                float score = InventoryUtil.getDamageScore(curSlot);
                if (f < score) {
                    f = score;
                    bestSlot = i - 36;
                }
            }

            if (mc.player.inventory.currentItem != bestSlot) {
                if (modeValue.getMode().equals("Spoof")) {
                    previousSlot = mc.player.inventory.currentItem;
                    ChatUtil.info("to weapon " + bestSlot);
                    mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(bestSlot));
                    mc.getConnection().sendPacketNoEvent(new CPacketCustomPayload("test", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
                } else {
                    mc.player.inventory.currentItem = bestSlot;
                    mc.playerController.updateController();
                }
            }
        } else if (modeValue.getMode().equals("Spoof")) {
            if (previousSlot == -1) return;
            ChatUtil.info("to previous " + previousSlot);
            mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(previousSlot));
            mc.getConnection().sendPacketNoEvent(new CPacketCustomPayload("test", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
        }
    }

    private boolean doTiger(ItemStack stack) {
        return stack.getItem() instanceof ItemSword || (InventoryUtil.isSharpAxe(stack) && !InventoryUtil.isGodAxe(stack));
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onUpdate(MotionEvent event) {
//        if (event.getState() != EventState.PRE) return;
        if (this.overrideSwordSorting() && doTiger(mc.player.getHeldItemMainhand())) {
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, mc.player.getHeldItemMainhand()) == 0 && KillAura.getTargets().isEmpty()) {
                for (int i = 0; i < 45; i++) {
                    ItemStack curSlot = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (curSlot == mc.player.getHeldItemMainhand()) continue;
                    if (!(curSlot.getItem() instanceof ItemSword itemSword)) continue;
                    if (itemSword.material == Item.ToolMaterial.WOOD && EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, curSlot) > 0) {
                        InventoryUtil.swap(i, mc.player.inventory.currentItem);
                        timer.reset();
                        return;
                    }
                }
            }
            if (KillAura.getTargets().isEmpty() || Blink.isInstanceEnabled()) {
                return;
            }
            int choice = -1;
            float score = 10000;
            int hand = mc.player.inventory.currentItem;
            int worstChoice = -1;
            float worstScore = 0;
            for (int i = 1; i < 45; i++) {
                ItemStack curSlot = mc.player.inventoryContainer.getSlot(i).getStack();
                if (curSlot == mc.player.getHeldItemMainhand()) continue;
                if (doTiger(curSlot)) {
                    float delta = InventoryUtil.getDamageScore(curSlot) - InventoryUtil.getDamageScore(mc.player.getHeldItem(EnumHand.MAIN_HAND));
                    if (delta > 0 && delta < score) {
                        choice = i;
                        score = delta;
                    }
                    if (delta < 0 && delta < worstScore) {
                        worstChoice = i;
                        worstScore = delta;
                    }
                }
            }
            if (choice != -1) {
                InventoryUtil.swap(choice, hand);
            } else if (worstChoice != -1) {
                InventoryUtil.swap(worstChoice, hand);
            }
            timer.reset();
        }
    }

    public boolean overrideSwordSorting() {
        if (this.isEnabled() && modeValue.getMode().equals("ArmorBreak")) {
            PreferWeapon preferWeapon = (PreferWeapon) Southside.moduleManager.getModuleByClass(PreferWeapon.class);
            if (preferWeapon.isEnabled() && preferWeapon.getPreferring() != PreferWeapon.WeaponType.SWORD) return false;
            return true;
        }
        return false;
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }
}
