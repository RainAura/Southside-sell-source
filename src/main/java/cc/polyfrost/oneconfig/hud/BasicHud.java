/*
 * This file is part of OneConfig.
 * OneConfig - Next Generation Config Library for Minecraft: Java Edition
 * Copyright (C) 2021~2023 Polyfrost.
 *   <https://polyfrost.cc> <https://github.com/Polyfrost/>
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *   OneConfig is licensed under the terms of version 3 of the GNU Lesser
 * General Public License as published by the Free Software Foundation, AND
 * under the Additional Terms Applicable to OneConfig, as published by Polyfrost,
 * either version 1.0 of the Additional Terms, or (at your option) any later
 * version.
 *
 *   This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 * License.  If not, see <https://www.gnu.org/licenses/>. You should
 * have also received a copy of the Additional Terms Applicable
 * to OneConfig, as published by Polyfrost. If not, see
 * <https://polyfrost.cc/legal/oneconfig/additional-terms>
 */

package cc.polyfrost.oneconfig.hud;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.options.impl.Color;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;


public abstract class BasicHud extends Hud {
    public Switch background = new Switch("Background", "If the background of the HUD is enabled.", false);
    public Switch rounded = new Switch("Rounded corners", "If the background has rounded corners.", false);
    public Switch border = new Switch("Outline/border", "If the hud has an outline.", false);
    public Color bgColor = new Color("Background color:", "The color of the background.", new OneColor("#00000000"));
    public Color borderColor = new Color("Border color:", "The color of the border.", new OneColor("#00000000"));
    public Slider cornerRadius = new Slider("Corner radius:", 0, 10, 0, "The corner radius of the background.", false, 0);
    public Slider borderSize = new Slider("Border thickness:", 0, 10, 0, "The thickness of the outline.", false, 0);
    public Slider paddingX = new Slider("X-Padding", 0, 10, 0, "The horizontal padding of the HUD.", false, 0);
    public Slider paddingY = new Slider("Y-Padding", 0, 10, 0, "The vertical padding of the HUD.", false, 0);

    protected float defaultPaddingX;
    protected float defaultPaddingY;
    private boolean loaded = false;

    /**
     * @param x            X-coordinate of hud on a 1080p display
     * @param y            Y-coordinate of hud on a 1080p display
     * @param scale        Scale of the hud
     * @param background   If the HUD should have a background
     * @param rounded      If the corner is rounded or not
     * @param cornerRadius Radius of the corner
     * @param paddingX     Horizontal background padding
     * @param paddingY     Vertical background padding
     * @param bgColor      Background color
     * @param border       If the hud has a border or not
     * @param borderSize   Thickness of the border
     * @param borderColor  The color of the border
     */
    public BasicHud(float x, float y, float scale, boolean background, boolean rounded, float cornerRadius, float paddingX, float paddingY, OneColor bgColor, boolean border, float borderSize, OneColor borderColor) {
        super(x, y, scale);
        this.background.setValue(background);
        this.rounded.setValue(rounded);
        this.cornerRadius.setValue(cornerRadius);
        this.paddingX.setValue(paddingX);
        this.paddingY.setValue(paddingY);
        this.bgColor.setValue(bgColor);
        this.border.setValue(border);
        this.borderSize.setValue(borderSize);
        this.borderColor.setValue(borderColor);
        position.getValue().setSize(getWidth(scale, true) + paddingX * scale * 2f, getHeight(scale, true) + paddingY * scale * 2f);
        if (!loaded) {
            this.defaultPaddingX = paddingX;
            this.defaultPaddingY = paddingY;
            loaded = true;
        }
    }

    /**
     * @param x       X-coordinate of hud on a 1080p display
     * @param y       Y-coordinate of hud on a 1080p display
     * @param scale   Scale of the hud
     */
    public BasicHud(float x, float y, float scale) {
        this(x, y, scale, true, false, 2, 5, 5, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
    }

    /**
     * @param x       X-coordinate of hud on a 1080p display
     * @param y       Y-coordinate of hud on a 1080p display
     */
    public BasicHud(float x, float y) {
        this(x, y, 1, true, false, 2, 5, 5, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
    }

    public BasicHud() {
        this(0, 0, 1, true, false, 2, 5, 5, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
    }

    @Override
    public void drawAll(UMatrixStack matrices, boolean example) {
        if (!example && !shouldShow()) return;
        preRender(example);
        position.getValue().setSize(getWidth(scale.getValue().floatValue(), example) + paddingX.getValue().floatValue() * scale.getValue().floatValue() * 2f, getHeight(scale.getValue().floatValue(), example) + paddingY.getValue().floatValue() * scale.getValue().floatValue() * 2f);
        if (shouldDrawBackground() && background.getValue())
            drawBackground(position.getValue().getX(), position.getValue().getY(), position.getValue().getWidth(), position.getValue().getHeight(), scale.getValue().floatValue());
        draw(matrices, position.getValue().getX() + paddingX.getValue().floatValue() * scale.getValue().floatValue(), position.getValue().getY() + paddingY.getValue().floatValue() * scale.getValue().floatValue(), scale.getValue().floatValue(), example);
    }

    @Override
    protected void resetPosition() {
        paddingX.setValue(defaultPaddingX);
        paddingY.setValue(defaultPaddingY);
        super.resetPosition();
    }

    /**
     * Set a new scale value
     *
     * @param scale   The new scale
     * @param example If the HUD is being rendered in example form
     */
    @Override
    public void setScale(float scale, boolean example) {
        this.scale.setValue(scale);
        position.getValue().updateSizePosition(getWidth(scale, example) + paddingX.getValue().floatValue() * scale * 2f, getHeight(scale, example) + paddingY.getValue().floatValue() * scale * 2f);
    }

    /**
     * @return If the background should be drawn
     */
    protected boolean shouldDrawBackground() {
        return true;
    }

    protected void drawBackground(float x, float y, float width, float height, float scale) {
        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        nanoVGHelper.setupAndDraw(true, (vg) -> {
            if (rounded.getValue()) {
                nanoVGHelper.drawRoundedRect(vg, x, y, width, height, bgColor.getValue().getRGB(), cornerRadius.getValue().floatValue() * scale);
                if (border.getValue())
                    nanoVGHelper.drawHollowRoundRect(vg, x - borderSize.getValue().floatValue() * scale, y - borderSize.getValue().floatValue() * scale, width + borderSize.getValue().floatValue() * scale, height + borderSize.getValue().floatValue() * scale, borderColor.getValue().getRGB(), cornerRadius.getValue().floatValue() * scale, borderSize.getValue().floatValue() * scale);
            } else {
                nanoVGHelper.drawRect(vg, x, y, width, height, bgColor.getValue().getRGB());
                if (border.getValue())
                    nanoVGHelper.drawHollowRoundRect(vg, x - borderSize.getValue().floatValue() * scale, y - borderSize.getValue().floatValue() * scale, width + borderSize.getValue().floatValue() * scale, height + borderSize.getValue().floatValue() * scale, borderColor.getValue().getRGB(), 0, borderSize.getValue().floatValue() * scale);
            }
        });
    }
}
