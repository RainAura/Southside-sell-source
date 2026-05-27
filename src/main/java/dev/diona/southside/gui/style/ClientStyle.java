package dev.diona.southside.gui.style;

import java.awt.*;

public abstract class ClientStyle {
    public String name;
    public ClientStyle(String name) {
        this.name = name;
    }
    public abstract Color getClickGuiBackgourndColor();
    public abstract Color getClickGuiTopBarColor();
    public abstract Color getClickGuiCategorySelectedColor();
    public abstract Color getClickGuiCategoryUnselectedColor();
    public abstract Color getClickGuiCategoryHoverColor();
    public abstract Color getClickGuiModuleEnabledColor();
    public abstract Color getClickGuiModuleDisabledColor();
    public abstract Color getClickGuiModuleBackgroundColor();
    public abstract Color getClickGuiModuleNameColor();
    public abstract Color getClickGuiModuleDescriptionColor();
    public abstract Color getClickGuiSettingBooleanValueEnabledColor();
    public abstract Color getClickGuiSettingBooleanValueDisabledColor();
    public abstract Color getClickGuiSettingNumberDragColor();
    public abstract Color getClickGuiSettingNumberReleaseColor();
    public abstract Color getClickGuiSettingNumberEnabledColor();
    public abstract Color getClickGuiSettingNumberDisabledColor();
    public abstract Color getClickGuiSettingNumberValueEnabledDragColor();
    public abstract Color getClickGuiSettingModeBackgroundColor();
    public abstract Color getClickGuiSettingModeTextColor();
    public abstract Color getClickGuiSettingTextColor();
    public abstract Color getClickGuiTitleNameColor();
    public abstract Color getClickGuiTitleVersionColor();
    public abstract Color getHudArrayListModuleNameColor();
    public abstract Color getHudArrayListModuleSuffixColor();
    public abstract Color getHudNotificationInfoColor();
    public abstract Color getHudNotificationWarningColor();
    public abstract Color getHudNotificationErrorColor();
    public abstract Color getHudNotificationEnabledColor();
    public abstract Color getHudNotificationDisabledColor();
    public abstract Color getHudNotificationTextColor();
    public abstract Color getFontShadowColor();
}
