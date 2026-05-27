package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class SlowDownEvent extends Event {
    private float strafe;
    private float forward;

    public SlowDownEvent(float strafe, float forward) {
        this.strafe = strafe;
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }


    @Override
    protected boolean isCancellable() {
        return false;
    }
}
