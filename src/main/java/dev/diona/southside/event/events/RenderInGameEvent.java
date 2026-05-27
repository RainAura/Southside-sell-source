package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class RenderInGameEvent extends Event {
    public final ScaledResolution scaledResolution;
    public final float partialTicks;
    public final Type type;

    public RenderInGameEvent(ScaledResolution scaledResolution, final float partialTicks, Type type) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
        this.type = type;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public enum Type {
        None,
        ExpBar,
        PlayerStats,
        Hotbar
    }
}
