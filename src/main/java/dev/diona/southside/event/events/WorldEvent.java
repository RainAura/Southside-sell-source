package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldEvent extends Event {
    private final WorldClient worldClient;

    public WorldEvent(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    public WorldClient getWorldClient() {
        return worldClient;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
