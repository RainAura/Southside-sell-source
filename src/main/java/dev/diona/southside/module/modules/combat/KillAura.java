package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.Binding;
import dev.diona.southside.module.modules.client.Notification;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.module.modules.movement.Stuck;
import dev.diona.southside.module.modules.player.Blink;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.PredictionUtil;
import dev.diona.southside.util.player.RayCastUtil;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.player.RotationUtil;
import dev.diona.southside.util.quickmacro.HytUtil;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.RandomUtils;
import org.lwjglx.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Binding(value = Keyboard.KEY_R)
public class KillAura extends Module {
    private static KillAura INSTANCE;
    public final Dropdown modeValue = new Dropdown("Mode", "Switch", "Single", "Switch");
    public final Slider switchDelayValue = new Slider("Switch Delay", 0, 0, 1000, 1);
    public final Dropdown attackModeValue = new Dropdown("CPS Mode", "Tick", "Delay", "Tick", "Hurttime");
    public final Dropdown sortMode = new Dropdown("Priority", "Distance", "Distance", "Health");
    public final Slider minCpsValue = new Slider("Min CPS", 8, 1, 20, 1);
    public final Slider maxCpsValue = new Slider("Max CPS", 12, 1, 20, 1);
    public final Slider minRangeValue = new Slider("Min Range", 3, 3, 6, 0.01);
    public final Slider maxRangeValue = new Slider("Max Range", 3, 3, 6, 0.01);
    public final Slider wallRangeValue = new Slider("Wall Range", 3, 0, 6, 0.01);
    public final Dropdown autoBlockValue = new Dropdown("Auto Block", "Grim", "OFF", "Fake", "Grim");
    public final Slider blockRangeValue = new Slider("Block Range", 5, 0, 8, 0.01);
    public final Slider discoverRangeValue = new Slider("Discover Range", 5, 0, 8, 0.01);
    public final Dropdown rotationModeValue = new Dropdown("Rotation Mode", "Normal", "Normal", "Smooth");
    public final Slider minRotationSpeedValue = new Slider("Min Rotation Speed", 180, 0, 180, 0.1);
    public final Slider maxRotationSpeedValue = new Slider("Max Rotation Speed", 180, 0, 180, 0.1);
    public final Switch usingValue = new Switch("Attack When Using", false);
    public final Slider respawnCooldownValue = new Slider("Respawn Cooldown", 10, 0, 20, 1);
    public final Switch silentRotationValue = new Switch("Silent Rotation", true);
    public final Switch autoDisableValue = new Switch("Auto Disable", false);
    public final Switch sprintValue = new Switch("Sprint", true);
    public final Switch multiValue = new Switch("Multi Raycast", true);

    public KillAura(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();

        addDependency(switchDelayValue.getLabel(), () -> modeValue.getMode().equals("Switch"));
        addDependency(blockRangeValue.getLabel(), () -> !autoBlockValue.getMode().equals("OFF"));
        addDependency(minCpsValue.getLabel(), () -> attackModeValue.getMode().equals("Delay"));
        addDependency(maxCpsValue.getLabel(), () -> attackModeValue.getMode().equals("Delay"));

        this.addRangedValueRestrict(minCpsValue, maxCpsValue);
        this.addRangedValueRestrict(minRotationSpeedValue, maxRotationSpeedValue);
    }

    public List<EntityLivingBase> targets = new ArrayList<>();
    public List<EntityLivingBase> discoveredTargets;
    public EntityLivingBase target;
    public int targetIndex = -1;
    public final TimerUtil switchTimer = new TimerUtil();
    private final TimerUtil cpsTimer = new TimerUtil();
    private long cpsDelay = 0;
    private int lastCPSMin = 0, lastCPSMax = 0;
    private final TimerUtil enableSprintTimer = new TimerUtil();

    public boolean blocking = false, shouldBlock, serverSideBlocking;

    @Override
    public boolean onEnable() {
        this.blocking = false;
        return true;
    }

    @Override
    public boolean onDisable() {
        enableSprintTimer.reset();
        this.stopBlock();
        if (this.targets != null) {
            this.targets.clear();
        }
        this.target = null;
        return true;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        this.close();
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (targets == null) return;
        for (EntityLivingBase target : targets) {
            Color color = new Color(0, 255, 0, 100);
            if (target == this.target) {
                final var alpha = target.hurtTime * 5;
                color = new Color(255, 0, 0, 100 + alpha);
            }
            RenderUtil.boundingESPBoxFilled(PredictionUtil.PredictedTarget(target, 2), color);
        }
    }

    private boolean canAttack() {
        return (!mc.player.isHandActive() || (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) || this.usingValue.getValue()) &&
                !Stuck.isStuck() &&
                mc.player.ticksExisted >= respawnCooldownValue.getValue().intValue() /* &&
                !Blink.isInstanceEnabled() */;
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onUpdate(UpdateEvent event) {
//        if (RotationUtil.targetRotation != null) return;
        if (mc.player.inventory.getStackInSlot(0).getItem() == Items.NETHER_STAR) {
            target = null;
            return;
        }

        float autoBlockRange = blockRangeValue.getValue().floatValue();
        shouldBlock = !this.getTargets(autoBlockRange).isEmpty(); // && !Critical.isInstanceWorking();
        if (!shouldBlock) {
            this.stopBlock();
        }

//        if (Blink.isInstanceEnabled()) return;

        if (shouldBlock) {
            this.preBlock();
        }
        float range = MathUtil.getRandomInRange(minRangeValue.getValue().floatValue(), maxRangeValue.getValue().floatValue());
        float wallRange = this.wallRangeValue.getValue().floatValue();
        if (!this.canAttack()) return;

        if (!mc.player.isEntityAlive() || mc.player.getHealth() <= 0) {
            this.close();
            if (!this.isEnabled()) return;
        }

        if (minCpsValue.getValue().intValue() != this.lastCPSMin || maxRangeValue.getValue().intValue() != this.lastCPSMax) {
            this.lastCPSMin = minCpsValue.getValue().intValue();
            this.lastCPSMax = maxRangeValue.getValue().intValue();
            this.resetCPS();
        }

        float discoverRange = discoverRangeValue.getValue().floatValue();
        this.targets = getTargets(Math.max(range + 1F, discoverRange), sortMode.getMode());

//        discoveredTargets = getTargets(discoverRange);

        if (targets.isEmpty()) {
//                if (!discoveredTargets.isEmpty()) {
//                    EntityLivingBase closest = discoveredTargets.get(0);
//                    rotateTo(closest, discoverRange, discoverRange);
//                }
            return;
        }

        if (target != null && !Target.isTarget(target)) {
            target = null;
        }

        if (this.modeValue.getMode().equals("Switch")) {
            if (target == null || !targets.contains(target)) {
                targetIndex = 0;
                target = targets.get(targetIndex);
                switchTimer.reset();
            } else {
                targetIndex = targets.indexOf(target);
            }

            boolean result = false;
            if (RotationUtil.calculate(target, range, wallRange, 1F, 1F) == null || switchTimer.hasReached(switchDelayValue.getValue().longValue())) {
                switchTimer.reset();
                int trial = 0;
                while (trial < targets.size()) {
                    targetIndex += 1;
                    trial++;
                    if (targetIndex == targets.size()) {
                        targetIndex = 0;
                    }
                    if (targets.get(targetIndex) == target) continue;
                    if (this.rotateTo(targets.get(targetIndex), range, wallRange)) {
                        target = targets.get(targetIndex);
                        result = true;
                        break;
                    }
                }
            }
            if (!result) {
                this.rotateTo(target, range, wallRange);
            }
        } else {
            targetIndex = -1;
//            target = targets.get(targetIndex);
            target = null;
            for (int i = 0; i < targets.size(); i++) {
                if (this.rotateTo(targets.get(i), range, wallRange)) {
                    targetIndex = i;
                    target = targets.get(i);
                    break;
                }
            }
        }

        if (!sprintValue.getValue()) {
            if (target == null)
                mc.player.setSprinting(false);
        }

        
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (!this.canAttack()) return;

        try {
            if (HytUtil.isInHyt()) return;

            float range = (float) MathUtil.getRandomInRange(minRangeValue.getValue().floatValue(), maxRangeValue.getValue().floatValue());
            float wallRange = this.wallRangeValue.getValue().floatValue();
            if (event.getState() == EventState.POST) {
                this.tryAttack(target, range, wallRange);
            }
        } finally {
            if (shouldBlock && event.getState() == EventState.POST) {
                this.postBlock();
            }
        }
    }

    public void preBlock() {
        if (!canBlock()) {
            this.blocking = false;
            this.stopBlock();
            return;
        }
        if (mc.getConnection() == null) return;
        this.blocking = true;
    }

    private boolean rotateTo(EntityLivingBase target, float range, float wallRange) {
//        if (target instanceof EntityPlayer player && player.fakePlayer != null && mc.scheduledTasks.size() <= 1) {
//            target = player.fakePlayer;
//        }
        Rotation targetRotation = RotationUtil.calculate(target, range, this.wallRangeValue.getValue().floatValue(), 1F, 1F);
        if (targetRotation == null) {
            targetRotation = RotationUtil.toRotation(
                    target.getPositionEyes(1F).add(0, -0.2, 0), 1F
            );
            this.updateRotation(targetRotation);
            return false;
        }
        float speed = (float) MathUtil.getRandomInRange(minRotationSpeedValue.getValue().floatValue(), maxRotationSpeedValue.getValue().floatValue());
        Rotation rotation = RotationUtil.turn(targetRotation, rotationModeValue.getMode(), speed);
        this.updateRotation(rotation);
        return true;
    }

    public void tryAttack(EntityLivingBase target, float range, float wallRange) {
        Blink blink = (Blink) Southside.moduleManager.getModuleByClass(Blink.class);

        if (blink.isSmartHit() && (target instanceof EntityPlayer player) && player.blinkHitCount >= blink.getMaxHitCount())
            return;

        boolean success = false;
        if (multiValue.getValue()) {
            List<RayTraceResult> results = RayCastUtil.rayCastList(RotationUtil.serverRotation, wallRange, 0F, mc.player, true, 0F, 1F);
            if (results.isEmpty()) {
                results.addAll(RayCastUtil.rayCastList(RotationUtil.serverRotation, range, 0F, mc.player, false, 0F, 1F));
            }
            for (RayTraceResult result : results) {
                Entity entity = result.entityHit;
                if (entity == null) continue;
                if (Target.isTarget(entity)) {
                    this.attack((EntityLivingBase) entity);
                    success = true;
                }
            }
//            if (!success) {
//                ChatUtil.info("miss");
//            }
            return;
        }

        RayTraceResult result1 = RayCastUtil.rayCast(RotationUtil.serverRotation, wallRange, 0F, mc.player, true, 0F, 1F);
        if (result1 != null && (result1.entityHit == target || (result1.typeOfHit == RayTraceResult.Type.ENTITY && Target.isTarget(result1.entityHit) && this.modeValue.getMode().equals("Switch")))) {
            this.attack((EntityLivingBase) result1.entityHit);
            return;
        }

        RayTraceResult result2 = RayCastUtil.rayCast(RotationUtil.serverRotation, range, 0F, mc.player, false, 0F, 1F);
        if (result2 != null && (result2.entityHit == target || (result2.typeOfHit == RayTraceResult.Type.ENTITY && Target.isTarget(result2.entityHit) && this.modeValue.getMode().equals("Switch")))) {
            this.attack((EntityLivingBase) result2.entityHit);
        }

    }

    public void updateRotation(Rotation rotation) {
        if (this.silentRotationValue.getValue()) {
            RotationUtil.setTargetRotation(rotation, 2);
        } else {
            rotation.apply();
        }
    }

    public void attack(EntityLivingBase target) {
        switch (attackModeValue.getMode()) {
            case "Delay" -> {
                if (!cpsTimer.hasReached(cpsDelay)) return;
            }
            case "Hurttime" -> {
                if (target.hurtTime > 6 && mc.player.fallDistance < 0.2D) return;
            }
            case "Tick" -> {
            }
        }

//        AttackEvent event = new AttackEvent(target);
//        Southside.eventBus.post(event);
//
//        if (event.isCancelled()) {
//            return;
//        }

        this.preAttack();

//        if (autoBlockValue.getValue().equals("Grim")) {
//            NoSlow.grimPre();
//        }

        mc.playerController.attackEntity(mc.player, target);

//        if (autoBlockValue.getValue().equals("Grim")) {
//            NoSlow.grimPost();
//        }

        this.postAttack();

        this.resetCPS();
    }

    public void resetCPS() {
        cpsDelay = (long) (1000 / Math.max(1, RandomUtils.nextDouble(minCpsValue.getValue().doubleValue(), maxCpsValue.getValue().doubleValue())));
        cpsTimer.reset();
    }

    public void preAttack() {
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.resetCooldown();
    }

    public void postAttack() {
    }

    public void postBlock() {
        if (!canBlock()) return;
        if (mc.getConnection() == null) return;
        switch (autoBlockValue.getMode()) {
            case "Fake" -> {
                this.blocking = true;
            }
            case "Grim" -> {
                this.blocking = true;
//                this.serverSideBlocking = true;
//                mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND, true));
//                mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND, true));
            }
        }
    }

    private boolean canBlock() {
        return mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && !autoBlockValue.equals("OFF") && !Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled();
    }

    private void stopBlock() {
        this.blocking = false;
//        if (blocking && !autoBlockValue.getValue().equals("Fake")) {
//            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));
//        }
//        if (!canBlock()) {
//            this.blocking = false;
//            if (this.serverSideBlocking) {
//                this.serverSideBlocking = false;
//                ChatUtil.info("stop block");
//                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));
//            }
//            return;
//        }
//        switch (autoBlockValue.getValue()) {
//            case "OFF", "Fake" -> {
//                this.blocking = false;
//            }
//            case "Grim" -> {
//                this.blocking = false;
//                if (this.serverSideBlocking) {
//                    this.serverSideBlocking = false;
//                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));
//                }
//            }
//        }
    }

    public static List<EntityLivingBase> getTargets(float range) {
        final double rangeSq = range * range;
        Blink blink = (Blink) Southside.moduleManager.getModuleByClass(Blink.class);
        return mc.world.loadedEntityList
                .stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(Entity::isEntityAlive)
                .map(entity -> (EntityLivingBase) entity)
                .filter(Target::isTarget)
                .filter(entityLivingBase -> !blink.isSmartHit() || !(entityLivingBase instanceof EntityPlayer) || ((EntityPlayer) entityLivingBase).blinkHitCount < blink.getMaxHitCount())
                .filter(entityLivingBase -> mc.player.getDistanceSq(entityLivingBase) <= rangeSq)
                .filter(entityLivingBase -> RotationUtil.calculate(entityLivingBase, range, INSTANCE.wallRangeValue.getValue().doubleValue(), 1F, 1F) != null)
                .sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity)))
                .collect(Collectors.toList());
    }

    public static List<EntityLivingBase> getTargets(float range, String mode) {
        final double rangeSq = range * range;
        Blink blink = (Blink) Southside.moduleManager.getModuleByClass(Blink.class);
        Stream<EntityLivingBase> targetsStream = mc.world.loadedEntityList
                .stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(Entity::isEntityAlive)
                .map(entity -> (EntityLivingBase) entity)
                .filter(Target::isTarget)
                .filter(entityLivingBase -> !blink.isSmartHit() || !(entityLivingBase instanceof EntityPlayer) || ((EntityPlayer) entityLivingBase).blinkHitCount < blink.getMaxHitCount())
                .filter(entityLivingBase -> mc.player.getDistanceSq(entityLivingBase) <= rangeSq)
                .filter(entityLivingBase -> RotationUtil.calculate(entityLivingBase, range, INSTANCE.wallRangeValue.getValue().doubleValue(), 1F, 1F) != null);

        if ("Health".equals(mode)) {
            targetsStream = targetsStream.sorted(Comparator.comparingDouble(EntityLivingBase::getHealth));
        } else if ("Distance".equals(mode)) {
            targetsStream = targetsStream.sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity)));
        }
        return targetsStream.collect(Collectors.toList());
    }

    private void close() {
        if (!this.autoDisableValue.getValue()) return;
        this.setEnable(false);
        Notification.addNotification("Auto disabled.", "Kill Aura", Notification.NotificationType.INFO);
    }

    @Override
    public String getSuffix() {
        return this.multiValue.getValue() ? "Multi" : this.modeValue.getMode();
    }

    public static boolean canSprint() {
        if (!INSTANCE.isEnabled() && INSTANCE.enableSprintTimer.hasReached(100)) return true;
        return INSTANCE.sprintValue.getValue() || KillAura.INSTANCE.target != null || ((INSTANCE.discoveredTargets == null || INSTANCE.discoveredTargets.isEmpty()) && RotationUtil.targetRotation == null);
    }

    public static boolean getBlocking() {
//        return INSTANCE.isEnabled() && mc.player.ticksExisted % 10 <= 5;
        return INSTANCE.isEnabled() && INSTANCE.blocking;
    }

    public static EntityLivingBase getTarget() {
        if (!INSTANCE.isEnabled()) return null;
        return INSTANCE.target;
    }

    public static List<EntityLivingBase> getTargets() {
        return INSTANCE.targets;
    }

    public static String getMode() {
        return INSTANCE.modeValue.getMode();
    }

    public static boolean isInstanceEnabled() {
        return INSTANCE.isEnabled();
    }
}
