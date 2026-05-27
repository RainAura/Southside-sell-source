package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class PushOutEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
