package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class MouseEvent extends Event {
    private final int button;
    private final int state;

    public MouseEvent(int button, int state) {
        this.button = button;
        this.state = state;
    }

    public int getButton() {
        return button;
    }

    public int getState() {
        return state;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
