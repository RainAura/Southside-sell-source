package dev.diona.southside.util.misc.disablerMagic;

import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.network.Packet;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class PacketProcessListenableFutureTask<V> extends FutureTask<V> implements ListenableFuture<V> {
    public final Packet<?> packetToProcess;

    private final ExecutionList executionList = new ExecutionList();

    public static <V> PacketProcessListenableFutureTask<V> create(PacketProcessCallable<V> callable) {
        return new PacketProcessListenableFutureTask<>(callable);
    }

    public static <V> PacketProcessListenableFutureTask<V> create(PacketProcessRunnable runnable, @Nullable V result) {
        return new PacketProcessListenableFutureTask<>(runnable, result);
    }

    PacketProcessListenableFutureTask(PacketProcessCallable<V> callable) {
        super(callable);
        packetToProcess = callable.packetToProcess;
    }

    PacketProcessListenableFutureTask(PacketProcessRunnable runnable, @Nullable V result) {
        super(runnable, result);
        packetToProcess = runnable.packetToProcess;
    }

    public void addListener(Runnable listener, Executor exec) {
        this.executionList.add(listener, exec);
    }

    protected void done() {
        this.executionList.execute();
    }
}
