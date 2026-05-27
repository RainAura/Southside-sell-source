package dev.diona.southside.event.events;

import dev.diona.southside.event.EventState;
import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent extends Event {
    private final Entity targetEntity;
    private final EventState state;

    public AttackEvent(Entity targetEntity, EventState state) {
        this.targetEntity = targetEntity;
        this.state = state;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public EventState getState() {
        return state;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
