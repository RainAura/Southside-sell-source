package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.HUD;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.gui.hud.PreferWeaponHud;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.player.InventoryUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSword;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class PreferWeapon extends Module {
    public final HashMap<WeaponType, PreferWeapon.WeaponOption> weapons = new HashMap<>();
    public WeaponType selected = null;
    private boolean lastTickPressed = false;

    public PreferWeapon(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
//        super(name, description, category, visible, "Prefer Weapon", 50, 180);
        weapons.put(WeaponType.SWORD, new WeaponOption(WeaponType.SWORD));
        weapons.put(WeaponType.SHARP_AXE, new WeaponOption(WeaponType.SHARP_AXE));
        weapons.put(WeaponType.KNOCKBACK_SLIMEBALL, new WeaponOption(WeaponType.KNOCKBACK_SLIMEBALL));
    }

    public HUD hud = new HUD("Prefer Weapon HUD", new PreferWeaponHud(
            40, 80, 1, 1, this
    ));


//    @Override
//    public float width() {
//        return 140;
//    }

//    @EventListener
//    public void onBloom2D(Bloom2DEvent event) {
//        float startY = 0;
//        RenderUtil.scissorStart(this.xValue.getValue() - 30, this.yValue.getValue() - 20, 200, 160);
//        for (WeaponType type : Arrays.asList(WeaponType.SWORD, WeaponType.SHARP_AXE, WeaponType.KNOCKBACK_SLIMEBALL)) {
//            WeaponOption weaponOption = weapons.get(type);
//            if (!weaponOption.lastTickFound) {
//                weaponOption.y.update(-30);
//            } else {
//                if (selected == null) {
//                    selected = weaponOption.weaponType;
//                }
//                if (selected == weaponOption.weaponType) {
//                    indicatorY.update(startY);
//                }
//                weaponOption.y.update(startY);
//                weaponOption.draw(this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue());
//                startY += 25;
//            }
//        }
//        if (selected == null) {
//            indicatorY.update(-30);
//        }
//        RoundUtil.drawRound(this.xValue.getValue().floatValue() - 5F, this.yValue.getValue().floatValue() + indicatorY.get() + 2.5F, 1, 15, 0.5F, Color.WHITE);
//        RenderUtil.scissorEnd();
//        super.onUIElementBloom(event);
//    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (selected != null && !weapons.get(selected).lastTickFound) {
            selected = null;
        }
        weapons.values().forEach(weaponOption -> weaponOption.lastTickFound = false);
        for (Slot slot : mc.player.inventoryContainer.inventorySlots) {
            if (slot.getStack().getItem() instanceof ItemSword) {
                weapons.get(WeaponType.SWORD).lastTickFound = true;
            }
            if (InventoryUtil.isSharpAxe(slot.getStack())) {
                weapons.get(WeaponType.SHARP_AXE).lastTickFound = true;
            }
            if (InventoryUtil.isKnockBackSlimeball(slot.getStack())) {
                weapons.get(WeaponType.KNOCKBACK_SLIMEBALL).lastTickFound = true;
            }
        }
        boolean state = Mouse.isButtonDown(2);
        if (!lastTickPressed && state && selected != null) {
            if (selected == WeaponType.SWORD) {
                for (WeaponType choice : Arrays.asList(WeaponType.SHARP_AXE, WeaponType.KNOCKBACK_SLIMEBALL)) {
                    if (weapons.get(choice).lastTickFound) {
                        selected = choice;
                        break;
                    }
                }
            } else if (selected == WeaponType.SHARP_AXE) {
                for (WeaponType choice : Arrays.asList(WeaponType.KNOCKBACK_SLIMEBALL, WeaponType.SWORD)) {
                    if (weapons.get(choice).lastTickFound) {
                        selected = choice;
                        break;
                    }
                }
            } else if (selected == WeaponType.KNOCKBACK_SLIMEBALL) {
                for (WeaponType choice : Arrays.asList(WeaponType.SWORD, WeaponType.SHARP_AXE)) {
                    if (weapons.get(choice).lastTickFound) {
                        selected = choice;
                        break;
                    }
                }
            }
        }
        lastTickPressed = state;
    }

    public WeaponType getPreferring() {
        if (!this.isEnabled()) {
            return WeaponType.SWORD;
        }
        if (selected != null && !weapons.get(selected).lastTickFound) {
            selected = null;
        }
        return selected == null ? WeaponType.SWORD : selected;
    }

    public enum WeaponType {
        SWORD("Sword"),
        SHARP_AXE("Sharpness Axe"),
        KNOCKBACK_SLIMEBALL("Knockback Slimeball");

        private final String name;

        WeaponType(String name) {
            this.name = name;
        }
    }

    public static class WeaponOption {
        public final WeaponType weaponType;
        public BezierUtil y = new BezierUtil(3, 0);
        public boolean lastTickFound = false;

        public WeaponOption(WeaponType weaponType) {
            this.weaponType = weaponType;
        }

        public void draw(long vg, float x, float yOffset, float scale) {
//            float width = 140F, height = 20F;
            float yValue = y.get() + yOffset;
//            RenderUtil.drawRect(x, yValue, x + width, yValue + height, new Color(0, 0, 0, 129).getRGB());

            NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
            nanoVGHelper.drawText(vg, this.weaponType.name, x + 5 * scale, yValue + 5F * scale, -1, 5 * scale, Fonts.WQY);
        }
    }
}
