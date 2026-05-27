package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class InputTickEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
