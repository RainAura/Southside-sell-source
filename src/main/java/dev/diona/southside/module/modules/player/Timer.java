package dev.diona.southside.module.modules.player;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import dev.diona.southside.event.PacketType;
import dev.diona.southside.event.events.HigherPacketEvent;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.network.PacketUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Timer extends Module {
    public Timer(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private final Queue<Packet<?>> packets = new ConcurrentLinkedDeque<>();
    private long balance = 0L;
    private AtomicInteger ticks = new AtomicInteger(0);
    private TimerUtil release = new TimerUtil();


    @Override
    public boolean onEnable() {
        mc.getTimer().tickLength = 50F;
        balance = 0L;
        ticks.set(0);
        return true;
    }

    @Override
    public boolean onDisable() {
        mc.getTimer().tickLength = 50F;
        return true;
    }

    @EventListener
    public void onPacket(HigherPacketEvent event) {
        if (mc.player == null || mc.player.ticksExisted < 60) {
            balance = 0L;
            long lastFlying = System.currentTimeMillis();
            ticks.set(9);
            release.reset();
            return;
        }

        Packet<?> packet = event.getPacket();

        if (PacketUtil.isEssential(packet)) return;

        if (PacketUtil.isCPacket(packet)) {
            if (packet instanceof CPacketPlayer c03) {
                this.balance += 50L;
                ticks.incrementAndGet();
                event.setCancelled(true);
                packets.add(packet);

                /*
                    快速释放，过掉Vulcan的TimerA检测，包和包之间延迟要大于50ms或小于4ms
                 */
                if (release.hasReached(150L) && !packets.isEmpty()) {
                    packets.forEach(p -> {
                        mc.player.connection.sendPacketNoHigherEvent(p);
                    });
                    balance -= release.passed() > 5000 ? 5000 :release.passed();
                    packets.clear();
                    release.reset();
                }

                if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
                    if (this.ticks.get() % 400 == 0) {
                        this.balance = -200L;
                        this.ticks.set(0);
                    }
                } else {
                    if (this.ticks.get() % 3000 == 0) {
                        this.balance = -200L;
                        this.ticks.set(0);
                    }
                }

            }
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getType() != PacketType.RECEIVE || mc.player == null || mc.player.ticksExisted < 60) return;
        Packet<?> packet = event.getPacket();
        if (packet instanceof SPacketPlayerPosLook packetIn) {
            event.setCancelled(true);
            balance -= ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8) ? 150L : 50L;
            EntityPlayer entityplayer = mc.player;
            double d0 = packetIn.getX();
            double d1 = packetIn.getY();
            double d2 = packetIn.getZ();
            float f = packetIn.getYaw();
            float f1 = packetIn.getPitch();

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
                d0 += entityplayer.posX;
            } else {
                entityplayer.motionX = 0.0D;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
                d1 += entityplayer.posY;
            } else {
                entityplayer.motionY = 0.0D;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
                d2 += entityplayer.posZ;
            } else {
                entityplayer.motionZ = 0.0D;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
                f1 += entityplayer.rotationPitch;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
                f += entityplayer.rotationYaw;
            }

            double d3;
            double d4 = mc.player.posX - d0;
            double d5 = mc.player.posY - d1;
            double d6 = mc.player.posZ - d2;
            d3 = d4 * d4 + d5 * d5 + d6 * d6;

            if (d3 >= 0.25D) {
                entityplayer.setPosition(d0, d1, d2); // No rotate
            } // 差异太小 Vanilla 认为同步 ignore

            // 注意！！此代码对Grim无效，GrimAC会对S08强同步，必须和S08的x,y,z,yaw,pitch一致才会同步，不然会flag BadPacket

//             mc.player.connection.sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId())); // 谁知道要不要确认传送？？
        }
    }

    @EventListener
    public void onTick(UpdateEvent e) {

        final int RESET_TICK = ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8) ? 400 : 3000;
        if (balance < -200L && RESET_TICK - ticks.get() > 20 && (mc.player.movementInput.forwardKeyDown || mc.player.movementInput.backKeyDown ||
                mc.player.movementInput.rightKeyDown || mc.player.movementInput.leftKeyDown || mc.player.movementInput.jump)) {
            long balanceTick = Math.abs(balance / 50L);

            if (RESET_TICK - ticks.get() > balanceTick) {
//                mc.getTimer().tickLength = (float) (50F / (1.0 + (Math.abs(balance) - 200L) / 50F / (400 - ticks.get()) * 0.05F));
                float maxTick = 1.0F + (double) balanceTick / 20 > 2.0 ? (float) 2.0 : (1.0F + (float) balanceTick / 20);
                mc.getTimer().tickLength = 50F / maxTick;
            } else {
                float maxTick = (1.0F + (double) (ticks.get() - 4) / 20) > 2.0 ? (float) 2.0 : (1.0F + (float) (ticks.get() - 4) / 20);
                mc.getTimer().tickLength = 50F / maxTick;
            }
        } else {
            mc.getTimer().tickLength = 50F;
        }
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        final int RESET_TICK = ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8) ? 400 : 3000;
        String msg = String.format("balance: %s | timer: %.3f | resetTick: %s", balance, 50F / mc.getTimer().tickLength, RESET_TICK - ticks.get());
        // 获取屏幕分辨率对象
        ScaledResolution scaledResolution = event.getSr();
        // 获取屏幕的宽度和高度
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int x = MathHelper.floor((screenWidth - mc.fontRenderer.getStringWidth(msg)) / 2.0);
        int y = screenHeight / 2 + 20; // 屏幕底部往上10个像素
        mc.fontRenderer.drawStringWithShadow(msg, x, y, -1);
    }
}
