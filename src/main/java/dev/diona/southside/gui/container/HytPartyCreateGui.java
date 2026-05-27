package dev.diona.southside.gui.container;

import dev.diona.southside.Southside;
import dev.diona.southside.util.quickmacro.HytContainerGuiUtil;
import dev.diona.southside.util.quickmacro.metadatas.VexViewMetadata;
import dev.diona.southside.util.quickmacro.vexview.VexViewSender;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;

public class HytPartyCreateGui extends GuiChest {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final VexViewMetadata createButton, joinButton;
    public static Item createMaterial = Item.getItemFromBlock(Blocks.CRAFTING_TABLE);
    public static Item joinMaterial = Items.OAK_DOOR;

    public HytPartyCreateGui(VexViewMetadata createButton, VexViewMetadata joinButton) {
        super(Southside.MC.mc.player.inventory, new HytPartyCreateContainer().inventory);
        this.createButton = createButton;
        this.joinButton = joinButton;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        if (slot != null && slot.getHasStack()) {
            ItemStack clickedStack = slot.getStack();
            Item item = clickedStack.getItem();
            if (item.equals(createMaterial)) {
                VexViewSender.clickButton(createButton.id);
            } else if (item.equals(joinMaterial)) {
                VexViewSender.clickButton(joinButton.id);
            }
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType) {
        if (clickType == ClickType.THROW) return;
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);
    }

    private static class HytPartyCreateContainer extends Container {
        private final IInventory inventory;
        private final ArrayList<Slot> slots = new ArrayList<>();

        public HytPartyCreateContainer() {
            inventory = new InventoryBasic("[Southside] Hyt Party Create GUI", false, 9);
            int playerInventoryStartX = 8;
            int playerInventoryStartY = 51;
            for (int j = 0; j < 9; j++) {
                Slot slot = new Slot(inventory, j, playerInventoryStartX + j * 18, playerInventoryStartY);
                slots.add(slot);
                addSlotToContainer(slot);
            }
            slots.get(0).putStack(HytContainerGuiUtil.getItemStack(createMaterial, "" + TextFormatting.GREEN + TextFormatting.BOLD + "创建队伍"));
            slots.get(1).putStack(HytContainerGuiUtil.getItemStack(joinMaterial, "" + TextFormatting.GREEN + TextFormatting.BOLD + "加入队伍"));
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }
}

