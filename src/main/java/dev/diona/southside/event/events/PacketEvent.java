package dev.diona.southside.event.events;

import dev.diona.southside.event.PacketType;
import me.bush.eventbus.event.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private final PacketType type;
    private final Packet<?> packet;

    public PacketEvent(PacketType type, Packet<?> packet) {
        this.type = type;
        this.packet = packet;
    }

    public PacketType getType() {
        return type;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
