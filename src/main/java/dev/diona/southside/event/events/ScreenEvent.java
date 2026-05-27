package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class ScreenEvent extends Event {
    private final GuiScreen guiScreen;

    public ScreenEvent(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    public GuiScreen getGuiScreen() {
        return guiScreen;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
