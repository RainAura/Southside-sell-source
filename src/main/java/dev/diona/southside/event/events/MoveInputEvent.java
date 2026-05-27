package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;

public class MoveInputEvent extends Event {
    public MoveInputEvent(float moveForward, float moveStrafe, boolean jump, boolean sneak, float sneakSlowDownMultiplier) {
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }

    private float moveForward;
    private float moveStrafe;
    private boolean jump;
    private boolean sneak;
    private float sneakSlowDownMultiplier;

    public float getMoveForward() {
        return moveForward;
    }

    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }

    public float getMoveStrafe() {
        return moveStrafe;
    }

    public void setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isSneak() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public float getSneakSlowDownMultiplier() {
        return sneakSlowDownMultiplier;
    }

    public void setSneakSlowDownMultiplier(float sneakSlowDownMultiplier) {
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
