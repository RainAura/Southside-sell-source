package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EntityMovementEvent extends Event {
    private final Entity entity;

    public EntityMovementEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
