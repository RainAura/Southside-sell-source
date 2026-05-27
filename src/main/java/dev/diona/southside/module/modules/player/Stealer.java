package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.misc.AntiTigerMachine;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.*;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

public class Stealer extends Module {
    private static Stealer INSTANCE;
    public final Dropdown modeValue = new Dropdown("Mode", "Simple", "Simple", "Auto Close", "Silent");

    public final Slider firstDelay = new Slider("First Delay", 150, 0, 1000, 10);
    public final Slider closeDelayValue = new Slider("Close Delay", 500, 0, 500, 1);
    public final Slider minDelayValue = new Slider("Min Delay", 0D, 0D, 300D, 1D);
    public final Slider maxDelayValue = new Slider("Max Delay", 0D, 0D, 300D, 1D);
    public final Switch aura = new Switch("Aura", false);
    public final Slider auraRange = new Slider("AuraRange", 4.5d, 0.0d, 6.0d, 0.1d);
    public final Slider auraDelay = new Slider("AuraDelay", 100D, 0D, 1000D, 10D);
    public final Slider waitOpenDelayValue = new Slider("Wait Open Delay", 2000D, 100D, 5000D, 1D);
    public final Switch noMoveValue = new Switch("No Move", true);
    public final Switch titleValue = new Switch("Title Check", true);
    public final Switch furnaceValue = new Switch("Furnace", false);
    public final Switch brewingStand = new Switch("BrewingStand", true);
    public final Switch exploitValue = new Switch("Exploit", true);
    public final Switch usefulValue = new Switch("Only Useful Item", true);
    private final TimerUtil timer = new TimerUtil();
    private final List<Item> items = new ArrayList<>();
    public static BlockPos currentChest = null;
    public static int count = 0;
    public static BezierUtil progress = new BezierUtil(4, 0);
    private BlockPos lastC08 = null;
    private long delay = 0;
    private TimerUtil firstDelayTimer = new TimerUtil();
    private final TimerUtil auraTimer = new TimerUtil();
    private final TimerUtil waitOpenTimer = new TimerUtil();
    private final TimerUtil closeDelayTimer = new TimerUtil();


    public final HashSet<BlockPos> stolen = new HashSet<>();
    public final HashSet<BlockPos> selfTried = new HashSet<>();
    public final HashSet<BlockPos> selfStolen = new HashSet<>();


    public Stealer(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(this.closeDelayValue.getLabel(), () -> !this.modeValue.getMode().equals("Simple"));

        this.addRangedValueRestrict(minDelayValue, maxDelayValue);
    }

    @EventListener
    public final void onWorldLoadEvent(final WorldEvent event) {
        stolen.clear();
        selfTried.clear();
        selfStolen.clear();
        currentChest = null;
    }

    @EventListener
    public final void onTickEvent(final TickEvent event) {
//        if (AntiTigerMachine.isTigerMachineWorking()) return;
//        InvManager invManager = (InvManager) Southside.moduleManager.getModuleByClass(InvManager.class);
//        if (!invManager.timer.hasReached(auraDelay.getValue().doubleValue())) return;
//        if (aura.getValue() && !Southside.moduleManager.getModuleByClass(Blink.class).isEnabled()) {
//            if (mc.player.openContainer.windowId != 0) {
//                stealTimer.reset();
//                return;
//            }
//            if (/*Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled() || */Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled() || Southside.moduleManager.getModuleByClass(Blink.class).isEnabled())
//                return;
//            final var tile = mc.world.loadedTileEntityList.stream()
//                    .filter(container -> container instanceof TileEntityChest /*|| container instanceof TileEntityFurnace || container instanceof TileEntityBrewingStand*/)
//                    .filter(entity -> !stolen.contains(entity.getPos()))
//                    .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= auraRange.getValue().doubleValue()).min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity.getPos())));
//            if (tile.isPresent() && stealTimer.hasReached(auraDelay.getValue().longValue()) && (count != -2 || waitOpenTimer.hasReached(waitOpenDelayValue.getValue().longValue()))) {
//                final var container = tile.get();
//
////                ChatUtil.info("我开了啊 " + container.getClass().getSimpleName());
//                if (mc.currentScreen == null) {
//                    CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(container.getPos(), getFacingDirection(container.getPos()), EnumHand.MAIN_HAND, 0, 0, 0);
////                    packet.placeDisabler = true;
//                    Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
//                    if (container instanceof TileEntityFurnace || container instanceof TileEntityBrewingStand) {
//                        stolen.add(container.getPos());
//                    } else {
//                        waitOpenTimer.reset();
//                    }
//                    stealTimer.reset();
//                }
//            }
//        }
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public final void onUpdate(final UpdateEvent event) {
        if (AntiTigerMachine.isTigerMachineWorking()) return;
        if (mc.player.isHandActive()) return;

        if (mc.player.isCreative() || mc.player.isSpectator()) return;

        if (MovementUtil.isMoving() && noMoveValue.getValue()) return;

        if (mc.player.openContainer.windowId == 0) { // 没有打开任何外部容器
            firstDelayTimer.reset();
            timer.reset();
            /*
             * Chest Aura
             */
            if (aura.getValue() && mc.currentScreen == null && !Southside.moduleManager.getModuleByClass(Blink.class).isEnabled()) {
                final var tile = mc.world.loadedTileEntityList.stream()
                        .filter(container -> container instanceof TileEntityChest || (container instanceof TileEntityFurnace && this.furnaceValue.getValue()) || (container instanceof TileEntityBrewingStand && this.brewingStand.getValue()))
                        .filter(entity -> !stolen.contains(entity.getPos()))
                        .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= auraRange.getValue().floatValue()).min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity.getPos())));
                if (tile.isPresent() && auraTimer.hasReached(auraDelay.getValue().intValue()) && (count != -2 || waitOpenTimer.hasReached(waitOpenDelayValue.getValue().longValue()))) {
                    final var container = tile.get();
                    final BlockPos chestPos = container.getPos();
                    Rotation rotation = RotationUtil.getRotationBlock(chestPos, 0);
                    RotationUtil.setTargetRotation(rotation.onPost(() -> {
                        CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(chestPos, Stealer.getFacingDirection(chestPos), EnumHand.MAIN_HAND, 0, 0, 0);
                        Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
                        if (!(container instanceof TileEntityChest)) {
                            stolen.add(chestPos);
                            selfStolen.add(chestPos);
                        }
                        selfTried.add(chestPos);
                        waitOpenTimer.reset();
                        auraTimer.reset();
                    }), 0);
                }
            }
        } else { // 打开了外部容器
            auraTimer.reset();
            if (!firstDelayTimer.hasReached(firstDelay.getValue().intValue()) || !timer.hasReached(delay)) return;

            List<Integer> slots = new ArrayList<>();
            if (mc.player.openContainer instanceof ContainerChest chest) {
                IInventory chestInv = chest.getLowerChestInventory();

                if (titleValue.getValue()) {
                    if (!checkTitle(chestInv)) {
                        return;
                    }
                }
                for (int i = 0; i < chestInv.getSizeInventory(); i++) {
                    ItemStack is = chestInv.getStackInSlot(i);
                    if (is != null && !is.isEmpty()) {
                        slots.add(i);
                    }
                }
                slots = usefulValue.getValue() ? addUsefulItem(slots) : slots;
                if (slots.isEmpty() || isInventoryFull()) {
                    close();
                    return;
                }
                Collections.shuffle(slots);

                for (int slot : slots) {
                    ItemStack is = chestInv.getStackInSlot(slot);
                    Item item = is != null ? is.getItem() : null;

                    if (item != null && !items.contains(item)) {
                        if (exploitValue.getValue()) {
                            int mouseButton = -1;
                            for (int i = 27; i < chest.getInventory().size(); i++) {
                                ItemStack slot2 = chest.getInventory().get(i);
                                if (!slot2.isEmpty()) continue;
                                mouseButton = i;
                                break;
                            }
                            if (mouseButton == -1) break;

                            try {
                                // merge
                                mc.playerController.windowClick(chest.windowId, slot, 0, ClickType.PICKUP, mc.player);
                                mc.playerController.windowClick(chest.windowId, slot, 0, ClickType.PICKUP_ALL, mc.player);
                                mc.playerController.windowClick(chest.windowId, mouseButton, 0, ClickType.PICKUP, mc.player);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mc.playerController.windowClick(chest.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
                        }
                        closeDelayTimer.reset();
                        timer.reset();
                        delay = (long) MathUtil.getRandomInRange(minDelayValue.getValue().longValue(), maxDelayValue.getValue().longValue());

                        if (!timer.hasReached(delay)) return;
                    }
                }
                this.close();
            } else if ((mc.player.openContainer instanceof ContainerFurnace && this.furnaceValue.getValue()) || (mc.player.openContainer instanceof ContainerBrewingStand && this.brewingStand.getValue())) {
                delay = (long) MathUtil.getRandomInRange(minDelayValue.getValue().longValue(), maxDelayValue.getValue().longValue());
                int lowerChestSize = 0;
                if (mc.player.openContainer instanceof ContainerFurnace furnace) {
                    lowerChestSize = 3;
                }
                if (mc.player.openContainer instanceof ContainerBrewingStand brewingStand) {
                    lowerChestSize = 5;
                }
                for (int i = 0; i < lowerChestSize; i++) {
                    Slot slot = mc.player.openContainer.getSlot(i);
                    if (slot.getHasStack()) {
                        slots.add(i);
                    }
                }
                slots = usefulValue.getValue() ? addUsefulItem(slots) : slots;
                if (slots.isEmpty() || isInventoryFull()) {
                    this.close();
                    return;
                }
                for (int i : slots) {
                    if (exploitValue.getValue()) {
                        int button = -1;
                        for (int j = 6; j < mc.player.openContainer.inventorySlots.size(); j++) {
                            ItemStack buttonStack = mc.player.openContainer.getInventory().get(j);
                            if (!buttonStack.isEmpty()) continue;
                            button = j;
                            break;
                        }
                        if (button == -1) break;

                        int test = -1;
                        for (int k = 0; k <= 8; k++) {
                            ItemStack slot1 = mc.player.inventory.getStackInSlot(k);
                            if (!slot1.isEmpty()) continue;
                            test = k;
                            break;
                        }
                        if (test == -1) {
                            List<Dropdown> slotValue = ((InvManager) Southside.moduleManager.getModuleByClass(InvManager.class)).slotValue;
                            for (int i1 = 0; i1 < slotValue.size(); i1++) {
                                String item = slotValue.get(i1).getMode();
                                if (item.equals("None")) {
                                    test = i1;
                                    break;
                                }
                            }
                            if (test == -1) break;
                            mc.playerController.windowClick(mc.player.openContainer.windowId, button, test, ClickType.SWAP, mc.player);
                        }
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, test, ClickType.SWAP, mc.player);
                        mc.playerController.windowClick(mc.player.openContainer.windowId, button, test, ClickType.SWAP, mc.player);
                    } else {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                    }
                    if (!timer.hasReached(delay)) return;
                }
                this.close();
            }
        }
    }

    public static EnumFacing getFacingDirection(final BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().isFullBlock(null)) {
            direction = EnumFacing.UP;
        }
        final RayTraceResult rayResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null) {
            return rayResult.sideHit;
        }
        return direction;
    }

    public static boolean checkTitle(IInventory inventory) {
        if (inventory instanceof InventoryBasic inventoryBasic) {
            return checkTitle(inventoryBasic.getDisplayName());
        }
        return false;
    }

    public static boolean checkTitle(ITextComponent textComponent) {
        return textComponent.getFormattedText().toLowerCase().contains(new ItemStack(Blocks.CHEST).getDisplayName().toLowerCase());
    }

    private void close() {
        if (!this.closeDelayTimer.hasReached(this.closeDelayValue.getValue().longValue())) return;
        if (!this.modeValue.getMode().equals("Simple")) {
            mc.player.closeScreen();
        }
    }

    private boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemAir) {
                return false;
            }
        }
        return true;
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketBlockAction sb) {
            if (mc.world.getBlockState(sb.getBlockPosition()).getBlock() instanceof BlockChest && sb.getData1() == 1 && sb.getData2() == 1) {
                stolen.add(sb.getBlockPosition());
                if (selfTried.contains(sb.getBlockPosition())) {
                    selfStolen.add(sb.getBlockPosition());
                }
            }
        }
//        ChatUtil.info("" + isSilent() + " : " + (INSTANCE != null) + " " + INSTANCE.isEnabled() + " " + INSTANCE.modeValue.getMode().equals("Silent") + "");
        if (!isSilent()) return;
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock packet) {
            if (mc.currentScreen instanceof GuiContainer) {
                event.setCancelled(true);
                ChatUtil.info("cancel");
                return;
            }
            lastC08 = packet.getPos();
            count = -2;
        }
        if (event.getPacket() instanceof SPacketOpenWindow packet && lastC08 != null) {
            Block block = mc.world.getBlockState(lastC08).getBlock();
            if (block instanceof BlockFurnace || block instanceof BlockBrewingStand || (block instanceof BlockChest && checkTitle(packet.getWindowTitle()))) {
                currentChest = lastC08;
                count = -1;
            }
        }
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }

    public static boolean isSilent() {
        return INSTANCE != null && INSTANCE.isEnabled() && INSTANCE.modeValue.getMode().equals("Silent");
//        return INSTANCE.modeValue.getValue().equals("Silent");
    }

    public static boolean isSilentStealing() {
        return Stealer.isSilent() &&
                Stealer.currentChest != null &&
                mc.currentScreen instanceof GuiContainer &&
                (mc.player.openContainer instanceof ContainerFurnace ||
                        mc.player.openContainer instanceof ContainerBrewingStand ||
                        (mc.player.openContainer instanceof ContainerChest chest && Stealer.checkTitle(chest.getLowerChestInventory())));
    }

    private List<Integer> addUsefulItem(List<Integer> slots) {
        List<Integer> newSlots = new ArrayList<>();
        /*
        更新背包内的物品分数
         */
        float bestSwordDamage = -1, bestBowDamage = -1, bestPickAxeStrength = -1, bestAxeStrength = -1;
        float bestBootsProtection = mc.player.inventoryContainer.getSlot(8).getStack() == null ? -1 : InventoryUtil.getArmorScore(mc.player.inventoryContainer.getSlot(8).getStack()),
                bestLeggingsProtection = mc.player.inventoryContainer.getSlot(7).getStack() == null ? -1 : InventoryUtil.getArmorScore(mc.player.inventoryContainer.getSlot(7).getStack()),
                bestChestPlateProtection = mc.player.inventoryContainer.getSlot(6).getStack() == null ? -1 : InventoryUtil.getArmorScore(mc.player.inventoryContainer.getSlot(6).getStack()),
                bestHelmetProtection = mc.player.inventoryContainer.getSlot(5).getStack() == null ? -1 : InventoryUtil.getArmorScore(mc.player.inventoryContainer.getSlot(5).getStack());
        for (int i = 9; i < 45; i++) {
            final Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot != null && slot.getStack() != null) {
                final ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof ItemSword) {
                    bestSwordDamage = Math.max(InventoryUtil.getDamageScore(stack), bestSwordDamage);
                } else if (stack.getItem() instanceof ItemBow) {
                    bestBowDamage = Math.max(InventoryUtil.getBowScore(stack), bestBowDamage);
                } else if (stack.getItem() instanceof ItemPickaxe) {
                    bestPickAxeStrength = Math.max(InventoryUtil.getToolScore(stack), bestPickAxeStrength);
                } else if (stack.getItem() instanceof ItemAxe) {
                    bestAxeStrength = Math.max(InventoryUtil.getSharpAxeScore(stack), bestAxeStrength);
                } else if (stack.getItem() instanceof ItemArmor armor) {
                    switch (armor.armorType) {
                        case HEAD -> {
                            bestHelmetProtection = Math.max(InventoryUtil.getArmorScore(stack), bestHelmetProtection);
                        }
                        case CHEST -> {
                            bestChestPlateProtection = Math.max(InventoryUtil.getArmorScore(stack), bestChestPlateProtection);
                        }
                        case LEGS -> {
                            bestLeggingsProtection = Math.max(InventoryUtil.getArmorScore(stack), bestLeggingsProtection);
                        }
                        case FEET -> {
                            bestBootsProtection = Math.max(InventoryUtil.getArmorScore(stack), bestBootsProtection);
                        }
                    }
                }
            }
        }
        int bestSwordSlot = -1, bestBowSlot = -1, bestPickAxeSlot = -1, bestAxeSlot = -1, bestHelmetSlot = -1, bestLeggingsSlot = -1, bestChestPlateSlot = -1, bestBootsSlot = -1;
        AutoWeapon autoWeapon = ((AutoWeapon) Southside.moduleManager.getModuleByName("AutoWeapon"));
        for (int i : slots) {
            final ItemStack stack = mc.player.openContainer.getInventory().get(i);
            if (stack.getItem() instanceof ItemSword) {
                if (autoWeapon != null && autoWeapon.isEnabled() && autoWeapon.overrideSwordSorting()) {
                    newSlots.add(i);
                    continue;
                }
                if (InventoryUtil.getDamageScore(stack) > bestSwordDamage) {
                    bestSwordDamage = InventoryUtil.getDamageScore(stack);
                    bestSwordSlot = i;
                }
            } else if (stack.getItem() instanceof ItemBow) {
                if (InventoryUtil.getBowScore(stack) > bestBowDamage) {
                    bestBowDamage = InventoryUtil.getBowScore(stack);
                    bestBowSlot = i;
                }
            } else if (stack.getItem() instanceof ItemPickaxe) {
                if (InventoryUtil.getToolScore(stack) > bestPickAxeStrength) {
                    bestPickAxeStrength = InventoryUtil.getToolScore(stack);
                    bestPickAxeSlot = i;
                }
            } else if (stack.getItem() instanceof ItemAxe) {
                if (InventoryUtil.getSharpAxeScore(stack) > bestAxeStrength) {
                    bestAxeStrength = InventoryUtil.getSharpAxeScore(stack);
                    bestAxeSlot = i;
                }
            } else if (stack.getItem() instanceof ItemArmor armor) {
                switch (armor.armorType) {
                    case HEAD -> {
                        if (InventoryUtil.getArmorScore(stack) > bestHelmetProtection) {
                            bestHelmetProtection = Math.max(InventoryUtil.getArmorScore(stack), bestHelmetProtection);
                            bestHelmetSlot = i;
                        }
                    }
                    case CHEST -> {
                        if (InventoryUtil.getArmorScore(stack) > bestChestPlateProtection) {
                            bestChestPlateProtection = InventoryUtil.getArmorScore(stack);
                            bestChestPlateSlot = i;
                        }
                    }
                    case LEGS -> {
                        if (InventoryUtil.getArmorScore(stack) > bestLeggingsProtection) {
                            bestLeggingsProtection = InventoryUtil.getArmorScore(stack);
                            bestLeggingsSlot = i;
                        }
                    }
                    case FEET -> {
                        if (InventoryUtil.getArmorScore(stack) > bestBootsProtection) {
                            bestBootsProtection = InventoryUtil.getArmorScore(stack);
                            bestBootsSlot = i;
                        }
                    }
                }
            } else if (stack.getItem() instanceof ItemPotion) {
                for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
                    if ("effect.moveSpeed".equals(effect.getEffectName())
                            || "effect.jump".equals(effect.getEffectName())
                            || "effect.regeneration".equals(effect.getEffectName())
                            || "effect.resistance".equals(effect.getEffectName())
                            || "effect.fireResistance".equals(effect.getEffectName())
                            || "effect.heal".equals(effect.getEffectName())) {
                        newSlots.add(i);
                    }
                }
            } else { // 其他没分类的暂时全部加进去
                newSlots.add(i);
            }
            if (bestSwordSlot != -1) newSlots.add(bestSwordSlot);
            if (bestBowSlot != -1) newSlots.add(bestBowSlot);
            if (bestPickAxeSlot != -1) newSlots.add(bestPickAxeSlot);
            if (bestAxeSlot != -1) newSlots.add(bestAxeSlot);
            if (bestHelmetSlot != -1) newSlots.add(bestHelmetSlot);
            if (bestLeggingsSlot != -1) newSlots.add(bestLeggingsSlot);
            if (bestChestPlateSlot != -1) newSlots.add(bestChestPlateSlot);
            if (bestBootsSlot != -1) newSlots.add(bestBootsSlot);
        }
        return newSlots;
    }
}
