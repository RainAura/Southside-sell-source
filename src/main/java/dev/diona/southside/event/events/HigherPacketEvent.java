package dev.diona.southside.event.events;

import dev.diona.southside.event.PacketType;
import me.bush.eventbus.event.Event;
import net.minecraft.network.Packet;

public class HigherPacketEvent extends Event {
    private final PacketType type;
    private Packet<?> packet;

    public HigherPacketEvent(PacketType type, Packet<?> packet) {
        this.type = type;
        this.packet = packet;
    }

    public PacketType getType() {
        return type;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
