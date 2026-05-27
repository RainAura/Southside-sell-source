package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class KeyEvent extends Event {
    private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
