package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class NewStealer extends Module {

    public NewStealer(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public final Slider firstDelay = new Slider("First Delay", 150, 0, 1000, 10);
    public final Slider delayValue = new Slider("Delay", 0, 0, 1000, 10);
    public final Slider closeDelayValue = new Slider("Close Delay", 500, 0, 1000, 10);

    public final Switch aura = new Switch("Aura", false);

    public final Slider auraDelay = new Slider("AuraDelay", 100D, 0D, 1000D, 10D);

    private TimerUtil firstDelayTimer = new TimerUtil();
    private TimerUtil delayTimer = new TimerUtil();
    private TimerUtil closeTimer = new TimerUtil();
    private TimerUtil auraDelayTimer = new TimerUtil();


    public final HashSet<BlockPos> stolen = new HashSet<>();


    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.openContainer.windowId == 0) {
            firstDelayTimer.reset();
            /*
             * Chest Aura
             */
            if (aura.getValue() && !Southside.moduleManager.getModuleByClass(Blink.class).isEnabled()) {
                final var tile = mc.world.loadedTileEntityList.stream()
                        .filter(container -> container instanceof TileEntityChest || container instanceof TileEntityFurnace || container instanceof TileEntityBrewingStand)
                        .filter(entity -> !stolen.contains(entity.getPos()))
                        .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 4.5F).min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity.getPos())));
                if (tile.isPresent() && auraDelayTimer.hasReached(auraDelay.getValue().intValue())) {
                    final var container = tile.get();
                    if (mc.currentScreen == null) {
                        CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(container.getPos(), Stealer.getFacingDirection(container.getPos()), EnumHand.MAIN_HAND, 0, 0, 0);
                        packet.placeDisabler = true;
                        Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
                        stolen.add(container.getPos());
                        auraDelayTimer.reset();
                    }
                }
            }

        } else {
            mc.currentScreen = null;
            if (!firstDelayTimer.hasReached(firstDelay.getValue().intValue())) return;
            if (mc.player.openContainer instanceof ContainerChest || mc.player.openContainer instanceof ContainerFurnace || mc.player.openContainer instanceof ContainerBrewingStand) {
                int lowerChestSize = 0;
                if (mc.player.openContainer instanceof ContainerChest chest) {
                    lowerChestSize = chest.getLowerChestInventory().getSizeInventory();
                }
                if (mc.player.openContainer instanceof ContainerFurnace furnace) {
                    lowerChestSize = 3;
                }
                if (mc.player.openContainer instanceof ContainerBrewingStand brewingStand) {
                    lowerChestSize = 5;
                }
                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < lowerChestSize; i++) {
                    ItemStack is = mc.player.openContainer.getInventory().get(i);
                    if (!is.isEmpty()) {
                        slots.add(i);
                    }
                }
                if (slots.isEmpty() || isInventoryFull()) {
                    if (closeTimer.hasReached(closeDelayValue.getValue().intValue())) {
                        mc.player.closeScreen();
                    }
                } else {
                    Collections.shuffle(slots);
                    slots.forEach(slot -> {
                        if (!delayTimer.hasReached(delayValue.getValue().intValue())) {
                            return;
                        }
                        mc.playerController.windowClick(mc.player.openContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
                        delayTimer.reset();
                    });
                    closeTimer.reset();
                }
            }
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


    private boolean itemIsUseful(ItemStack itemStack) {
        Item item = itemStack.getItem();

        if ((item instanceof ItemAxe || item instanceof ItemPickaxe)) {
            return true;
        }

        if (item instanceof ItemFood)
            return true;
        if ((item instanceof ItemBow))
            return true;
        if (item instanceof ItemPotion )
            return true;
        if (item instanceof ItemSword)
            return true;
        if (item instanceof ItemArmor)
            return true;
        if (item instanceof ItemBlock)
            return true;

        return item instanceof ItemEnderPearl;
    }
}
