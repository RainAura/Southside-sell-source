package dev.diona.southside.util.misc.disablerMagic;


import net.minecraft.network.Packet;

import java.util.concurrent.Callable;

public class PacketProcessCallable<T> implements Callable<T> {
    public final Packet<?> packetToProcess;
    private final Runnable task;
    private final T result;

    public PacketProcessCallable(PacketProcessRunnable task, T result) {
        this.task = task;
        this.packetToProcess = task.packetToProcess;
        this.result = result;
    }

    public T call() {
        this.task.run();
        return this.result;
    }

    public String toString() {
        return super.toString() + "[Wrapped task = " + this.task + "]";
    }
}