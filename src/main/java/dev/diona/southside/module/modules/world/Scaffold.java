package dev.diona.southside.module.modules.world;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.player.*;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {
    public static Scaffold INSTANCE;
    public final Switch fullSprint = new Switch("Full Sprint", false);
    public final Switch keepFov = new Switch("Keep fov", false);
    public final Switch switchBack = new Switch("Switch Back", true);
    public final Slider fovValue = new Slider("Fov", 1.2, 0.8, 1.5, 0.05);
    public final Switch bw = new Switch("Bed Wars", false);

    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.ENCHANTING_TABLE, Blocks.CHEST, Blocks.ENDER_CHEST,
            Blocks.TRAPPED_CHEST, Blocks.ANVIL, Blocks.SAND, Blocks.WEB, Blocks.TORCH,
            Blocks.CRAFTING_TABLE, Blocks.FURNACE, Blocks.WATERLILY, Blocks.DISPENSER,
            Blocks.STONE_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.NOTEBLOCK,
            Blocks.DROPPER, Blocks.TNT, Blocks.STANDING_BANNER, Blocks.WALL_BANNER, Blocks.REDSTONE_TORCH, Blocks.CRAFTING_TABLE);

    public int baseY = -1;
    private int slot;
    private boolean canPlace;
    public int bigVelocityTick = 0;

    public Scaffold(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public boolean onEnable() {
        if (mc.player == null) return true;
        blockPos = null;
        this.slot = mc.player.inventory.currentItem;
        baseY = -1;
        canPlace = true;
        bigVelocityTick = 0;
        return true;
    }

    @Override
    public boolean onDisable() {
        if (mc.player == null) return true;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        mc.player.inventory.currentItem = slot;
        return true;
    }

    @EventListener
    public void onRender2D(NewRender2DEvent event) {
        ScaledResolution sr = event.getScaledResolution();
        int count = getBlockCount();
        String text = String.format(TextFormatting.WHITE + "Blocks: %s", (count > 64 ? TextFormatting.GREEN : count > 0 ? TextFormatting.YELLOW : TextFormatting.RED) + String.valueOf(count));
        mc.fontRenderer.drawStringWithShadow(text, (float) sr.getScaledWidth() / 2 - (float) mc.fontRenderer.getStringWidth(text) / 2, (float) sr.getScaledHeight() / 2 - 30, -1);
    }

    @EventListener
    public void onMoveInput(MoveInputEvent event) {
        if (mc.player.onGround && event.getMoveForward() > 0 && !mc.gameSettings.keyBindJump.isKeyDown()) {
            event.setJump(true);
        }
    }

    private void place(boolean rotate) {
        if (!canPlace) {
            return;
        }
        if (!InventoryUtil.switchBlock()) return;

        if (blockPos != null) {
            if (mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, getVec3(blockPos, enumFacing), EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            blockPos = null;
            if (rotate) {
                RotationUtil.setTargetRotation(new Rotation(mc.player.rotationYaw, mc.player.rotationPitch), 0);
            }
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketEntityVelocity velocity && velocity.getEntityID() == mc.player.getEntityId()) {
            double strength = new Vec3d(velocity.getMotionX() / 8000D, 0, velocity.getMotionZ() / 8000D).length();
            if (strength >= 1.5D) {
                ChatUtil.info("你也是要飞了: " + strength);
                bigVelocityTick = 60;
            }
        }
    }

    private int rotateCount = 0;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        BlockPos playerPos = new BlockPos(mc.player);
        IBlockState state = mc.world.getBlockState(playerPos);
        if (state.getBlock() != Blocks.AIR && state.getBlock().isPassable(mc.world, playerPos)) return; // 开法阵了
        if (mc.player.ticksExisted <= 5) return;
        if (bigVelocityTick > 0) {
            bigVelocityTick--;
        }
        if (mc.player.onGround && bigVelocityTick <= 30) {
            bigVelocityTick = 0;
        }
        double motion = Math.max(mc.player.motionX, mc.player.motionZ);
        if (!fullSprint.getValue()) {
            place(true);
        }
//        if (!mc.gameSettings.keyBindJump.isKeyDown() && !bw.getValue()) {
            if (!fullSprint.getValue() && motion <= 0.4) {
                if (Math.abs(mc.player.motionX) < 0.03 || Math.abs(mc.player.motionZ) < 0.03) {
                    if (!mc.player.onGround && mc.player.offGroundTicks <= 2) return;
                } else {
                    if (!mc.player.onGround && mc.player.offGroundTicks <= 1) return;
                }
            }
//        }
        if (baseY == -1 || baseY > (int) mc.player.posY - 1 || bigVelocityTick > 0 || mc.player.onGround || mc.gameSettings.keyBindJump.isKeyDown()) {
            baseY = (int) mc.player.posY - 1;
        }

        this.findBlock();
        if (!InventoryUtil.switchBlock()) return;
        canPlace = !mc.gameSettings.keyBindJump.isKeyDown() || mc.player.offGroundTicks >= 2;
        if (mc.gameSettings.keyBindJump.isKeyDown() && !canPlace) {
            return;
        }
        if (blockPos != null) {
            boolean reachable = true;
            if (mc.player.motionY < -0.1) {
                FallingPlayer fallingPlayer = new FallingPlayer(mc.player);
                fallingPlayer.calculate(2);
                if (blockPos.getY() > fallingPlayer.getY()) {
                    reachable = false;
                }
            }
            if ((!reachable || bigVelocityTick > 0 || fullSprint.getValue()) && rotateCount <= 8) {
                Rotation rotation = RotationUtil.getRotationBlock(blockPos, 0F);
//                ChatUtil.info("working");
                mc.playerStuckTicks++;
                rotateCount++;
                mc.getConnection().sendPacket(new CPacketPlayer.Rotation(
                        rotation.yaw, rotation.pitch, mc.player.onGround
                ));
                place(false);
                this.onUpdate(event);
            } else {
                Rotation rotation = RotationUtil.getRotationBlock(blockPos, 1F);
                rotateCount = 0;
                RotationUtil.setTargetRotation(rotation, 0);
            }
        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() == EventState.POST && switchBack.getValue()) {
            if (mc.player.inventory.currentItem != slot) {
                mc.player.connection.sendPacket(new CPacketKeepAlive(0));
                mc.player.inventory.currentItem = slot;
            }
        }
    }

    private void findBlock() {
        Vec3d baseVec = mc.player.getPositionEyes(2F);
//        BlockPos base = new BlockPos(baseVec.x, baseY + 0.1f, baseVec.z);
        BlockPos base = new BlockPos(baseVec.x, baseY + 0.1f, baseVec.z);
        int baseX = base.getX();
        int baseZ = base.getZ();
        if (mc.world.getBlockState(base).isTopSolid()) return;
        if (checkBlock(baseVec, base)) {
            return;
        }
        for (int d = 1; d <= 6; d++) {
            if (checkBlock(baseVec, new BlockPos(
                    baseX,
                    baseY - d,
                    baseZ
            ))) {
                return;
            }
            for (int x = 1; x <= d; x++) {
                for (int z = 0; z <= d - x; z++) {
                    int y = d - x - z;
                    for (int rev1 = 0; rev1 <= 1; rev1++) {
                        for (int rev2 = 0; rev2 <= 1; rev2++) {
                            if (checkBlock(baseVec, new BlockPos(
                                    baseX + (rev1 == 0 ? x : -x),
                                    baseY - y,
                                    baseZ + (rev2 == 0 ? z : -z)
                            ))) return;
                        }
                    }
                }
            }
        }
    }

    private BlockPos blockPos;
    private EnumFacing enumFacing;

    private boolean checkBlock(Vec3d baseVec, BlockPos pos) {
        if (!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) return false;
        Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        for (EnumFacing 脸 : EnumFacing.values()) {
            Vec3d hit = center.add(new Vec3d(脸.getDirectionVec()).scale(0.5));
            Vec3i baseBlock = pos.add(脸.getDirectionVec());
            if (!mc.world.getBlockState(new BlockPos(baseBlock.getX(), baseBlock.getY(), baseBlock.getZ())).isBlockNormalCube())
                continue;
            Vec3d relevant = hit.subtract(baseVec);
            if (relevant.lengthSquared() <= 4.5 * 4.5 && relevant.dotProduct(
                    new Vec3d(脸.getDirectionVec())
            ) >= 0) {
                blockPos = new BlockPos(baseBlock);
                enumFacing = 脸.getOpposite();
                return true;
            }
        }
        return false;
    }

    public static Vec3d getVec3(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtil.getRandomInRange(0.3, -0.3);
            z += MathUtil.getRandomInRange(0.3, -0.3);
        } else {
            y += MathUtil.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtil.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtil.getRandomInRange(0.3, -0.3);
        }
        return new Vec3d(x, y, z);
    }

    private boolean isValid(final Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) (item)).getBlock());
    }

    public void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()
                    && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) is.getItem();
                    if (isValid(block)) {
                        if (36 + switchSlot != i) {
                            InventoryUtil.swap(i, switchSlot);
                        }
                        break;
                    }
                }
            }
        }
    }

    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) is.getItem();
                    if (isValid(block)) {
                        blockCount += is.getCount();
                    }
                }
            }
        }
        return blockCount;
    }
}
