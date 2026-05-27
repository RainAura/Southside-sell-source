package dev.diona.southside.gui.container;

import dev.diona.southside.Southside;
import dev.diona.southside.util.quickmacro.HytContainerGuiUtil;
import dev.diona.southside.util.quickmacro.metadatas.PartyRequestMetadata;
import dev.diona.southside.util.quickmacro.metadatas.VexViewMetadata;
import dev.diona.southside.util.quickmacro.vexview.VexViewSender;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;

public class HytPartyInviteGui extends GuiChest {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public static Item requestMaterial = Items.SKULL;
    private ArrayList<PartyRequestMetadata> requests = new ArrayList<>();

    public HytPartyInviteGui(ArrayList<PartyRequestMetadata> requests) {
        super(Southside.MC.mc.player.inventory, new HytPartyInviteContainer(requests).inventory);
        this.requests = requests;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        if (slot != null && slot.getHasStack()) {
            ItemStack clickedStack = slot.getStack();
            Item item = clickedStack.getItem();
            if (item.equals(requestMaterial)) {
                VexViewMetadata operation = null;
                if (mouseButton == 0) {
                    operation = new VexViewMetadata("Accept", clickedStack.getTagCompound().getString("AcceptId"));
                } else {
                    operation = new VexViewMetadata("Deny", clickedStack.getTagCompound().getString("DenyId"));
                }
                VexViewSender.clickButton(operation.id);
                requests.remove(clickedStack.getTagCompound().getInteger("RequestIndex"));
                mc.displayGuiScreen(new HytPartyInviteGui(requests));
//                VexViewSender.clickButton(createButton.id);
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

    private static class HytPartyInviteContainer extends Container {
        private final IInventory inventory;
        private final ArrayList<Slot> slots = new ArrayList<>();
        private ArrayList<PartyRequestMetadata> requests = new ArrayList<>();

        public HytPartyInviteContainer(ArrayList<PartyRequestMetadata> requests) {
            this.requests = requests;

            inventory = new InventoryBasic("[Southside] Hyt Party Invite Gui", false, 9);
            int playerInventoryStartX = 8;
            int playerInventoryStartY = 51;
            for (int j = 0; j < 9; j++) {
                Slot slot = new Slot(inventory, j, playerInventoryStartX + j * 18, playerInventoryStartY);
                slots.add(slot);
                addSlotToContainer(slot);
            }
            for (int j = 0; j < 9; j++) {
                if (j < requests.size()) {
                    ItemStack requestItem = HytContainerGuiUtil.getItemStack(requestMaterial, requests.get(j).getName(), new String[]{"" + TextFormatting.GREEN + TextFormatting.BOLD + "左键接受", "" + TextFormatting.RED + TextFormatting.BOLD + "右键拒绝"});
                    requestItem.setTagInfo("AcceptId", new NBTTagString(requests.get(j).getAcceptId()));
                    requestItem.setTagInfo("DenyId", new NBTTagString(requests.get(j).getDenyId()));
                    requestItem.setTagInfo("RequestIndex", new NBTTagInt(j));
                    requestItem.addEnchantment(Enchantments.UNBREAKING, 1);
                    slots.get(j).putStack(requestItem);
                }
            }

        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }
}
