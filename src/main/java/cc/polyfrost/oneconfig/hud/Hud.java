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

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.options.WrappedValue;
import cc.polyfrost.oneconfig.config.options.impl.*;
import cc.polyfrost.oneconfig.config.options.impl.Number;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.gui.elements.config.ConfigDropdown;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.platform.Platform;

/**
 * Represents a HUD element in OneConfig.
 * A HUD element can be used to display useful information to the user, like FPS or CPS.
 * <p>
 * If you simply want to display text, extend {@link TextHud} or {@link SingleTextHud},
 * whichever applies to the use case. Then, override the required methods.
 * <p>
 * If you want to display something else, extend this class and override {@link Hud#getWidth(float, boolean)}, {@link Hud#getHeight(float, boolean)}, and {@link Hud#draw(UMatrixStack, float, float, float, boolean)} with the width, height, and the drawing code respectively.
 * </p>
 * <p>
 * It should also be noted that additional options to the HUD can be added simply by declaring them.
 * <pre>{@code
 *     public class TestHud extends SingleTextHud {
 *         @literal @Switch(
 *             name = "Additional Option"
 *         )
 *         public boolean additionalOption = true;
 *     }
 *     }</pre>
 * </p>
 * To register an element, add it to your OneConfig {@link Config}.
 * <pre>{@code
 *  *     public class YourConfig extends Config {
 *  *         @literal @HUD(
 *  *             name = "HUD Element"
 *  *         )
 *  *         public YourHudElement hudElement = new YourHudElement("Title");
 *  *     }
 *  *     }</pre>
 */
public abstract class Hud {
//    public cc.polyfrost.oneconfig.config.options.impl.Switch enabled = new cc.polyfrost.oneconfig.config.options.impl.Switch(
//            "Enabled",
//            "If the HUD is enabled",
//            1,
//            true
//    );
    public Button resetPosition = new Button("Reset Position", "Reset", "Reset HUD to default position", 1, this::resetPosition);
    //    protected boolean enabled;
    public Switch locked = new Switch("Locked", "If the position is locked", 2, false);
    public WrappedValue<Position> position = new WrappedValue<Position>("HUD Position",
            new Position(
                    null,
                    0, 0, 0, 0
            ));
    public Slider scale = new Slider("Scale", 0.2f, 10f, 0, "The scale of the HUD", false, 1);
//    public Dropdown positionAlignment = new Dropdown("Position Alignment", new String[]{"Auto", "Left", "Center", "Right"}, "The alignment of the HUD", 1, 0);

    private transient Config config;
    private int defaultAlignment;
    public float deltaTicks;
    private float defaultScale;
    private boolean loaded = false;
    private Position defaultPosition;

    /**
     * @param x                 X-coordinate of hud on a 1080p display
     * @param y                 Y-coordinate of hud on a 1080p display
     * @param positionAlignment Alignment of the hud
     * @param scale             Scale of the hud
     */
    public Hud(float x, float y, int positionAlignment, float scale) {
//        this.enabled = enabled;
//        this.enabled.setValue(enabled);
        this.scale.setValue(scale);
//        this.positionAlignment.setValue(positionAlignment);
        position.setValue(new Position(this, 0, 0, getWidth(scale, true), getHeight(scale, true)));
        position.getValue().anchor = Position.getAnchor(positionAlignment);
        if (!loaded) {
            defaultPosition = new Position(this, 0, 0, getWidth(scale, true), getHeight(scale, true));
            defaultPosition.setX(x);
            defaultPosition.setY(y);
            defaultPosition.anchor = Position.getAnchor(positionAlignment);
            defaultAlignment = positionAlignment;
            defaultScale = scale;
            loaded = true;
        }
    }

    public Hud(float x, float y, float scale) {
        this(x, y, 0, scale);
    }

    /**
     * @param x       X-coordinate of hud on a 1080p display
     * @param y       Y-coordinate of hud on a 1080p display
     */
    public Hud(float x, float y) {
        this(x, y, 1);
    }

    public Hud() {
        this(0, 0, 1);
    }

    /**
     * Function called when drawing the hud
     *
     * @param matrices The UMatrixStack used for rendering in higher versions
     * @param x        Top left x-coordinate of the hud
     * @param y        Top left y-coordinate of the hud
     * @param scale    Scale of the hud
     * @param example  If the HUD is being rendered in example form
     */
    protected abstract void draw(UMatrixStack matrices, float x, float y, float scale, boolean example);

    /**
     * @param scale   Scale of the hud
     * @param example If the HUD is being rendered in example form
     * @return The width of the hud
     */
    protected abstract float getWidth(float scale, boolean example);

    /**
     * @param scale   Scale of the hud
     * @param example If the HUD is being rendered in example form
     * @return The height of the hud
     */
    protected abstract float getHeight(float scale, boolean example);

    /**
     * Function to do things before rendering anything
     *
     * @param example If the HUD is being rendered in example form
     */
    protected void preRender(boolean example) {
    }

    protected void resetPosition() {
        Position pos = defaultPosition;
        float width = position.getValue().getWidth();
        float height = position.getValue().getHeight();
//        positionAlignment.setValue(defaultAlignment);
        scale.setValue(defaultScale);
        position.getValue().anchor = pos.anchor;
        float anchorX = position.getValue().anchor.x;
        float anchorY = position.getValue().anchor.y;
        if (anchorX == 0f) {
            position.getValue().setX(pos.getX());
        } else if (anchorX == 0.5f) {
            position.getValue().setX(pos.getCenterX() - width / 2f);
        } else {
            position.getValue().setX(pos.getRightX() - width);
        }
        if (anchorY == 0f) {
            position.getValue().setY(pos.getY());
        } else if (anchorY == 0.5f) {
            position.getValue().setY(pos.getCenterY() - height / 2f);
        } else {
            position.getValue().setY(pos.getBottomY() - height);
        }
    }

    /**
     * Draw the background, the hud and all childed huds, used by HudCore
     */
    public void drawAll(UMatrixStack matrices, boolean example) {
        if (!example && !shouldShow()) return;
        preRender(example);
        position.getValue().setSize(getWidth(scale.getValue().floatValue(), example), getHeight(scale.getValue().floatValue(), example));
        draw(matrices, position.getValue().getX(), position.getValue().getY(), scale.getValue().floatValue(), example);
    }

    protected boolean shouldShow() {
        if (!showInGuis.getValue() && Platform.getGuiPlatform().getCurrentScreen() != null && !(Platform.getGuiPlatform().getCurrentScreen() instanceof OneConfigGui))
            return false;
        if (!showInChat.getValue() && Platform.getGuiPlatform().isInChat()) return false;
        return showInDebug.getValue() || !Platform.getGuiPlatform().isInDebug();
    }

    /**
     * @return If the hud is enabled
     */
    public boolean isEnabled() {
        return (config == null || config.isEnabled());
    }

    /**
     * @return If the hud is locked
     */
    public boolean isLocked() {
        return locked.getValue() && (config == null || config.isEnabled());
    }

    /**
     * Set the config to disable accordingly, intended for internal use
     *
     * @param config The config instance
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * @return The config of this HUD
     */
    public Config getConfig() {
        return this.config;
    }

    /**
     * @return The scale of the Hud
     */
    public float getScale() {
        return scale.getValue().floatValue();
    }

    /**
     * Set a new scale value
     *
     * @param scale   The new scale
     * @param example If the HUD is being rendered in example form
     */
    public void setScale(float scale, boolean example) {
        this.scale.setValue(scale);
        position.getValue().updateSizePosition(getWidth(scale, example), getHeight(scale, example));
    }

    public Switch showInChat = new Switch("Show in Chat", true);
    public Switch showInDebug = new Switch("Show in F3 (Debug)", false);
    public Switch showInGuis = new Switch("Show in GUIs", true);
}