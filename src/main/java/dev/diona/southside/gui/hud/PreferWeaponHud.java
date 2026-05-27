package dev.diona.southside.gui.hud;

import cc.polyfrost.oneconfig.hud.Hud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.renderer.scissor.Scissor;
import cc.polyfrost.oneconfig.renderer.scissor.ScissorHelper;
import dev.diona.southside.module.modules.combat.PreferWeapon;
import dev.diona.southside.util.misc.BezierUtil;

import java.awt.*;
import java.util.Arrays;

public class PreferWeaponHud extends Hud {
    private BezierUtil height = new BezierUtil(4, 0);
    private final PreferWeapon parent;

    public PreferWeaponHud(float x, float y, int positionAlignment, float scale, PreferWeapon parent) {
        super(x, y, positionAlignment, scale);
        this.parent = parent;
    }
    private final BezierUtil indicatorY = new BezierUtil(3, -30);

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        ScissorHelper scissorHelper = ScissorHelper.INSTANCE;
        nanoVGHelper.setupAndDraw(true, vg -> {
            final float topbar = 73 * 0.1F * scale;
            float startY = topbar;

            nanoVGHelper.drawRect(vg, x, y, 60 * scale, height.get(), new Color(0, 0, 0, 120).getRGB());
            nanoVGHelper.drawDropShadow(vg, x, y, 60 * scale, height.get(), 10, 1, 0);
            nanoVGHelper.drawRect(vg, x, y + 1 * scale, 1 * scale, 5 * scale, new Color(255, 255, 255).getRGB());
            nanoVGHelper.drawText(vg, "Prefer Weapons", x + 2 * scale, y + 3.9F * scale, -1, (4 * scale), Fonts.SEMIBOLD);

            Scissor scissor = scissorHelper.scissor(vg, x, y, getWidth(scale, example), height.get());
            for (PreferWeapon.WeaponType type : Arrays.asList(PreferWeapon.WeaponType.SWORD, PreferWeapon.WeaponType.SHARP_AXE, PreferWeapon.WeaponType.KNOCKBACK_SLIMEBALL)) {
                PreferWeapon.WeaponOption weaponOption = parent.weapons.get(type);
                if (!weaponOption.lastTickFound) {
                    weaponOption.y.update(-30 * scale);
                } else {
                    if (parent.selected == null) {
                        parent.selected = weaponOption.weaponType;
                    }
                    if (parent.selected == weaponOption.weaponType) {
                        indicatorY.update(startY);
                    }
                    weaponOption.y.update(startY);
                    weaponOption.draw(vg, x, y, scale);
                    startY += 10 * scale;
                }
            }
            if (parent.selected == null) {
                indicatorY.update(-30 * scale);
            }
            height.update(startY);
            nanoVGHelper.drawRoundedRect(vg, x + 2F * scale, y + indicatorY.get() + 2.1F * scale, 1 * scale, 5 * scale, -1, 0.5F * scale);
//            nanoVGHelper.drawText(vg, "|", x + 1F * scale, y + indicatorY.get() + 5F * scale, -1, 4F * scale, Fonts.MINECRAFT_BOLD);
            scissorHelper.resetScissor(vg, scissor);
        });
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return 60 * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        if (height == null) return 0;
        return height.get();
    }
}
