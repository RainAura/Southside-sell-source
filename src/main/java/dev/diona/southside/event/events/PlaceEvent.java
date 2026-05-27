package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class PlaceEvent extends Event {

    private boolean shouldRightClick;
    private int slot;

    public PlaceEvent(final int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(final int slot) {
        this.slot = slot;
    }

    public boolean isShouldRightClick() {
        return this.shouldRightClick;
    }

    public void setShouldRightClick(final boolean shouldRightClick) {
        this.shouldRightClick = shouldRightClick;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
