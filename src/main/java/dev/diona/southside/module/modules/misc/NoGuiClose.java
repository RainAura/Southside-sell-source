package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.gui.OneConfigGui;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.server.SPacketCloseWindow;

public class NoGuiClose extends Module {
    public NoGuiClose(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketCloseWindow closeWindow) {
            if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu || mc.currentScreen instanceof OneConfigGui) {
                event.setCancelled(true);
            }
            if (mc.currentScreen instanceof GuiInventory && closeWindow.windowId != 0) {
                event.setCancelled(true);
            }
        }
    }
}
