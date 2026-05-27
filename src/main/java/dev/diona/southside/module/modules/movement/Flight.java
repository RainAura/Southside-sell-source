package dev.diona.southside.module.modules.movement;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.misc.Disabler;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;


public class Flight extends Module {
    private static Flight INSTANCE;

    public final Dropdown modeValue = new Dropdown("Mode", "Vanilla", "Vanilla", "GrimVertical");

    public Slider timerValue = new Slider("Timer", 0.45F, 0.1F, 1.0F, 0.1F);
    public final Switch autoDisableValue = new Switch("Auto Disable", true);

    public Flight(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    private int ticks = 0;
    private Vec3d pos;

    @EventListener
    public void onWorld(WorldEvent event) {
        if (this.autoDisableValue.getValue()) {
            this.setEnable(false);
        }
    }

    @Override
    public boolean onDisable() {
        if (modeValue.isMode("Vanilla")) {
            mc.lastTickToggledFlight = true;
        }
        mc.getTimer().tickLength = 50F;
        ticks = 0;
        exploited = false;
        pos = null;
        return super.onDisable();
    }

    @Override
    public boolean onEnable() {
        return super.onEnable();
    }

    @EventListener
    public void onStrafe(StrafeEvent event) {
        if (modeValue.isMode("Vanilla")) {
            event.setFriction(3f);
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }

    private boolean exploited = false;

    @EventListener
    public void onMotion(MotionEvent event) {
        if (modeValue.isMode("Vanilla")) {
            mc.player.motionY = -0.001D + (mc.gameSettings.keyBindJump.isKeyDown() ? 3f : 0.0D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? 3f : 0.0D);
        }
//        else if (modeValue.isMode("GrimVertical")) {
//            if (ticks >= 2) {
//                if (!exploited) {
//                    if (event.getState() == EventState.PRE) {
//                        pos = mc.player.getPositionVector();
//                        mc.player.setPosition(mc.player.posX + 114514, mc.player.posY, mc.player.posZ + 1919180);
//                        exploited = true;
//                    } else {
//                        mc.player.setPosition(pos.x, pos.y, pos.z);
//                    }
//                } else {
//                    mc.playerStuckTicks += 1;
//                }
//            }
//        }
    }

    double last = 0;

    @EventListener
    public void onPacket(PacketEvent event) {
        if (modeValue.isMode("Vanilla")) {
            if (event.getPacket() instanceof CPacketPlayer player) {
                player.setOnGround(true);
            }
        }
        if (modeValue.isMode("GrimVertical")) {
            if (event.getPacket() instanceof SPacketEntityVelocity velocity && velocity.getEntityID() == mc.player.getEntityId()) {
                double str = new Vec3d(velocity.getMotionX(), 0, velocity.getMotionZ()).length();
                if (str > 1 && autoDisableValue.getValue()) {
                    this.setEnable(false);
                }
            }
            if (event.getPacket() instanceof SPacketPlayerPosLook S08) {
//                ChatUtil.info(S08.getY() + " " + (S08.getY() - last));
                last = S08.getY();
                exploited = true;
//                event.setCancelled(true);
//                mc.getConnection().sendPacket(new CPacketConfirmTeleport(S08.getTeleportId()));
//                mc.player.setPosition(mc.player.posX + 114514, mc.player.posY, mc.player.posZ + 1919180);
//                mc.playerStuckTicks += 1;
//                mc.getConnection().sendPacketNoEvent(new CPacketPlayer.PositionRotation(114514, -1, 1919810, S08, false));
            }
//            if (ticks >= 2) {
//                if (event.getPacket() instanceof CPacketPlayer player) {
//                    event.setCancelled(true);
//                    mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(114514, -1, 1919810, false));
//                }
//            }
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (modeValue.isMode("GrimVertical")) {
            if (mc.player.fallDistance > 2F && autoDisableValue.getValue()) {
                this.toggle();
            }
            if (ticks == 0) {
                if (mc.player.onGround) {
                    mc.player.jump();
                }
            }
            else if (ticks <= 5) {
                mc.getTimer().tickLength = 50F / timerValue.getValue().floatValue();
            } else {
                mc.getTimer().tickLength = 50F;
            }
            ticks++;
            if (exploited || ticks == 2) {
                exploited = false;
                mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(
                        mc.player.posX + 114514, -1, mc.player.posZ + 1919180, false));
            }
            if (ticks > 2) {
                mc.playerStuckTicks++;
            }
        }
    }

    public static boolean isInstanceEnabled() {
        return INSTANCE.isEnabled();
    }

    public static boolean shouldDisableDisabler() {
        return INSTANCE.isEnabled() && INSTANCE.modeValue.isMode("GrimVertical");
    }
}
