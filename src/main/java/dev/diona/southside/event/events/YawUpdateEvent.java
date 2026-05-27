package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class YawUpdateEvent extends Event {

    private float yaw;

    public YawUpdateEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
