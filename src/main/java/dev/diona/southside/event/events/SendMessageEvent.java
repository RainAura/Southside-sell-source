package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class SendMessageEvent extends Event {
    private String message;

    public SendMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
