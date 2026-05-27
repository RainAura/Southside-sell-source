package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.event.events.client.FailReduceKBEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.module.modules.player.Blink;
import dev.diona.southside.util.network.PacketUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.FakePlayerUtil;
import dev.diona.southside.util.player.RayCastUtil;
import dev.diona.southside.util.player.RotationUtil;
import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import top.fl0wowp4rty.phantomshield.annotations.Native;
import top.fl0wowp4rty.phantomshield.annotations.license.VirtualizationLock;

import java.util.ArrayList;
import java.util.List;

public class Critical extends Module {
    private static Critical INSTANCE;
    public final Dropdown modeValue = new Dropdown("Mode", "Grim", "Grim");
    public final Switch debugValue = new Switch("Debug", false);
    public final Switch hytValue = new Switch("Bypass HYT", true);
    public final Switch autoBlockValue = new Switch("Auto Block", true);

    private final List<List<Packet<?>>> packets = new ArrayList<>();
    private final List<Entity> sbHyt = new ArrayList<>();
    private final List<Boolean> groundStatus = new ArrayList<>();
    private boolean enabledHytSetting = false;
    private boolean working = false;
    private Entity overrideTarget = null;
    private boolean serverBlockState = false;
    private boolean landed = false;
    private boolean onGround = false;
    private EntityOtherPlayerMP fakePlayer = null;

    public Critical(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(debugValue.getLabel(), () -> false);
    }

    @Override
    public boolean onEnable() {
        stop();
        return true;
    }

    @Override
    public boolean onDisable() {
        stop();
        return true;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        stop();
    }

    private RayTraceResult serverResult = null;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (!mc.player.isEntityAlive()) {
            stop();
        }
//        CPacketPlayerTryUseItemOnBlock p = new CPacketPlayerTryUseItemOnBlock(
//                new BlockPos(998, 244, 353),
//                EnumFacing.EAST,
//                EnumHand.MAIN_HAND,
//                0,
//                0,
//                0
//        );
//        p.test = true;
//        mc.getConnection().sendPacketNoEvent(p);
//        mc.getConnection().sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));

        switch (this.modeValue.getMode()) {
            case "Grim" -> {
                if (Blink.isInstanceEnabled()) {
                    stop();
                    return;
                }
                if (packets.size() >= 150 && enabledHytSetting) {
                    stop();
                    return;
                }
                if (mc.player.fallDistance > 2) {
                    stop();
                    return;
                }
                if (mc.player.onGround) {
                    landed = true;
                }
                if (KillAura.getTargets(6F).isEmpty() && enabledHytSetting) {
                    stop();
                } else if (mc.player.fallDistance > 0.01F && !mc.player.onGround /* && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(MobEffects.BLINDNESS) && !mc.player.isRiding() */) {
                    if (landed) {
                        stop();
                    }
                    List<EntityLivingBase> targets = KillAura.getTargets(2.5F);
                    if (!working && ((targets != null && !targets.isEmpty()) || !enabledHytSetting)) {
                        stop();
                        enabledHytSetting = hytValue.getValue();
                        landed = false;
                        onGround = false;

                        if (!enabledHytSetting) {
                            overrideTarget = null;
                            RayTraceResult result = serverResult;
                            if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && Target.isTarget(result.entityHit)) {
                                overrideTarget = result.entityHit;
                            }
                        }

                        start();
                    }
                }
                if (this.working) {
                    packets.add(new ArrayList<>());
                    if (enabledHytSetting) {
                        RayTraceResult result = serverResult;
                        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && Target.isTarget(result.entityHit)) {
                            sbHyt.add(result.entityHit);
                        } else {
                            sbHyt.add(null);
                        }
                        groundStatus.add(mc.player.fallDistance > 0.01F && !mc.player.onGround);
//                        groundStatus.add(true);
                    }
                }
            }
        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() != EventState.POST) return;

        serverResult = RayCastUtil.rayCast(RotationUtil.serverRotation, 3, 0, mc.player, true, 0F, 1F);
    }

    @EventListener
    public void onMoveInput(MoveInputEvent event) {
        if (INSTANCE.modeValue.getMode().equals("Grim")) {
            if (working && mc.player.onGround && enabledHytSetting) {
                event.setJump(true);
            }
        }
    }

    private void releaseBlock() {
        mc.getConnection().sendPacketNoHigherEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
        mc.getConnection().sendPacketNoHigherEvent(new CPacketCustomPayload("test", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
        mc.getConnection().sendPacketNoHigherEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem));
    }

    private void startBlock() {
        CPacketPlayerTryUseItemOnBlock p = new CPacketPlayerTryUseItemOnBlock(
                new BlockPos(998, 244, 353),
                EnumFacing.EAST,
                EnumHand.MAIN_HAND,
                0,
                0,
                0
        );
        p.test = true;
        mc.getConnection().sendPacketNoHigherEvent(p);
    }

    @EventListener(priority = ListenerPriority.LOW)
    public void onPreMotion(MotionEvent event) {
        if (Southside.type != Southside.ClientType.DEV && this.debugValue.getValue()) {
            this.debugValue.setValue(false);
        }
        if (event.getState() != EventState.PRE) return;
        if (INSTANCE.modeValue.getMode().equals("Grim")) {
            if (working) {
                if (enabledHytSetting) {
                    if (serverBlockState) {
//                        releaseBlock();
//                        mc.getConnection().sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));
                    }
                    while (packets.size() >= 3 && (sbHyt.get(0) == null || !groundStatus.get(0))) {
                        pollAndAttack();
                        if (debugValue.getValue()) {
                            ChatUtil.info("poll bad " + (sbHyt.get(0) == null) + " " + (!groundStatus.get(0)));
                        }
                    }
                    if (mc.player.ticksExisted % 5 == 0 || mc.player.ticksExisted % 5 == 3) {
                        if (packets.size() >= 3 /* && sbHyt.getFirst() != null && sbHyt.getFirst().hurtResistantTime <= 30 */) {
                            pollAndAttack();
                        } else {
//                            ChatUtil.info("empty??");
                        }
                    }
                    if (serverBlockState) {
//                        startBlock();
//                        mc.getConnection().sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND, true));
                    }
                } else {
                    if (overrideTarget == null) {
                        stop();
                    }
                    if (debugValue.getValue()) {
                        ChatUtil.info("attack");
                    }
                    mc.getConnection().sendPacketNoHigherEvent(new CPacketAnimation(EnumHand.MAIN_HAND));
                    mc.getConnection().sendPacketNoHigherEvent(new CPacketUseEntity(overrideTarget));
                }
            }
        }
    }


    @Native
    public void pollAndAttack() {
        Entity entity = sbHyt.get(0);
        sbHyt.remove(0);
        groundStatus.remove(0);
        this.sendTick(packets.get(0));
        if (entity != null) {
            if (debugValue.getValue()) {
                ChatUtil.info("attack");
            }
            mc.getConnection().sendPacketNoHigherEvent(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.getConnection().sendPacketNoHigherEvent(new CPacketUseEntity(entity));
        }
        packets.remove(0);
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    public void onAttack(AttackEvent event) {
        if (getOverrideTarget() != event.getTargetEntity() && working) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onHigherPacket(HigherPacketEvent event) {
        if (PacketUtil.isCPacket(event.getPacket()) && working) {
            packets.get(packets.size() - 1).add(event.getPacket());
            event.setCancelled(true);
        }
    }


    @Native
    private void sendTick(List<Packet<?>> tick) {
        tick.forEach(packet -> {
            if (!enabledHytSetting) {
                if (packet instanceof CPacketPlayerTryUseItem) {
                    return;
                }
                if (packet instanceof CPacketUseEntity use && !use.critical) {
                    return;
                }
                if (packet instanceof CPacketPlayerDigging digging && digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    return;
                }
            }
            handleFakePlayerPacket(packet);
            mc.getConnection().sendPacketNoHigherEvent(packet);
        });
    }


    @Native
    public static void start() {
        if (!INSTANCE.enabledHytSetting && INSTANCE.overrideTarget == null) {
            return;
        }
        INSTANCE.serverBlockState = INSTANCE.autoBlockValue.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword;
//        if (!((KillAura) Southside.moduleManager.getModuleByClass(KillAura.class)).serverSideBlocking && INSTANCE.serverBlockState) {
//            mc.getConnection().sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND, true));
//        }
        if (INSTANCE.debugValue.getValue()) {
            ChatUtil.info("START");
            INSTANCE.fakePlayer = FakePlayerUtil.spawnFakePlayer();
        }
        INSTANCE.working = true;
    }

    public static void stop() {
        if (!INSTANCE.working) return;
        if (INSTANCE.modeValue.getMode().equals("Grim")) {
            if (INSTANCE.fakePlayer != null && INSTANCE.debugValue.getValue()) {
                try {
                    mc.world.removeEntity(INSTANCE.fakePlayer);
                } catch (Exception ignored) {
                }
            }
            mc.getConnection().sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));
            INSTANCE.working = false;
            INSTANCE.overrideTarget = null;
            INSTANCE.packets.forEach(INSTANCE::sendTick);
            INSTANCE.packets.clear();
            INSTANCE.sbHyt.clear();
            INSTANCE.groundStatus.clear();
            INSTANCE.onGround = false;
            if (INSTANCE.debugValue.getValue()) {
                ChatUtil.info("STOP");
            }
        }
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (fakePlayer == null) return;
        if (packet instanceof CPacketPlayer.Position position) {
            fakePlayer.setPositionAndRotationDirect(
                    position.getX(0D),
                    position.getY(0D),
                    position.getZ(0D),
                    fakePlayer.rotationYaw,
                    fakePlayer.rotationPitch,
                    3, true
            );
            fakePlayer.onGround = position.isOnGround();
        } else if (packet instanceof CPacketPlayer.Rotation rotation) {
            fakePlayer.setPositionAndRotationDirect(
                    fakePlayer.posX,
                    fakePlayer.posY,
                    fakePlayer.posZ,
                    rotation.getYaw(0F),
                    rotation.getPitch(0F),
                    3,
                    true
            );
            fakePlayer.onGround = rotation.isOnGround();

            fakePlayer.rotationYawHead = rotation.getYaw(0F);
            fakePlayer.rotationYaw = rotation.getYaw(0F);
            fakePlayer.rotationPitch = rotation.getPitch(0F);
        } else if (packet instanceof CPacketPlayer.PositionRotation positionRotation) {
            fakePlayer.setPositionAndRotationDirect(
                    positionRotation.getX(0D),
                    positionRotation.getY(0D),
                    positionRotation.getZ(0D),
                    positionRotation.getYaw(0F),
                    positionRotation.getPitch(0F),
                    3,
                    true
            );
            fakePlayer.onGround = positionRotation.isOnGround();

            fakePlayer.rotationYawHead = positionRotation.getYaw(0F);
            fakePlayer.rotationYaw = positionRotation.getYaw(0F);
            fakePlayer.rotationPitch = positionRotation.getPitch(0F);
        } else if (packet instanceof CPacketEntityAction action) {
            if (action.getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                fakePlayer.setSprinting(true);
            } else if (action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                fakePlayer.setSprinting(false);
            } else if (action.getAction() == CPacketEntityAction.Action.START_SNEAKING) {
                fakePlayer.setSneaking(true);
            } else if (action.getAction() == CPacketEntityAction.Action.STOP_SNEAKING) {
                fakePlayer.setSneaking(false);
            }
        } else if (packet instanceof CPacketAnimation animation) {
            fakePlayer.swingArm(animation.getHand());
        }
    }

    public static Entity getOverrideTarget() {
        return INSTANCE.isEnabled() ? INSTANCE.overrideTarget : null;
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }

    public static boolean isInstanceWorking() {
        return INSTANCE.isEnabled() && INSTANCE.working && INSTANCE.autoBlockValue.getValue();
    }
}