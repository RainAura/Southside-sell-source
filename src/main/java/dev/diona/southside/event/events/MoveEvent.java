package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class MoveEvent extends Event {
    private final double x;
    private final double y;
    private final double z;
    private boolean isSafeWalk;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setSafeWalk(boolean safeWalk) {
        isSafeWalk = safeWalk;
    }

    public boolean isSafeWalk() {
        return isSafeWalk;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}