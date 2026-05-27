package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.FakePlayer;
import dev.diona.southside.util.network.PacketUtil;
import dev.diona.southside.util.player.FakePlayerUtil;
import dev.diona.southside.util.world.ProjectileUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {
    private static Blink INSTANCE;
    private static final int ENTITY_ID = -1234;
    private List<List<Packet<?>>> packets = new ArrayList<>();
    public static FakePlayer fakePlayer;
    private int ticks;
    private boolean closing;
    public final Dropdown modeValue = new Dropdown("Mode", "Simple", "Simple", "SkyWars", "BedWars");
    public final Switch smartHitValue = new Switch("Smart Kill Aura", true);
    public final Slider hitCountValue = new Slider("Smart Hit Count", 1, 1, 3, 1);
    public final Switch slowReleaseValue = new Switch("Slow Release", false);
    public final Switch ignoreKeepAlive = new Switch("Ignore KeepAlive", false);
    public final Switch antiAimValue = new Switch("Anti Aim", true);
    public final Switch antiAimArrowValue = new Switch("Arrow", true);
    public final Switch antiAimProjectileValue = new Switch("Projectile", true);
    public final Switch antiAimTNTValue = new Switch("TNT", true);
    public final Switch antiAimPlayerValue = new Switch("Player", true);
    private List<Vec3d> realPos = new ArrayList<>();
    private Vec3d lastPos;

    public Blink(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(hitCountValue.getLabel(), smartHitValue.getLabel());
        addDependency(slowReleaseValue.getLabel(), () -> modeValue.getMode().equals("SkyWars") || modeValue.getMode().equals("BedWars"));
    }

    @Override
    public boolean onEnable() {
        if (mc.player == null) {
            this.setEnable(false);
            return false;
        }
        packets.clear();
        packets.add(new ArrayList<>());
        realPos.add(new Vec3d(
                mc.player.posX,
                mc.player.posY,
                mc.player.posZ
        ));
        ticks = 0;
        fakePlayer = FakePlayerUtil.spawnFakePlayer();
        closing = false;
        lastPos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
        return true;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        packets.clear();
        realPos.clear();
        ticks = 0;
        closing = true;
        this.setEnable(false);
    }

    @Override
    public boolean onDisable() {
        closing = true;
        switch (modeValue.getMode()) {
            case "Simple", "SkyWars" -> {
                packets.forEach(this::sendTick);
                packets.clear();
                realPos.clear();
            }
        }
        if (!packets.isEmpty()) {
            return false;
        }

        try {
            if (fakePlayer != null)
                mc.world.removeEntity(fakePlayer);
        } catch (Exception ignored) {
        }

        return true;
//        return packets.isEmpty();
    }

    @EventListener
    public void onPacket(HigherPacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (PacketUtil.isEssential(packet)) return;

        if (ignoreKeepAlive.getValue() && event.getPacket() instanceof CPacketKeepAlive)
            return;

        if (PacketUtil.isCPacket(packet)) {
            mc.addScheduledTask(() -> {
                packets.get(packets.size() - 1).add(packet);
            });
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (!mc.player.isEntityAlive() && !closing) {
            this.setEnable(false);
        }
        ticks++;
        packets.add(new ArrayList<>());
        realPos.add(new Vec3d(
                mc.player.posX,
                mc.player.posY,
                mc.player.posZ
        ));
        switch (this.modeValue.getMode()) {
            case "SkyWars" -> {
                if (packets.size() > 195 || (ticks % 10 == 0 && slowReleaseValue.getValue())) {
                    this.poll();
                }
            }
            case "BedWars" -> {
                if (packets.size() > 195 || (ticks % 10 == 0 && slowReleaseValue.getValue())) {
                    this.poll();
                }
                if (closing) {
                    int remain = 5;
                    while (remain > 0 && !packets.isEmpty()) {
                        this.poll();
                        remain--;
                    }
                    if (packets.isEmpty()) {
                        this.setEnable(false);
                        return;
                    }
                }
            }
        }
        if (antiAimValue.getValue()) {
            while (true) {
                boolean dangerous = false;
                for (Entity entity : mc.world.getLoadedEntityList()) {
                    if ((antiAimArrowValue.getValue() && entity instanceof EntityArrow arrow && !arrow.inGround) ||
                            (antiAimProjectileValue.getValue() && entity instanceof EntitySnowball || entity instanceof EntityEgg) ||
                            (antiAimTNTValue.getValue() && entity instanceof EntityTNTPrimed) ||
                            (antiAimPlayerValue.getValue() && entity instanceof EntityPlayer player && player.getUniqueID() != mc.player.getUniqueID())
                    ) {
                        if (this.isDangerous(entity)) {
                            dangerous = true;
                            break;
                        }
                    }
                }
                if (dangerous && packets.size() >= 3) {
                    this.poll();
                } else {
                    break;
                }
            }
        }
    }

    private boolean isDangerous(Entity entity) {
        final float width = 1.2F;
        final float height = 2.2F;
        if (entity instanceof IProjectile projectile) {
            float motionSlowdown = 0.99F, size = 1.2F, gravity = 0.05F;
            if (projectile instanceof EntityArrow) {
                motionSlowdown = 0.99F;
                size = 1.2F;
                gravity = 0.05F;
            } else if (projectile instanceof EntitySnowball || projectile instanceof EntityEgg) {
                motionSlowdown = 0.99F;
                gravity = 1.2F;
                size = 0.25F;
            }
            return ProjectileUtil.predictBox(
                    entity.posX,
                    entity.posY,
                    entity.posZ,
                    entity.motionX,
                    entity.motionY,
                    entity.motionZ,
                    motionSlowdown,
                    size,
                    gravity,
                    realPos,
                    width / 2,
                    height
            );
        } else if (entity instanceof EntityTNTPrimed tnt) {
            Vec3d pos = realPos.get(0);
            return (tnt.getDistanceSq(
                    pos.x,
                    pos.y,
                    pos.z
            ) <= 30 && tnt.getFuse() <= 10);
        } else if (entity instanceof EntityPlayer player) {
            Vec3d pos = realPos.get(0);
            return (player.getDistanceSq(
                    pos.x,
                    pos.y,
                    pos.z
            ) <= 20);
        }
        return false;
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
//        final float width = 0.6F;
//        final float height = 2F;
//        RenderUtil.drawAxisAlignedBB(new AxisAlignedBB(
//                realPos.x - width / 2,
//                realPos.y - height / 2,
//                realPos.z - width / 2,
//                realPos.x + width / 2,
//                realPos.y + height / 2,
//                realPos.z + width / 2
//        ), Color.WHITE);
//        RenderUtil.drawTracerLine(realPos.x, realPos.y, realPos.z, Color.WHITE);
    }

    private void poll() {
        if (packets.isEmpty()) return;
        this.sendTick(packets.get(0));
        packets.remove(0);
    }

    @Override
    public String getSuffix() {
        return modeValue.getMode();
    }

    private void sendTick(List<Packet<?>> tick) {
//        lastPos = null;
        tick.forEach(packet -> {
            mc.getConnection().sendPacketNoHigherEvent(packet);
            this.handleFakePlayerPacket(packet);
        });
        if (!realPos.isEmpty()) {
            realPos.remove(0);
        }
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (packet instanceof CPacketPlayer.Position position) {
            fakePlayer.setPositionAndRotationDirect(
                    position.getX(0D),
                    position.getY(0D),
                    position.getZ(0D),
                    fakePlayer.rotationYaw,
                    fakePlayer.rotationPitch,
                    3, true
            );
            lastPos = new Vec3d(position.getX(0D), position.getY(0D), position.getZ(0D));
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

            lastPos = new Vec3d(positionRotation.getX(0D), positionRotation.getY(0D), positionRotation.getZ(0D));
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

    public static int getReleaseSpeedModifier(int ticks, int length) {
        switch (INSTANCE.modeValue.getMode()) {
            case "Simple": {
                return 0;
            }
            case "BedWars": {
                if (INSTANCE.closing) {
                    return 5;
                }
                if (length >= 195) return 1;
                return INSTANCE.slowReleaseValue.getValue() ? (ticks % 10 == 0 ? 1 : 0) : 0;
            }
            case "SkyWars": {
                if (length >= 195) return 1;
                return INSTANCE.slowReleaseValue.getValue() ? (ticks % 10 == 0 ? 1 : 0) : 0;
            }
        }
        return 1;
    }

    @EventListener
    public void onAttack(AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityPlayer player) {
            player.blinkHitPos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
            player.blinkHitCount++;
        }
    }

    public boolean isSmartHit() {
        return this.isEnabled() && this.smartHitValue.getValue();
    }

    public int getMaxHitCount() {
        return this.isSmartHit() ? this.hitCountValue.getValue().intValue() : 0;
    }

    public static boolean isInstanceEnabled() {
        return INSTANCE.isEnabled();
    }
}
