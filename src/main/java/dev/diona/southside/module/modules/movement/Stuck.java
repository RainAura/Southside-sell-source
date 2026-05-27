package dev.diona.southside.module.modules.movement;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.PlayerUtil;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;

public class Stuck extends Module {
    public final Switch autoDisable = new Switch("AutoDisable", true);
    public final Switch rotationFirst = new Switch("Rotation First", true);
    private static Stuck INSTANCE;

    private boolean delayingC0F = false;

    public Stuck(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    private double x, y, z, motionX, motionY, motionZ;
    private boolean enableAgain = false;
    private int count = 0, rotationCount = 0;
    private boolean onGround = false;
    public boolean thrown = false;
    private Rotation rotation;
    private boolean closing = false;
    private static boolean needS08 = false;

    @Override
    public boolean onEnable() {
        if (mc.player == null) return true;
        onGround = mc.player.onGround;
        x = mc.player.posX;
        y = mc.player.posY;
        z = mc.player.posZ;
        motionX = mc.player.motionX;
        motionY = mc.player.motionY;
        motionZ = mc.player.motionZ;
        rotation = RotationUtil.getPlayerRotation();
        rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        delayingC0F = true;
        thrown = false;
        count = 20;
        rotationCount = 0;
        enableAgain = false;
        needS08 = false;
        return true;
    }

    @Override
    public boolean onDisable() {
        if (!closing /* && !PlayerUtil.isBlockUnder(mc.player.posY + mc.player.getEyeHeight()) */) {
            mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(mc.player.posX + 1000, mc.player.posY, mc.player.posZ, false));
            return false;
        }
        delayingC0F = false;
        return true;
    }

    public static void onS08() {
        if (needS08) return;
        INSTANCE.closing = true;
        INSTANCE.setEnable(false);
        INSTANCE.closing = false;
        if (INSTANCE.enableAgain) {
            INSTANCE.enableAgain = false;
            INSTANCE.setEnable(true);
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem || event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
//            if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemEnderPearl)) return;
//            if (thrown) return;
//            thrown = true;
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)
                return;
            updateRotation(event);
            return;
        }
        if (event.getPacket() instanceof CPacketPlayerDigging digging) {
            if (digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow) {
                    updateRotation(event);
                    return;
                }
            }
        }
        if (event.getPacket() instanceof CPacketPlayer packet) {
            if (needS08 && ++count < 3) {
                return;
            }
            event.setCancelled(true);
            needS08 = false;
            return;
        }
        if (event.getPacket() instanceof CPacketConfirmTransaction) {
            if (needS08) return;
            event.setCancelled(true);
            return;
        }
        if (event.getPacket() instanceof CPacketUseEntity) {
            event.setCancelled(true);
            return;
        }
    }

    private boolean updateRotation(PacketEvent event) {
        Rotation current = RotationUtil.getPlayerRotation();
        current.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        if (rotation.yaw == current.yaw && rotation.pitch == current.pitch) {
            return false;
        }
        rotation = current;
        event.setCancelled(true);
        if (rotationCount++ > 19 && rotationFirst.getValue()) {
            count = 0;
            rotationCount = 0;
            needS08 = true;
            mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(mc.player.posX + 1000, mc.player.posY, mc.player.posZ, false));
            return false;
        }
        mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Rotation(current.yaw, current.pitch, onGround));
        mc.getConnection().sendPacketNoEvent(event.getPacket());
        return true;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        onS08();
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
//        mc.getConnection().sendPacketNoEvent(new CPacketSteerBoat(true, true));

//        mc.playerStuckTicks += 1;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
//        mc.player.setPosition(x, y, z);

        if (PlayerUtil.isBlockUnder(mc.player.posY + mc.player.getEyeHeight()) && autoDisable.getValue()) {
            onS08();
        }
    }

    public static boolean isStuck() {
        return INSTANCE.isEnabled();
    }

    public static boolean isDelayingC0F() {
        return INSTANCE.delayingC0F && INSTANCE.isEnabled();
    }

    public static void throwPearl(Rotation current) {
        if (!INSTANCE.isEnabled()) return;
        current.apply();
        current.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        if (!INSTANCE.rotation.equals(current)) {
            mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Rotation(current.yaw, current.pitch, INSTANCE.onGround));
        }
        INSTANCE.rotation = current;
        mc.getConnection().sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
    }
}
