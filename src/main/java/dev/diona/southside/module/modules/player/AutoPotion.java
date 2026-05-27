package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.MovementUtil;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;

import java.util.Random;

public class AutoPotion extends Module {
    public AutoPotion(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public final Slider delayValue = new Slider("Delay", 250, 0, 1000, 50);
    public final Slider healthValue = new Slider("Health", 1, 1, 20, 1);
    public final Slider selectValue = new Slider("Select Slot", 6, 0, 8, 1);

    TimerUtil delayTimer = new TimerUtil();

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (!delayTimer.hasReached(delayValue.getValue().intValue()) || mc.player.isHandActive() || RotationUtil.targetRotation != null || mc.player.isOnLadder())
            return;
        int potionSlot = getPotInInventory();
        if (potionSlot != -1) {
            int oldSlot = mc.player.inventory.currentItem;
            Rotation rotation = RotationUtil.getPlayerRotation();
            if (MovementUtil.isMoving() && mc.player.isSprinting()) {
                rotation.pitch = new Random().nextFloat(40F, 42F);
            } else {
                rotation.pitch = new Random().nextFloat(85F, 90F);
            }
            RotationUtil.setTargetRotation(rotation.onPost(() -> {
                if (potionSlot < 36) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, potionSlot, selectValue.getValue().intValue(), ClickType.SWAP, mc.player);
                    mc.player.inventory.currentItem = selectValue.getValue().intValue();
                } else {
                    mc.player.inventory.currentItem = potionSlot - 36;
                }
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                mc.player.inventory.currentItem = oldSlot;
                delayTimer.reset();
            }), 0);
        }
    }

    int getPotInInventory() {
        outerLoop:
        for (int i = 9; i < mc.player.inventoryContainer.getInventory().size(); i++) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemSplashPotion) {
                for (PotionEffect effect : PotionUtils.getEffectsFromStack(itemStack)) {

                    //玩家已经存在相同的效果，不要继续使用这个药水了
                    for (PotionEffect activeEffect : mc.player.getActivePotionEffects()) {
                        if (activeEffect.getEffectName() == effect.getEffectName())
                            continue outerLoop;
                    }

                    if ("effect.moveSpeed".equals(effect.getEffectName())
                            || "effect.jump".equals(effect.getEffectName())
                            || "effect.regeneration".equals(effect.getEffectName()) && healthValue.getValue().intValue() >= mc.player.getHealth() + mc.player.getAbsorptionAmount()
                            || "effect.nightVision".equals(effect.getEffectName())
                            || "effect.invisibility".equals(effect.getEffectName())
                            || "effect.resistance".equals(effect.getEffectName())
                            || "effect.fireResistance".equals(effect.getEffectName())
                            || "effect.heal".equals(effect.getEffectName()) && healthValue.getValue().intValue() >= mc.player.getHealth() + mc.player.getAbsorptionAmount()
                    ) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
