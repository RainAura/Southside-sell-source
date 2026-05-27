//package dev.diona.southside.module.modules.player;
//
//import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
//import cc.polyfrost.oneconfig.config.options.impl.Switch;
//import dev.diona.southside.event.EventState;
//import dev.diona.southside.event.events.*;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.Module;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import dev.diona.southside.util.misc.MathUtil;
//import dev.diona.southside.util.misc.TimerUtil;
//import dev.diona.southside.util.player.*;
//import dev.diona.southside.util.render.RenderUtil;
//import me.bush.eventbus.annotation.EventListener;
//import net.minecraft.block.BlockAir;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.play.client.CPacketEntityAction;
//import net.minecraft.util.EnumActionResult;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.RayTraceResult;
//import net.minecraft.util.math.Vec3d;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class Scaffold extends Module {
//    private static Scaffold INSTANCE;
//    public final Dropdown modeValue = new Dropdown("Mode", "Telly", "Telly", "Legit", "Normal");
//    public final Slider placeDelayValue = new Slider("Place Delay", 20, 0, 100, 1);
//    public final Switch sprintValue = new Switch("Sprint", false);
//    public final Switch upValue = new Switch("Up", false);
//    public final Switch eagle = new Switch("Eagle", true);
//    public final Dropdown eagleMode = new Dropdown("EagleMode", "Normal", "Silent", "Normal");
//    HashMap<BlockPos, FadePos> positions = new HashMap<>();
//    public Scaffold(String name, String description, Category category, boolean visible) {
//        super(name, description, category, visible);
//        eagleMode.setDisplay(eagle::getValue);
//        sprintValue.setDisplay(() -> modeValue.getValue().equals("Telly"));
//        INSTANCE = this;
//    }
//    private boolean thisTickPlaced = false;
//    private Vec3d findBlockPoint = null;
//    private EnumFacing findBlockFacing = null;
//    private int legitStartY = -1;
//
//    private final TimerUtil delayTimer = new TimerUtil();
//    private BlockPos findBlockPos = null;
//
//    private int startSlot = 0;
//
//    private boolean silentSneakState;
//
//    @Override
//    public boolean onEnable() {
//        if (mc.player == null) return true;
//        startSlot = mc.player.inventory.currentItem;
//        findBlockPoint = null;
//        findBlockPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
//        findBlockFacing = null;
//        return true;
//    }
//
//    @Override
//    public boolean onDisable() {
//        if (mc.player == null) return true;
//        mc.player.inventory.currentItem = startSlot;
//        if (eagle.getValue() && eagleMode.isMode("Silent") && silentSneakState) {
//            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
//        }
//        mc.gameSettings.keyBindSneak.setPressed(false);
//        return true;
//    }
//
//    @EventListener
//    public final void onMoveInputEvent(final MoveInputEvent event) {
//        if (eagle.getValue() && eagleMode.isMode("Silent") && silentSneakState) {
//            final var multiplier = event.getSneakSlowDownMultiplier();
//            event.setMoveStrafe(event.getMoveStrafe() * multiplier);
//            event.setMoveForward(event.getMoveForward() * multiplier);
//        }
//    }
//
//    private RayTraceResult highIQRayCast(Rotation rotation) {
//        RayTraceResult result = null;
//        float l = 1F, r = 1F;
//        if (modeValue.getValue().equals("Telly")) {
//            l = 1F; r = 3F;
//        }
//        if (modeValue.getValue().equals("Normal")) {
//            l = 1F; r = 2F;
//        }
//        for (float predict = l; predict <= r; predict += 0.2F) {
//            result = RayCastUtil.rayCast(rotation, 4.5, 0, mc.player, false, 0, predict);
//            if (checkResult(result)) break;
//        }
//        return result;
//    }
//
//    @EventListener
//    public void onMoveInput(MoveInputEvent event) {
//        if (this.modeValue.getValue().equals("Telly")) {
//            if (mc.player.onGround && event.getMoveForward() > 0) {
//                event.setJump(true);
//            }
////            if (mc.player.offGroundTicks >= 6 && !(upValue.getValue() || mc.gameSettings.keyBindJump.isKeyDown())) {
////                event.setMoveForward(0);
////                event.setMoveStrafe(0);
////            }
//            if (mc.player.offGroundTicks >= 4) {
//                event.setMoveForward(0);
//                event.setMoveStrafe(0);
//            }
//        }
//    }
//
//    @EventListener
//    public void onTick(TickEvent event) {
//        thisTickPlaced = false;
//    }
//
//    @EventListener
//    public void onInput(InputTickEvent event) {
//        if (mc.player == null) return;
//        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
//        boolean onEdge = mc.world.getBlockState(pos).getBlock() instanceof BlockAir;
//        if (modeValue.getValue().equals("Legit") || modeValue.getValue().equals("Telly") || modeValue.getValue().equals("Normal")) {
//            if (mc.player.onGround) {
//                legitStartY = (int) mc.player.posY;
//            }
//            if (!onEdge) return;
//            if (!InventoryUtil.switchBlock()) return;
//            if (thisTickPlaced && placeDelayValue.getValue().intValue() > 0) return;
////            RayTraceResult result = this.highIQRayCast(RotationUtil.serverRotation);
//            RayTraceResult result = null;
//            for (float predict = 1; predict <= 3; predict += 0.1F) {
//                result = RayCastUtil.rayCast(RotationUtil.serverRotation, 4.5, 0, mc.player, false, 0, predict);
//                if (checkResult(result)) break;
//            }
//            if (checkResult(result)) {
//                BlockPos blockPos = result.getBlockPos().add(result.sideHit.getDirectionVec());
//                if (delayTimer.hasReached(placeDelayValue.getValue())) {
//                    if (mc.playerController.processRightClickBlock(
//                            mc.player,
//                            mc.world,
//                            result.getBlockPos(),
//                            result.sideHit,
//                            result.hitVec,
//                            EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS
//                    ) {
//                        thisTickPlaced = true;
//                        mc.player.swingArm(EnumHand.MAIN_HAND);
//                        new FadePos(blockPos, new Color(0x385EDC5E, true), true);
//                        delayTimer.reset();
//                    }
//                }
//            } else {
//                delayTimer.reset();
//            }
//        }
//    }
//
//    private boolean checkResult(RayTraceResult result) {
//        if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK || !isGoodBlockPos(result.getBlockPos().add(result.sideHit.getDirectionVec()))) return false;
//        EnumFacing side = result.sideHit;
//        BlockPos pos = result.getBlockPos();
////        double minEyeX = , minEyeZ = mc.player.posZ;
////        double maxEyeX = , maxEyeZ = minEyeZ + mc.player.motionZ;
//        double minEyeX = Math.min(mc.player.posX - mc.player.motionX, mc.player.posX + mc.player.motionX);
//        double maxEyeX = Math.max(mc.player.posX - mc.player.motionX, mc.player.posX + mc.player.motionX);
//        double minEyeZ = Math.min(mc.player.posZ - mc.player.motionZ, mc.player.posZ + mc.player.motionZ);
//        double maxEyeZ = Math.max(mc.player.posZ - mc.player.motionZ, mc.player.posZ + mc.player.motionZ);
//        AxisAlignedBB aabb = new AxisAlignedBB(pos);
//        return switch (side) {
//            case NORTH -> // Z- face
//                    minEyeZ <= aabb.minZ - 0.2;
//            case SOUTH -> // Z+ face
//                    maxEyeZ >= aabb.maxZ + 0.2;
//            case EAST -> // X+ face
//                    maxEyeX >= aabb.maxX + 0.2;
//            case WEST -> // X- face
//                    minEyeX <= aabb.minX - 0.2;
//            default -> true;
//        };
//    }
//
//    @EventListener
//    public void onUpdate(UpdateEvent event) {
//        if (mc.player == null) return;
//
//        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
//        double dis = Math.max(Math.abs(mc.player.posX - Math.floor(mc.player.posX)), Math.abs(mc.player.posZ - Math.floor(mc.player.posZ)));
//        boolean onEdge = mc.world.getBlockState(pos).getBlock() instanceof BlockAir;
//        final var flag1 = onEdge && mc.player.onGround && (
//                dis <= 1
//                );
//
//        if (eagle.getValue()) {
//            switch (eagleMode.getValue()) {
//                case "Silent" -> {
//                    if (flag1 != this.silentSneakState)
//                    {
//                        if (flag1) {
//                            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
//                        } else {
//                            if (mc.gameSettings.keyBindSneak.isPressed()) mc.gameSettings.keyBindSneak.setPressed(false);
//                            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
//                        }
//                        this.silentSneakState = flag1;
//                    }
//                }
//                case "Normal" -> mc.gameSettings.keyBindSneak.setPressed(flag1);
//            }
//        }
//
//        switch (modeValue.getValue()) {
//            case "Legit", "Telly", "Normal" -> {
//                if (modeValue.getValue().equals("Telly") && mc.player.onGround && sprintValue.getValue()) {
//                    return;
//                }
//
//                Rotation rotation = new Rotation(mc.player.rotationYaw + 180, 90);
//                boolean found = false;
//
//                if (upValue.getValue()) {
//                    RayTraceResult vertical = RayCastUtil.rayCast(rotation, 4.5, 0, mc.player, false, 0, 1F);
//                    if (vertical != null && vertical.typeOfHit == RayTraceResult.Type.BLOCK) {
//                        BlockPos blockPos = vertical.getBlockPos().add(vertical.sideHit.getDirectionVec());
//                        if (isGoodBlockPos(blockPos)) {
//                            found = true;
//                        }
//                    }
//                }
//
//                if (!found) for (float pitch = 50; pitch <= 89; pitch += 0.1F) {
//                    rotation.pitch = pitch;
//                    RayTraceResult result = this.highIQRayCast(rotation);
//                    if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
//                        BlockPos blockPos = result.getBlockPos().add(result.sideHit.getDirectionVec());
//                        if (isGoodBlockPos(blockPos)) {
//                            found = true;
//                            break;
//                        }
//                    }
//                }
//
//                if (!found) {
//                    rotation.pitch = 85;
//                }
//
//                boolean sym = MathUtil.getRandomInRange(0, 1) > 0;
//                rotation.yaw += (sym ? 1 : -1) * MathUtil.getRandomFloat(0.1F, 0.01F) * 1F;
//
//                RotationUtil.setTargetRotation(rotation, 0);
//            }
//        }
//    }
//
//    private boolean isGoodBlockPos(BlockPos pos) {
//        if (findBlockPos != null && pos.distanceSqToCenter(
//                mc.player.posX,
//                mc.player.posY,
//                mc.player.posZ
//        ) > findBlockPos.distanceSqToCenter(
//                mc.player.posX,
//                mc.player.posY,
//                mc.player.posZ
//        )) return false;
//        if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) return false;
//        if (!(upValue.getValue() && !MovementUtil.isMoving()) && !(modeValue.getValue().equals("Telly") && mc.gameSettings.keyBindJump.isKeyDown())) {
//            return pos.getY() == legitStartY - 1;
//        }
//        else return pos.getY() < (int) mc.player.posY;
//    }
//
//    @EventListener
//    public void onMotion(MotionEvent event) {
//        if (mc.player == null) return;
//        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
//        boolean onEdge = mc.world.getBlockState(pos).getBlock() instanceof BlockAir;
//        switch (modeValue.getValue()) {
//            case "Telly" -> {
//                if (event.getState() != EventState.POST) return;
//                if (!onEdge) return;
//                if (!InventoryUtil.switchBlock()) return;
//                if (thisTickPlaced && placeDelayValue.getValue().intValue() > 0) return;
////                if (mc.player.offGroundTicks <= 6) return;
////                RayTraceResult result = this.highIQRayCast(RotationUtil.serverRotation);
//
////                if (true) return; // 死妈了
//                RayTraceResult result = null;
//                for (float predict = 1; predict <= 1.2; predict += 0.1F) {
//                    result = RayCastUtil.rayCast(RotationUtil.serverRotation, 4.5, 0, mc.player, false, 0, predict);
//                    if (checkResult(result)) break;
//                }
//                if (checkResult(result)) {
//                    BlockPos blockPos = result.getBlockPos().add(result.sideHit.getDirectionVec());
////                    if (mc.world.getBlockState(result.getBlockPos()).getBlock() instanceof BlockAir) {
////                        ChatUtil.info("???");
////                    }
//                    if (delayTimer.hasReached(placeDelayValue.getValue())) {
//                        if (mc.playerController.processRightClickBlock(
//                                mc.player,
//                                mc.world,
//                                result.getBlockPos(),
//                                result.sideHit,
//                                result.hitVec,
//                                EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS
//                        ) {
//                            thisTickPlaced = true;
//                            findBlockPos = blockPos;
//                            mc.player.swingArm(EnumHand.MAIN_HAND);
//                            new FadePos(blockPos, new Color(Color.RED.getRGB(), true), true);
//                            delayTimer.reset();
//                        }
//                    }
//                } else {
//                    delayTimer.reset();
//                }
//            }
//            case "Legit" -> {
//            }
//        }
//    }
//
//    @EventListener
//    public void onRender3D(Render3DEvent event) {
//        if (findBlockPoint != null) {
//            RenderUtil.drawTracerLine(findBlockPoint.x, findBlockPoint.y, findBlockPoint.z, Color.WHITE);
//        }
//        if (findBlockPos != null) {
//            RenderUtil.boundingESPBoxFilled(new AxisAlignedBB(findBlockPos), Color.BLACK);
//        }
//        ArrayList<BlockPos> remove = new ArrayList<>();
//        int maxTime = (int) (0.5 * 1000);
//        for (FadePos pos : positions.values()) {
//            int current = (int) pos.fadeTimer.passed();
//            if (current > maxTime) {
//                remove.add(pos.pos);
//                continue;
//            }
//            Color fill;
//            if (pos.isFading()) {
//                double percent = Math.min((double) (maxTime - current) / maxTime,1);
//                fill = new Color(pos.fill.getRed(), pos.fill.getGreen(), pos.fill.getBlue(), (int) (pos.fill.getAlpha() * percent));
//            } else {
//                fill = pos.fill;
//                pos.fadeTimer.reset();
//            }
//            RenderUtil.boundingESPBoxFilled(new AxisAlignedBB(pos.pos), fill);
//            //EspUtil.drawOutline(pos.pos, fill.brighter());
//        }
//        for (BlockPos r : remove) {
//            positions.remove(r);
//        }
//    }
//
//    private Vec3d findBlock(int range, int yOffset) {
//        double playerX = mc.player.posX, playerZ = mc.player.posZ;
//        Vec3d best = null;
//        findBlockFacing = null;
//        Vec3d eye = mc.player.getPositionEyes(1F);
//        if (upValue.getValue()) {
//            for (int y = 0; y >= -range; y--) {
//                BlockPos pos = new BlockPos(playerX, yOffset + y, playerZ);
//                if (mc.world.getBlockState(pos).getBlock() instanceof BlockAir) {
//                    BlockPos nextPos = pos.add(0, -1, 0);
//                    if (mc.world.getBlockState(nextPos).isFullBlock()) {
//                        Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//                        findBlockPos = nextPos;
//                        findBlockPoint = center;
//                        if (checkVisible(center)) {
//                            findBlockFacing = EnumFacing.UP;
//                            return center;
//                        }
//                    }
//                }
//            }
//        }
//        for (int x = -range; x <= range; x++) {
//            for (int z = -range; z <= range; z++) {
//                BlockPos pos = new BlockPos(playerX + x, yOffset, playerZ + z);
//                if (mc.world.getBlockState(pos).getBlock() instanceof BlockAir) {
//                    for (int i = 0; i < 4; i++) {
//                        int dx = 0, dz = 0;
//                        switch (i) {
//                            case 0 -> dx = 1;
//                            case 1 -> dx = -1;
//                            case 2 -> dz = 1;
//                            case 3 -> dz = -1;
//                        }
//                        BlockPos nextPos = new BlockPos(playerX + x + dx, yOffset, playerZ + z + dz);
//                        if (mc.world.getBlockState(nextPos).isFullBlock()) {
//
//                            Vec3d center = new Vec3d(pos.getX() + 0.5 + dx / 2D, pos.getY() + 0.5, pos.getZ() + 0.5 + dz / 2D);
//                            if (checkVisible(center) && (best == null || best.squareDistanceTo(eye) > center.squareDistanceTo(eye))) {
//                                findBlockPos = nextPos;
//                                findBlockPoint = center;
//                                if (dz > 0) {
//                                    findBlockFacing = EnumFacing.NORTH;
//                                } else if (dz < 0) {
//                                    findBlockFacing = EnumFacing.SOUTH;
//                                } else if (dx > 0) {
//                                    findBlockFacing = EnumFacing.WEST;
//                                } else {
//                                    findBlockFacing = EnumFacing.EAST;
//                                }
//                                assert findBlockFacing != null;
//                                best = center;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (best == null || best.squareDistanceTo(mc.player.getPositionEyes(1F)) > mc.playerController.getBlockReachDistance() * mc.playerController.getBlockReachDistance()) {
//            return null;
//        }
////        return best.add(0, 0.1, 0);
//        return best;
//    }
//
//    private boolean checkVisible(Vec3d pos) {
//        Vec3d eye = new Vec3d(
//                mc.player.posX,
//                mc.player.posY + (double) mc.player.getEyeHeight(),
//                mc.player.posZ
//        );
//        if (eye.y < pos.y) return false;
//        return mc.world.rayTraceBlocks(
//                eye
//                , pos, false, true, false) == null;
//    }
//
//    private Vec3d[] getFaceMidpoints(Vec3d position) {
//        double x = position.x;
//        double y = position.y;
//        double z = position.z;
//
//        Vec3d[] faceMidpoints = {
//                new Vec3d(+ 0.5, 0, 0),
//                new Vec3d(- 0.5, 0, 0),
//                new Vec3d(0, 0, + 0.5),
//                new Vec3d(0, 0, - 0.5)
//        };
//
//        return faceMidpoints;
//    }
//
//
//
//    @Override
//    public String getSuffix() {
//        return modeValue.getValue();
//    }
//
//    public static boolean canSprint() {
//        if (!INSTANCE.isEnabled()) return true;
//        return INSTANCE.sprintValue.getValue();
//    }
//
//    public class FadePos {
//        public BlockPos pos;
//        public Color fill;
//
//        public TimerUtil fadeTimer;
//        boolean fading;
//
//        public FadePos(BlockPos pos, Color fill) {
//            this(pos,fill,0,true);
//        }
//        public FadePos(BlockPos pos, Color fill,boolean fading) {
//            this(pos, fill, 0, fading);
//        }
//        public FadePos(BlockPos pos, Color fill, int fadeDelay,boolean fading) {
//            this.fading = fading;
//            this.pos = pos;
//            this.fill = fill;
//            fadeTimer = new TimerUtil();
//            fadeTimer.delay(fadeDelay);
//            positions.put(pos, this);
//        }
//
//        public void startFade() {
//            fading = true;
//        }
//
//        public boolean isFading() {
//            return fading;
//        }
//    }
//}