package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class TextEvent extends Event {
    private String text;

    public TextEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
