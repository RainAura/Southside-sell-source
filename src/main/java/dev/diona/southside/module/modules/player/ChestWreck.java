package dev.diona.southside.module.modules.player;

import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.MoveInputEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.module.modules.misc.AntiTigerMachine;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.Objects;

public class ChestWreck extends Module {
    public ChestWreck(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    TimerUtil delay = new TimerUtil();
    TimerUtil switchDelay = new TimerUtil();
    private boolean shouldSneak = false;
    int lastSlot = -1;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.isHandActive() || !KillAura.getTargets().isEmpty()) {
            shouldSneak = false;
            return;
        }
        if (AntiTigerMachine.isTigerMachineWorking()) return;
        Stealer stealer = (Stealer) Southside.moduleManager.getModuleByClass(Stealer.class);
        if (mc.currentScreen == null && mc.player.openContainer.windowId == 0) {
            final var tile = mc.world.loadedTileEntityList.stream()
                    .filter(container -> container instanceof TileEntityChest)
                    .filter(entity -> stealer.selfStolen.contains(entity.getPos()))
                    .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 3F).min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity.getPos())));
            if (tile.isPresent() && delay.hasReached(500)) {
                final var container = tile.get();
                final BlockPos chestPos = container.getPos();
                if (mc.world.getBlockState(chestPos.up()).getBlock() == Blocks.AIR) {
                    if (!(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBlock block && !Scaffold.invalidBlocks.contains(block.getBlock()))) {
                        int slot = getBlockSlot();
                        if (slot != -1) {
                            lastSlot = mc.player.inventory.currentItem;
                            mc.player.inventory.currentItem = slot;
                            switchDelay.reset();
                        }
                    }
//                    if (!shouldSneak) {
//                        this.shouldSneak = true;
//                        return;
//                    }
                    if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) return;
                    Rotation rotation = RotationUtil.getRotationBlock(chestPos, 0);
                    RotationUtil.setTargetRotation(rotation.onPost(() -> {
                        if (!mc.player.serverSneakState) {
                            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        }
                        boolean prev = mc.player.isSneaking();
                        mc.player.setSneaking(true);
//                        CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(chestPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0);
//                        mc.getConnection().sendPacket(packet);
                        mc.playerController.processRightClickBlock(
                                mc.player,
                                mc.world,
                                chestPos,
                                EnumFacing.UP,
                                Scaffold.getVec3(chestPos, EnumFacing.UP),
                                EnumHand.MAIN_HAND
                        );
                        mc.player.setSneaking(prev);
                        if (!mc.player.serverSneakState) {
                            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                        }
                    }), 0);
//                    CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(chestPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0);
//                    Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
                    delay.reset();
                }
            } else {
                this.shouldSneak = false;
            }
        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() == EventState.PRE) {

        } else {
            if (lastSlot != -1 && switchDelay.hasReached(150)) {
                mc.player.inventory.currentItem = lastSlot;
                lastSlot = -1;
            }
        }
    }

//    @EventListener
//    public void onMoveInput(MoveInputEvent event) {
//        if (this.shouldSneak) {
//            event.setSneak(true);
//        } else if (event.isSneak()) {
//            event.setSneak(false);
//        }
//    }

    int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return -1;
    }
}
