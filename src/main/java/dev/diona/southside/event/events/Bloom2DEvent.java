package dev.diona.southside.event.events;

import dev.diona.southside.event.EventState;
import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Bloom2DEvent extends Event {
    private final EventState state;
    private final ScaledResolution sr;
    private final float partialTicks;

    public Bloom2DEvent(EventState state, ScaledResolution sr, float partialTicks) {
        this.state = state;
        this.sr = sr;
        this.partialTicks = partialTicks;
    }

    public EventState getState() {
        return state;
    }

    public ScaledResolution getSr() {
        return sr;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
