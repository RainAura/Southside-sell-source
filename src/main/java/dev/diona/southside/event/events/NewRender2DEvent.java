package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class NewRender2DEvent extends Event {
    private final float partialTicks;
    private final ScaledResolution scaledResolution;

    public NewRender2DEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
