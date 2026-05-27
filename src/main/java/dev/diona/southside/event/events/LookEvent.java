package dev.diona.southside.event.events;

import dev.diona.southside.util.player.Rotation;
import me.bush.eventbus.event.Event;

public class LookEvent extends Event {
    private Rotation rotation;

    public LookEvent(Rotation rotation) {
        this.rotation = rotation;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
