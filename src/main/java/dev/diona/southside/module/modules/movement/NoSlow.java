package dev.diona.southside.module.modules.movement;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.util.network.PacketUtil;
import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.block.Block;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class NoSlow extends Module {
    private static NoSlow INSTANCE;

    public final Dropdown modeValue = new Dropdown("Mode", "Grim", "Vanilla", "Grim");
    public final Switch sprintValue = new Switch("Sprint", true);
    public final Switch swordValue = new Switch("Sword", true);
    public final Switch bowValue = new Switch("Bow", true);
    public final Switch consumeValue = new Switch("Consume", true);
    public final Switch runEatValue = new Switch("RunEat", true);
    private CPacketPlayerTryUseItem bufferPacket;

    private boolean canEat = true;
    public static boolean shouldSlow;
    public static boolean severSideBlocking = false;


    public NoSlow(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        severSideBlocking = false;
    }

    @EventListener
    public void onSlowdown(SlowDownEvent event) {
        if (checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), true, true, true)) {
            switch (modeValue.getMode()) {
                case "Grim" -> {
                    if (shouldSlow && checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), false, true, false))
                        return;
                    event.setForward(1.0F);
                    event.setStrafe(1.0F);
                }
                case "Vanilla" -> {
                    event.setForward(1.0F);
                    event.setStrafe(1.0F);
                }
                default -> {
                }
            }
        }
    }

    private static boolean checkItem(Item item, boolean sword, boolean consume, boolean bow) {
        return item instanceof ItemSword && INSTANCE.swordValue.getValue() && sword
                || item instanceof ItemBow && INSTANCE.bowValue.getValue() && bow
                || ((item instanceof ItemPotion && !(item instanceof ItemSplashPotion) && !(item instanceof ItemLingeringPotion)) || item instanceof ItemFood || item instanceof ItemBucketMilk) && INSTANCE.consumeValue.getValue() && consume;
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onPre(MotionEvent event) {
        if (event.getState() != EventState.PRE) return;
        boolean isMoving = mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);

        switch (modeValue.getMode()) {
            case "Grim": {
                grimPre();
                if (isMoving && mc.player.isHandActive() && checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), false, true, true)) {
                    if (runEatValue.getValue()) {

                    } else {
                        int count = mc.player.getItemInUseMaxCount();
                        if (count % 4 == 0) {
                            mc.player.connection.sendPacket(new CPacketClickWindow(0, 36, 0, ClickType.SWAP, new ItemStack(Block.getBlockById(166)), (short) 0));
                        }
                    }
                }
                break;
            }
        }
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    public void onPost(MotionEvent event) {
        if (event.getState() != EventState.POST) return;
        boolean isMoving = mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);

        switch (modeValue.getMode()) {
            case "Grim": {
//                if (!isMoving) return;
                grimPost();
                break;
            }
        }
    }

    public static void grimPre() {
        if (!INSTANCE.modeValue.getMode().equals("Grim")) return;
        if (severSideBlocking) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN, true));
            severSideBlocking = false;
        }
    }

    public static void grimPost() {
        if (!INSTANCE.isEnabled()) return;
        if (!INSTANCE.modeValue.getMode().equals("Grim")) return;
        if (checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), true, false, false)) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword && (mc.player.isHandActive() || KillAura.getBlocking())) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND, true));
                severSideBlocking = true;
            }
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
//        // 当最后一个食物吃完消失时，可以再次食用
//        if (mc.player.inventory.getCurrentItem() == ItemStack.EMPTY) {
//            canEat = true;
//        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        switch (modeValue.getMode()) {
            case "Grim" -> {
                Packet<?> packet = event.getPacket();
                if (PacketUtil.isCPacket(packet)) {
                    if (checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), true, true, true)) {
                        if (checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), false, true, false) && runEatValue.getValue()) {
                            if (packet instanceof CPacketPlayerTryUseItem) {
//                                if (canEat) {
                                shouldSlow = true;
//                                    canEat = false;
                                if (mc.player.inventory.getCurrentItem().getCount() > 1) mc.player.dropItem(false);
//                                } else {
//                                    event.setCancelled(true);
//                                }
                            }
//                            if (packet instanceof CPacketPlayerTryUseItemOnBlock block) {
//                                ChatUtil.info(block.getFacingX() + "");
//                            }

                            if (packet instanceof CPacketPlayerDigging digging) {
                                if (digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.player.inventory.getCurrentItem().getCount() > 1) {
                                    event.setCancelled(true);
                                }
                            }
                        } else {
                            if (packet instanceof CPacketPlayerTryUseItem || packet instanceof CPacketPlayerTryUseItemOnBlock) {
                                shouldSlow = true;
                            }
                            if (packet instanceof CPacketPlayerDigging digging && digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                                shouldSlow = true;
                            }
                        }
                        // 弓
                        if (checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), false, false, true)) {
                            if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                                mc.getConnection().sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                                mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
                                mc.getConnection().sendPacketNoEvent(new CPacketCustomPayload("test", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
                                mc.getConnection().sendPacketNoEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                                event.setCancelled(true);
                            }
                        }
                    }
                } else {
                    Block block = null;
//                    if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
//                        block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
                    if (runEatValue.getValue() && checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), false, true, false)) {
                        if (packet instanceof SPacketSetSlot slot) {
                            if (slot.getStack().getItem() == mc.player.inventory.getCurrentItem().getItem()) {
//                                mc.player.inventory.getCurrentItem().setCount(slot.getSlot());
                                if (slot.getWindowId() == 0 && slot.getSlot() >= 36 && slot.getSlot() < 45) {
                                    if (!slot.getStack().isEmpty()) {
                                        ItemStack itemstack1 = mc.player.inventoryContainer.getSlot(slot.getSlot()).getStack();

                                        if (itemstack1.isEmpty() || itemstack1.getCount() < slot.getStack().getCount()) {
                                            slot.getStack().setAnimationsToGo(5);
                                        }
                                    }
                                    mc.player.inventory.getCurrentItem().setCount(slot.getStack().getCount());
                                    event.setCancelled(true);
                                    shouldSlow = false;
                                }
                            }
                        }
//                        if (packet instanceof SPacketUpdateHealth) {
//                            canEat = true;
//                        }
                    } else {
                        if (checkItem(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem(), true, true, true)
                                && mc.player.isHandActive() /* && (block == null || block != Blocks.CHEST) */) {
                            if (packet instanceof SPacketWindowItems) {
                                event.setCancelled(true);
                                shouldSlow = false;
                            }
                            if (packet instanceof SPacketSetSlot) {
                                event.setCancelled(true);
                            }
                        }

                    }

                }

//                if (mc.player == null ||
//                        mc.player.getHeldItem(EnumHand.MAIN_HAND) == null ||
//                        (!(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFood && consumeValue.getValue()) &&
//                                !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemLingeringPotion && consumeValue.getValue()) &&
//                                !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow && bowValue.getValue())))
//                    return;
//                Packet<?> wrapper = event.getPacket();
//                if (wrapper instanceof CPacketPlayerTryUseItem) {
//                    process();
//                    bufferPacket = (CPacketPlayerTryUseItem) wrapper;
//                    event.setCancelled(true);
//                }
//                if (wrapper instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) wrapper).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
//                    process();
//                }
//                if (wrapper instanceof SPacketWindowItems) {
//                    if (bufferPacket != null) {
//                        mc.player.connection.sendPacketNoEvent(bufferPacket);
//                        bufferPacket = null;
//                    }
//                    event.setCancelled(true);
//                }
//                break;
            }
        }
    }

    private void process() {
        mc.player.connection.sendPacket(new CPacketClickWindow(0, 36, 0, ClickType.SWAP, new ItemStack(Block.getBlockById(166)), (short) 0));
    }

    public static boolean canSprint() {
        return INSTANCE.isEnabled() && INSTANCE.sprintValue.getValue();
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }
}
