package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;

public class AutoHeal extends Module {
    public final Slider delayValue = new Slider("Delay", 1000, 100, 2000, 1);
    public final Slider healValue = new Slider("Health", 12, 0, 20, 1);
    public final Switch headValue = new Switch("Head", true);
    public final Switch soupValue = new Switch("Soup", true);
    public final Switch headCheck = new Switch("Soup Regen Check", true);
    public final Switch throwBowlValue = new Switch("Throw Bowls", true);
    public AutoHeal(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(throwBowlValue.getLabel(), soupValue.getLabel());
//        throwBowlValue.setDisplay(soupValue::getValue);
    }

    private boolean hasRegeneration() {
        for (PotionEffect effect : mc.player.getActivePotionEffects()) {
            if (effect.getPotion().equals(MobEffects.REGENERATION)) {
                return true;
            }
        }
        return false;
    }

    private final TimerUtil timerUtil = new TimerUtil();

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() != EventState.PRE) return;

        if (throwBowlValue.getValue()) {
            for (int i = 0; i <= 44; i++) {
                if (mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.BOWL) {
                    InventoryUtil.drop(i);
                }
            }
        }
        boolean soup = soupValue.getValue();
        boolean head = headValue.getValue() && (!this.hasRegeneration() || !headCheck.getValue());
        if ((soup || head) && mc.player.getHealth() < healValue.getValue().floatValue()) {
            if (timerUtil.hasReached(delayValue.getValue().longValue())) {
                int findSlot = -1;
                for (int i = 36; i <= 44; i++) {
                    Item item = mc.player.inventoryContainer.getSlot(i).getStack().getItem();
                    if ((item instanceof ItemSoup && soup) || (item instanceof ItemSkull && head)) {
                        findSlot = i;
                        break;
                    }
                }
                if (findSlot == -1) {
                    for (int i = 0; i <= 35; i++) {
                        Item item = mc.player.inventoryContainer.getSlot(i).getStack().getItem();
                        if ((item instanceof ItemSoup && soup) || (item instanceof ItemSkull && head)) {
                            findSlot = i;
                            break;
                        }
                    }
                    if (findSlot == -1) {
                        return;
                    }
                    InventoryUtil.swap(findSlot, 8);
                    InventoryUtil.useSlot(8);
                    InventoryUtil.swap(findSlot, 8);

                } else {
                    InventoryUtil.useSlot(findSlot - 36);
                }
                timerUtil.reset();
            }
        }
    }
}
