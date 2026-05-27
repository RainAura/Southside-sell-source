package dev.diona.southside.gui.style.styles;

import dev.diona.southside.gui.style.ClientStyle;

import java.awt.*;

public class SouthsideDarkStyle extends ClientStyle {
    public SouthsideDarkStyle() {
        super("Dark");
    }

    @Override
    public Color getClickGuiBackgourndColor() {
        return new Color(31, 31, 31);
    }

    @Override
    public Color getClickGuiTopBarColor() {
        return new Color(24, 24, 24);
    }

    @Override
    public Color getClickGuiCategorySelectedColor() {
        return new Color(255, 255, 255);
    }

    @Override
    public Color getClickGuiCategoryUnselectedColor() {
        return new Color(255, 255, 255);
    }

    @Override
    public Color getClickGuiCategoryHoverColor() {
        return new Color(46, 46, 46);
    }

    @Override
    public Color getClickGuiModuleEnabledColor() {
        return new Color(205, 205, 205);
    }

    @Override
    public Color getClickGuiModuleDisabledColor() {
        return new Color(49, 49, 49);
    }

    @Override
    public Color getClickGuiModuleBackgroundColor() {
        return new Color(36, 36, 36);
    }

    @Override
    public Color getClickGuiModuleNameColor() {
        return new Color(255, 255, 255);
    }

    @Override
    public Color getClickGuiModuleDescriptionColor() {
        return new Color(255, 255, 255);
    }

    @Override
    public Color getClickGuiSettingBooleanValueEnabledColor() {
        return new Color(105, 140, 255);
    }

    @Override
    public Color getClickGuiSettingBooleanValueDisabledColor() {
        return this.getClickGuiModuleDisabledColor();
    }

    @Override
    public Color getClickGuiSettingNumberDragColor() {
        return new Color(121, 59, 247);
    }

    @Override
    public Color getClickGuiSettingNumberReleaseColor() {
        return new Color(59, 103, 247);
    }

    @Override
    public Color getClickGuiSettingNumberEnabledColor() {
        return new Color(100, 180, 255);
    }

    @Override
    public Color getClickGuiSettingNumberDisabledColor() {
        return this.getClickGuiModuleDisabledColor();
    }

    @Override
    public Color getClickGuiSettingNumberValueEnabledDragColor() {
        return new Color(137, 105, 255);
    }

    @Override
    public Color getClickGuiSettingModeBackgroundColor() {
        return new Color(49, 49, 49);
    }

    @Override
    public Color getClickGuiSettingModeTextColor() {
        return new Color(204, 204, 204);
    }

    @Override
    public Color getClickGuiSettingTextColor() {
        return this.getClickGuiModuleNameColor();
    }

    @Override
    public Color getClickGuiTitleNameColor() {
        return new Color(204, 204, 204);
    }

    @Override
    public Color getClickGuiTitleVersionColor() {
        return new Color(204, 204, 204);
    }
    public Color getHudArrayListModuleNameColor() {
        return new Color(255, 255, 255);
    }
    public Color getHudArrayListModuleSuffixColor() {
        return new Color(200, 200, 200);
    }

    @Override
    public Color getHudNotificationInfoColor() {
        return new Color(33, 177, 242, 120);
    }

    @Override
    public Color getHudNotificationWarningColor() {
        return new Color(204, 167, 32, 80);
    }

    @Override
    public Color getHudNotificationErrorColor() {
        return new Color(241, 76, 76, 80);
    }

    @Override
    public Color getHudNotificationEnabledColor() {
        return new Color(20, 250, 90, 140);
    }

    @Override
    public Color getHudNotificationDisabledColor() {
        return new Color(255, 30, 30, 140);
    }

    @Override
    public Color getHudNotificationTextColor() {
        return new Color(255, 255, 255);
    }

    @Override
    public Color getFontShadowColor() {
        return new Color(0, 0, 0);
    }
}