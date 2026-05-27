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

public class HytPartyManageGui extends GuiChest {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final VexViewMetadata leaveButton, disbandButton, inviteButton, requestButton, disband, invite, request;

    public HytPartyManageGui(VexViewMetadata leaveButton, VexViewMetadata disbandButton, VexViewMetadata inviteButton, VexViewMetadata requestButton) {
        super(Southside.MC.mc.player.inventory, new HytPartyManageContainer(inviteButton != null, disbandButton != null).inventory);
        this.leaveButton = leaveButton;
        this.disbandButton = disbandButton;
        this.inviteButton = inviteButton;
        this.requestButton = requestButton;
        disband = null;
        invite = null;
        request = null;
    }

    public static Item leaveMaterial = Item.getItemFromBlock(Blocks.REDSTONE_BLOCK);
    public static Item disbandMaterial = Item.getItemFromBlock(Blocks.REDSTONE_TORCH);
    public static Item inviteMaterial = Item.getItemFromBlock(Blocks.CHEST);
    public static Item requestMaterial = Items.IRON_DOOR;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        if (slot != null && slot.getHasStack()) {
            ItemStack clickedStack = slot.getStack();
            Item item = clickedStack.getItem();
            if (item.equals(leaveMaterial)) {
                VexViewSender.clickButton(leaveButton.id);
            } else if (item.equals(disbandMaterial)) {
                VexViewSender.clickButton(disbandButton.id);
            } else if (item.equals(inviteMaterial)) {
                VexViewSender.clickButton(inviteButton.id);
            } else if (item.equals(requestMaterial)) {
                VexViewSender.clickButton(requestButton.id);
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

    private static class HytPartyManageContainer extends Container {
        private final IInventory inventory;
        private final ArrayList<Slot> slots = new ArrayList<>();

        public HytPartyManageContainer(boolean canInvite, boolean canDisband) {
            inventory = new InventoryBasic("[Southside] Hyt Party Manage GUI", false, 9);
            int playerInventoryStartX = 8;
            int playerInventoryStartY = 51;
            for (int j = 0; j < 9; j++) {
                Slot slot = new Slot(inventory, j, playerInventoryStartX + j * 18, playerInventoryStartY);
                slots.add(slot);
                addSlotToContainer(slot);
            }
            if (canInvite) {
                slots.get(0).putStack(HytContainerGuiUtil.getItemStack(leaveMaterial, "" + TextFormatting.RED + TextFormatting.BOLD + "离开队伍"));
                slots.get(1).putStack(HytContainerGuiUtil.getItemStack(disbandMaterial, "" + TextFormatting.DARK_RED + TextFormatting.BOLD + "解散队伍"));
                slots.get(2).putStack(HytContainerGuiUtil.getItemStack(inviteMaterial, "" + TextFormatting.GREEN + TextFormatting.BOLD + "邀请入队"));
                slots.get(3).putStack(HytContainerGuiUtil.getItemStack(requestMaterial, "" + TextFormatting.GREEN + TextFormatting.BOLD + "申请列表"));
            } else {
                slots.get(0).putStack(HytContainerGuiUtil.getItemStack(leaveMaterial, "" + TextFormatting.RED + TextFormatting.BOLD + "离开队伍"));
                if (canDisband) {
                    slots.get(1).putStack(HytContainerGuiUtil.getItemStack(disbandMaterial, "" + TextFormatting.DARK_RED + TextFormatting.BOLD + "解散队伍"));
                }
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }

}

