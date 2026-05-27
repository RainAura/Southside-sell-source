package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.HigherPacketEvent;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Notification;
import dev.diona.southside.module.modules.movement.Flight;
import dev.diona.southside.module.modules.movement.Stuck;
import dev.diona.southside.module.modules.player.Alink;
import dev.diona.southside.util.misc.FakePlayer;
import dev.diona.southside.util.misc.disablerMagic.PacketProcessListenableFutureTask;
import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.FutureTask;

public class Disabler extends Module {
    private static Disabler INSTANCE;
    public final Dropdown modeValue = new Dropdown("Mode","Grim", "Grim");
//    public static ConcurrentLinkedDeque<Integer> pingPackets = new ConcurrentLinkedDeque<>();
    public final Switch grimPostValue = new Switch("Grim Post", true);
    public final Switch grimNoSlowValue = new Switch("Grim No Slow", false);
    public final Switch grimRotationValue = new Switch("Grim Rotation", true);
    public final Switch grimBlinkValue = new Switch("Grim Blink", true);
    public static ConcurrentLinkedDeque<Integer> pingPackets = new ConcurrentLinkedDeque<>();
    public static boolean processingFakePlayers = false;

    public Disabler(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();

        addDependency(this.grimPostValue.getLabel(), () -> modeValue.getMode().equals("Grim"));
        addDependency(this.grimRotationValue.getLabel(), () -> modeValue.getMode().equals("Grim"));
        addDependency(this.grimBlinkValue.getLabel(), () -> modeValue.getMode().equals("Grim"));
    }

    private boolean sentC07 = false;

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onPost(MotionEvent event) {
        if (event.getState() != EventState.POST) return;
        sentC07 = false;
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onHigherPacket(HigherPacketEvent event) {
        if (modeValue.getMode().equals("Grim") && grimNoSlowValue.getValue()) {
            if ((event.getPacket() instanceof CPacketPlayerTryUseItem) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {

                CPacketPlayerTryUseItemOnBlock p = new CPacketPlayerTryUseItemOnBlock(
                        new BlockPos(998, 244, 353),
                        EnumFacing.EAST,
                        EnumHand.MAIN_HAND,
                        0,
                        0,
                        0
                );
                p.test = true;
                mc.getConnection().sendPacketNoEvent(p);
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof CPacketPlayerDigging packet && packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
//                if (sentC07) {
//                    ChatUtil.info("?");
//                }
//                ChatUtil.info("?");
//                sentC07 = true;

                mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
                mc.getConnection().sendPacketNoEvent(new CPacketCustomPayload("test", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
                mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                event.setCancelled(true);
            }
        }
    }

    public static void onS08() {
        INSTANCE.s08 = true;
//        Stuck.onS08();
    }

    private boolean s08 = false;
    private static int flyTicks = 0;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (this.grimBlinkValue.getValue()) {
            mc.getConnection().sendPacketNoHigherEvent(new CPacketConfirmTransaction(1, (short) 1, true));
        }
        if (s08) {
            Stuck.onS08();
            s08 = false;
            while (mc.scheduledTasks.size() > 1) {
                grimProcessStoredPackets();
            }
        }
    }

    public static void fixC0F(CPacketConfirmTransaction packet) {
//        mc.getConnection().sendPacketNoEvent(packet);
        if (!shouldProcess()) return;
        int id = packet.getUid();
        if (id >= 0 || pingPackets.isEmpty()) {
            mc.getConnection().sendPacketNoEvent(packet);
        } else {
            while (true) {
                int current = pingPackets.getFirst();
                mc.getConnection().sendPacketNoEvent(new CPacketConfirmTransaction(packet.getWindowId(), (short) current, true));
                pingPackets.pollFirst();
                if (current == id || pingPackets.isEmpty()) {
                    break;
                }
//                else  {
//                    ChatUtil.info("漏了 " + current);
//                }
            }
        }
    }

    public static void preProcessPackets(boolean forceSkipAbilities) {
        LinkedList<FutureTask<?>> queue = new LinkedList();
        boolean flyReceived = false;
        while (!mc.preProcessedTasks.isEmpty()) {
            FutureTask<?> task = mc.preProcessedTasks.get(0);
            if (mc.getConnection() != null && mc.world != null && mc.player != null && task instanceof PacketProcessListenableFutureTask<?> packetTask) {
                if (!forceSkipAbilities && flyTicks <= 180 && packetTask.packetToProcess instanceof SPacketPlayerAbilities abilities
                        && !abilities.isAllowFlying()
                        && (mc.player.capabilities.isFlying || Southside.moduleManager.getModuleByClass(Flight.class).isEnabled())
                        && Southside.moduleManager.getModuleByClass(SpectatorAbuse.class).isEnabled()
                        && mc.player.inventory.getStackInSlot(8).isEmpty()) {
                    mc.playerController.setGameType(GameType.SURVIVAL);
                    mc.player.capabilities.allowFlying = true;
                    mc.player.capabilities.isFlying = true;
//                    flyTicks++;
                    flyReceived = true;
                    if (flyTicks < 180) {
                        break;
                    }
                }

                processingFakePlayers = true;
                if (packetTask.packetToProcess instanceof SPacketEntityHeadLook packet && packet.getEntity(mc.world) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketEntity packet && packet.getEntity(mc.world) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketEntityStatus packet && packet.getEntity(mc.world) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketEntityEffect packet && mc.world.getEntityByID(packet.getEntityId()) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketAnimation packet && mc.world.getEntityByID(packet.getEntityID()) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketEntityTeleport packet && mc.world.getEntityByID(packet.getEntityId()) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketEntityVelocity packet && mc.world.getEntityByID(packet.getEntityID()) instanceof EntityPlayer player && player.getEntityId() != mc.player.getEntityId()) {
                    packet.processPacket(mc.getConnection());
                }
                if (packetTask.packetToProcess instanceof SPacketDestroyEntities packet) {
                    packet.processPacket(mc.getConnection());
                }

                processingFakePlayers = false;
//                if (packetTask.packetToProcess instanceof SPacketDestroyEntities packet) {
//                    packet.processPacket(mc.getConnection());
//                }
            }
            queue.add(task);
            mc.preProcessedTasks.pollFirst();
        }
        if (flyReceived) {
            flyTicks++;
        }
        if (Southside.moduleManager != null) {
            Flight flight = (Flight) Southside.moduleManager.getModuleByClass(Flight.class);
            if (flyTicks == 1) {
                if (!flight.isEnabled()) flight.setEnable(true);
                Notification.addNotificationKeepTime("起飞", "Disabler", Notification.NotificationType.INFO, 9.);
            } else if (flyTicks > 180) {
                Notification.addNotification("再飞就飞踢了", "Disabler", Notification.NotificationType.WARN);
                if (flight.isEnabled()) flight.setEnable(false);
                flyTicks = 0;
            }
        }
        if (!flyReceived) {
            flyTicks = 0;
        }
        mc.scheduledTasks.add(queue);
    }

    public static FakePlayer getOrSpawnFakeEntity(EntityPlayer player) {
        if (player.fakePlayer == null) {
            player.fakePlayer = new FakePlayer(player);
        }
        return player.fakePlayer;
    }


    public static void grimProcessStoredPackets() {
        if (mc.scheduledTasks.isEmpty()) return;
        while (!mc.scheduledTasks.get(0).isEmpty())
        {
            Util.runTask(mc.scheduledTasks.get(0).poll(), Minecraft.LOGGER);
        }
        mc.scheduledTasks.remove(0);
    }

    @Override
    public String getSuffix() {
        return INSTANCE.modeValue.getMode();
    }

    public static boolean getGrimPost() {
        return INSTANCE != null && INSTANCE.isEnabled() && INSTANCE.modeValue.getMode().equals("Grim") && INSTANCE.grimPostValue.getValue() && !Flight.shouldDisableDisabler();
    }

    public static boolean shouldProcess() {
        if (Southside.moduleManager == null) return false;
        return !Alink.isInstanceEnabled();
    }

    public static boolean getGrimRotation() {
        return INSTANCE.grimRotationValue.getValue();
    }
}
