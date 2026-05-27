package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.DefaultEnabled;
import dev.diona.southside.module.modules.combat.Critical;
import dev.diona.southside.module.modules.combat.KillAura;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@DefaultEnabled
public class OldHitting extends Module {
    public static boolean blocking = false;
    private static OldHitting INSTANCE;
    public final Slider translateXValue = new Slider("Translate X", 0, -1, 1, 0.01);
    public final Slider translateYValue = new Slider("Translate Y", 0, -1, 1, 0.01);
    public final Slider translateZValue = new Slider("Translate Z", 0, -1, 1, 0.01);
    public final Slider swingSpeedValue = new Slider("Swing Speed", 1F, 0F, 3F, 0.1F);
    public final Slider itemScaleValue = new Slider("Item Scale", 1F, 0F, 2F, 0.1F);
    public final Dropdown modeValue = new Dropdown("Mode", "1.7", "1.7", "Swank", "Swing", "Swang", "Swong", "Swaing", "Punch", "Stella", "Styles", "Slide", "Interia", "Ethereal", "Sigma", "Exhibition", "Smooth", "Leaked","ShenMiClient");
    public final Switch shieldValue = new Switch("Give Shield", true);
    public OldHitting(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public static boolean isOldHitting() {
        return INSTANCE.isEnabled();
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
            if (!hasShield()) {
                mc.player.inventory.offHandInventory.set(0, new ItemStack(Items.SHIELD));
            }
        } else {
            if (hasShield()) {
                mc.player.inventory.offHandInventory.set(0, new ItemStack(Items.AIR));
            }
        }
    }

    private boolean hasShield() {
        return mc.player.getHeldItemOffhand().getItem() instanceof ItemShield;
    }

    public static String getMode() {
        return INSTANCE.modeValue.getMode();
    }

    public static boolean giveShield() {
        return INSTANCE.isEnabled() && INSTANCE.shieldValue.getValue();
    }

    public static boolean isBlocking() {
        return KillAura.getBlocking() || Critical.isInstanceWorking();
    }

    public static void setBlocking(boolean blocking) {
        OldHitting.blocking = blocking;
    }

    @Override
    public String getSuffix() {
        return modeValue.getMode();
    }

    public static float translateX() {
        return INSTANCE.translateXValue.getValue().floatValue();
    }

    public static float translateY() {
        return INSTANCE.translateYValue.getValue().floatValue();
    }

    public static float translateZ() {
        return INSTANCE.translateZValue.getValue().floatValue();
    }

    public static float getSwingSpeedValue() {
        return INSTANCE.swingSpeedValue.getValue().floatValue();
    }

    public static float getItemScaleValue() {
        return INSTANCE.itemScaleValue.getValue().floatValue();
    }
}
