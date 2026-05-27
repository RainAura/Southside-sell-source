package dev.diona.southside.module.modules.player;

import dev.diona.southside.Southside;
import dev.diona.southside.event.PacketType;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.player.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;

import static dev.diona.southside.module.modules.player.KitSelector.SelectStatus.*;

public class KitSelector extends Module {
    public final Slider select = new Slider("Select Slot", 6, 0, 7, 1);
    private SelectStatus status = IDLE;
    public KitSelector(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public boolean onEnable() {
        this.status = IDLE;
        return super.onEnable();
    }

    @EventListener
    public void onWorldLoad(WorldEvent event) {
        this.status = IDLE;
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getType().equals(PacketType.RECEIVE) && !DONE.equals(this.status)) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof SPacketSetSlot setSlot && !setSlot.getStack().isEmpty()) {
                if (IDLE.equals(this.status)) {
                    if (setSlot.getSlot() >= 36 && setSlot.getSlot() <= 44) {
                        ItemStack itemStack = setSlot.getStack();
                        if (!itemStack.isEmpty() && itemStack.getItem().equals(Items.ENDER_EYE)) {
                            int slot = setSlot.getSlot() - 36;
                            if (mc.player.inventory.currentItem != slot) {
                                mc.player.inventory.currentItem = slot;
                                mc.playerController.updateController();
                            }
                            mc.rightClickMouse();
                            this.status = WAITING_OPEN;
                        }
                    }
                }
            }
            if (packet instanceof SPacketWindowItems windowItems) {
                if (IDLE.equals(this.status) && windowItems.getWindowId() == 0) {
                    for (int i = 0; i < windowItems.getItemStacks().size(); i++) {
                        if (i >= 36 && i <= 44) {
                            ItemStack itemStack = windowItems.getItemStacks().get(i);
                            if (!itemStack.isEmpty() && itemStack.getItem().equals(Items.ENDER_EYE)) {
                                int slot = i - 36;
                                if (mc.player.inventory.currentItem != slot) {
                                    mc.player.inventory.currentItem = slot;
                                    mc.playerController.updateController();
                                }
                                mc.rightClickMouse();
                                this.status = WAITING_OPEN;
                            }
                        }

                    }
                } else if (WAITING_ITEMS.equals(status) && windowItems.getWindowId() != 0) {
                    for (int i = 0; i < windowItems.getItemStacks().size(); i++) {
                        ItemStack itemStack = windowItems.getItemStacks().get(i);
                        if (!itemStack.isEmpty() && i == select.getValue().intValue()) {
                            mc.playerController.windowClick(windowItems.getWindowId(), i, 0, ClickType.PICKUP, mc.player);
                            event.cancel();
                        }
                    }
                }
            }
            if (event.getPacket() instanceof SPacketOpenWindow && WAITING_OPEN.equals(status)) {
                if (((SPacketOpenWindow) event.getPacket()).getWindowTitle().getFormattedText().equals("§5选择你的职业§r")) {
                    this.status = WAITING_ITEMS;
                    event.cancel();
                }
            }
        }
    }
    enum SelectStatus {
        IDLE,
        WAITING_OPEN,
        WAITING_ITEMS,
        DONE
    }
}
