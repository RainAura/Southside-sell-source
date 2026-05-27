package dev.diona.southside.util.misc.disablerMagic;

import net.minecraft.network.ThreadQuickExitException;

public class PacketCancelledException extends ThreadQuickExitException {
    public PacketCancelledException() {
        super();
    }
}
