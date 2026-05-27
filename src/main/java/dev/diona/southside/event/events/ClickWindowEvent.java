package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.inventory.ClickType;

public class ClickWindowEvent extends Event {
    private final int windowId;
    private final int slotId;
    private final int mouseButtonClicked;
    private final ClickType type;

    public ClickWindowEvent(int windowId, int slotId, int mouseButtonClicked, ClickType type) {
        this.windowId = windowId;
        this.slotId = slotId;
        this.mouseButtonClicked = mouseButtonClicked;
        this.type = type;
    }

    public int getWindowId() {
        return windowId;
    }

    public int getSlotId() {
        return slotId;
    }

    public int getMouseButtonClicked() {
        return mouseButtonClicked;
    }

    public ClickType getType() {
        return type;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
