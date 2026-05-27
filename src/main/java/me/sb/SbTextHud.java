package me.sb;

import cc.polyfrost.oneconfig.hud.SingleTextHud;

public class SbTextHud extends SingleTextHud {
    public SbTextHud() {
        super("Test", true);
    }

    @Override
    public String getText(boolean example) {
        return "I'm an example HUD";
    }
}
