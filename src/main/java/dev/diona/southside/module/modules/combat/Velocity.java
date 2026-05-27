package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.event.events.client.FailReduceKBEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.module.modules.movement.NoSlow;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.MovementUtil;
import dev.diona.southside.util.player.RayCastUtil;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector2d;

public class Velocity extends Module {

    public final Dropdown modeValue = new Dropdown("Mode", "Grim", "Simple", "Legit", "Grim", "Mix", "GrimOnGround");
    public final Switch grimLegitValue = new Switch("Grim Legit", false);
    public final Slider horizontalValue = new Slider("Horizontal", 0f, -2f, 2f, 0.05f);
    public final Slider verticalValue = new Slider("Vertical", 0f, -2f, 2f, 0.05f);
    public final Switch s08detect = new Switch("S08 Detect", true);
    public final Switch webValue = new Switch("Disable In Web", true);
    public final Switch liquidValue = new Switch("Disable In Liquid", false);
    public final Switch rayCastValue = new Switch("Ray Cast", false);
    public final Switch jumpRotateValue = new Switch("Jump Rotate", false);
    public static boolean attacked = false, velocity = false;
    private SPacketEntityVelocity velocityPacket;

    public Velocity(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(grimLegitValue.getLabel(), "Grim".equals(modeValue.getMode()) || "Mix".equals(modeValue.getMode()));
        addDependency(horizontalValue.getLabel(), "Simple".equals(modeValue.getMode()));
        addDependency(verticalValue.getLabel(), "Simple".equals(modeValue.getMode()));
    }

    @Override
    public boolean onDisable() {
        velocity = attacked = false;
        return true;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        attacked = velocity = false;
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook packet) {
            if (velocity && mc.playerController.gameIsSurvivalOrAdventure()) {
                if (s08detect.getValue() && mc.player.hurtTime > 0 && mc.player.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) <= 50)
                    ChatUtil.info("[velocity] detected S08");
            }
        }
        final String mode = modeValue.getMode();
        if (event.getPacket() instanceof SPacketEntityVelocity packet) {
            if (packet.getEntityID() != mc.player.getEntityId()) {
                return;
            }
            if (mc.player.isInWeb() && this.webValue.getValue()) {
                return;
            }
            if ((mc.player.isInWater() || mc.player.isInLava()) && this.liquidValue.getValue()) {
                return;
            }
            double strength = new Vec3d(packet.getMotionX(), packet.getMotionY(), packet.getMotionZ()).length();
            double horizontalStrength = new Vector2d(packet.getMotionX(), packet.getMotionZ()).length();
            if ("Simple".equals(mode)) {
                float horizontal = this.horizontalValue.getValue().floatValue();
                float vertical = this.verticalValue.getValue().floatValue();

                if (horizontal == 0F) {
                    if (vertical != 0F) {
                        mc.player.motionY = packet.getMotionY() * vertical / 8000.0;
                    }
                    event.setCancelled(true);
                    return;
                }

                packet.setMotionX((int) (packet.getMotionX() * horizontal));
                packet.setMotionY((int) (packet.getMotionY() * vertical));
                packet.setMotionZ((int) (packet.getMotionZ() * horizontal));
            }
            if ("Legit".equals(mode)) {
                if (jumpRotateValue.getValue()) {
                    if (packet.getMotionY() < 0) { // 无视摔落击退
                        velocityPacket = null;
                    } else {
                        velocityPacket = packet;
                    }
                }
            }
            if ("Grim".equals(mode) || "Mix".equals(mode)) {
                velocity = false;
                if (horizontalStrength <= 1000) return;
                velocity = true;
                attacked = false;
                velocityPacket = packet;
            }
            if ("GrimOnGround".equals(mode)) {
                if (mc.player.ticksExisted <= 10) return;
                if (mc.player.onGround && !MovementUtil.isMoving()) {
                    event.setCancelled(true);
                    velocity = true;
                }
            }
        }
    }

    private void attackEntity(Entity entity) {
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.resetCooldown();
    }

    private void attackNull() {
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.playerController.syncCurrentPlayItem();
        mc.getConnection().sendPacketNoEvent(new CPacketUseEntity(-123123));
        mc.player.resetCooldown();
    }

//    int justTesting = 0;
//    @EventListener
//    public void onHigherPacket(HigherPacketEvent event) {
//        if (event.getPacket() instanceof CPacketPlayer) {
//            justTesting = 0;
//        }
//        if (event.getPacket() instanceof CPacketEntityAction action && (action.getAction() == CPacketEntityAction.Action.START_SPRINTING || action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
//            justTesting++;
//            ChatUtil.info(justTesting + "");
//            if (justTesting == 2) {
//                new Exception().printStackTrace();
//            }
//        }
//    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void on(MotionEvent event) {
        if (mc.player == null) return;
        if (event.getState() != EventState.POST) return;
        final String mode = modeValue.getMode();
        if ("Legit".equals(mode) || "Mix".equals(mode)) {
            if (mc.player.onGround && mc.player.isSprinting()) {
                // 如何让玩家跟随设置的rotate并且forward 10tick?
//                mc.gameSettings.keyBindForward.setPressed(true);
                if (jumpRotateValue.getValue() && mc.player.hurtTime > 0) {
                    if (velocityPacket == null) return;
                    float motionX = (float) (velocityPacket.getMotionX() / 8000.0D);
                    float motionZ = (float) (velocityPacket.getMotionZ() / 8000.0D);
                    float fixedYaw = (float) Math.toDegrees(Math.atan2(motionX, -motionZ));
//                    mc.player.prevRotationYaw = fixedYaw;
                    mc.player.rotationYaw = fixedYaw;
//                    RotationUtil.setTargetRotation(new Rotation(fixedYaw, mc.player.rotationPitch), 2);
                    // MoveFix?
                }
                if (mc.player.hurtTime > 6 && !mc.gameSettings.keyBindJump.isPressed()) {
                    mc.gameSettings.keyBindJump.setPressed(true);
                } else {
                    mc.gameSettings.keyBindJump.setPressed(GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
                }
            }

        }
        if ("Grim".equals(mode) || "Mix".equals(mode)) {
            if (mc.player.hurtTime == 0) {
                velocity = attacked = false;
            }
            if (velocity && !attacked) {

                Entity entity = null;
                double reduceXZ = 1;
                RayTraceResult result = RayCastUtil.rayCast(RotationUtil.serverRotation, 3.2, 0.0F, mc.player, true, 0F, 1F);
                if (/* !Blink.isInstanceEnabled() && */ result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && Target.isTarget(result.entityHit) /* &&  result.entityHit == KillAura.getTarget() */) {
                    entity = result.entityHit;
                }
                if (entity == null && !rayCastValue.getValue()) {
                    Entity target = KillAura.getTarget();
                    if (target != null && target.getDistanceSq(mc.player) <= 9.5) {
                        entity = KillAura.getTarget();
                    }
                }

                if (entity == null || !entity.isEntityAlive()) {
                    Southside.eventBus.post(new FailReduceKBEvent());
                    return;
                }
                boolean blocking = NoSlow.severSideBlocking;

//                    if (blocking) {
//                        NoSlow.grimPre();
//                    }

                boolean state = mc.player.serverSprintState;
                if (!state) {
                    mc.getConnection().sendPacketNoEvent(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                Southside.eventBus.post(new AttackEvent(entity, EventState.PRE));
                Southside.eventBus.post(new AttackEvent(entity, EventState.POST));
                int count = grimLegitValue.getValue() ? 1 : 6;
                for (int i = 1; i <= count; i++) {
                    mc.getConnection().sendPacketNoEvent(new CPacketAnimation(EnumHand.MAIN_HAND));
                    CPacketUseEntity cPacketUseEntity = new CPacketUseEntity(entity);
                    cPacketUseEntity.critical = true;
                    mc.getConnection().sendPacketNoEvent(cPacketUseEntity);
                }
                if (!state) {
                    mc.getConnection().getNetworkManager().sendPacket(new CPacketPlayer(mc.player.onGround));
                    mc.playerStuckTicks += 1;
                    mc.getConnection().sendPacketNoEvent(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }
                attacked = true;
                reduceXZ = this.getMotion();

                if (attacked) {
                    mc.player.motionX *= reduceXZ;
                    mc.player.motionZ *= reduceXZ;
                }

//                    if (blocking) {
//                        NoSlow.grimPost();
//                    }
            }

        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if ("GrimOnGround".equals(modeValue.getMode()) && velocity) {
            mc.player.connection.sendPacketNoEvent(new CPacketPlayer(true));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
            velocity = false;
        }
    }

    private double getMotion() {
        return grimLegitValue.getValue() ? 0.6D : 0.07776D;
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }
}