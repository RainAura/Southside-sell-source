package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class JumpEvent extends Event {
    public void setMotion(float motion) {
        this.motion = motion;
    }

    private float motion;

    public JumpEvent(float motion) {
        this.motion = motion;
    }

    public float getMotion() {
        return motion;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
