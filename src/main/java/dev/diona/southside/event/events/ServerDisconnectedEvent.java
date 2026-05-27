package dev.diona.southside.event.events;

import me.bush.eventbus.event.Event;
import net.minecraft.util.text.ITextComponent;

public class ServerDisconnectedEvent extends Event {
    private final ITextComponent reason;

    public ServerDisconnectedEvent(ITextComponent reason) {
        this.reason = reason;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public ITextComponent getReason() {
        return reason;
    }
}
