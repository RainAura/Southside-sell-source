package dev.diona.southside.event.events.client;

import me.bush.eventbus.event.Event;

public class FailReduceKBEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
