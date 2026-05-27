package dev.diona.southside.event.events;

import dev.diona.southside.event.EventState;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.event.Event;

public class MotionEvent extends Event {
    private final EventState state;

    public MotionEvent(EventState state) {
        this.state = state;
    }

    public EventState getState() {
        return state;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
