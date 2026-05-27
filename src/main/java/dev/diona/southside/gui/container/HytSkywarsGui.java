//package dev.diona.southside.gui.container;
//
//import dev.diona.southside.Southside;
//import dev.diona.southside.module.modules.misc.hytprotocols.GermModProcessor;
//import dev.diona.southside.util.hyt.HytContainerGuiUtil;
//import dev.diona.southside.util.hyt.metadatas.GermMetadata;
//import io.netty.buffer.Unpooled;
//import net.minecraft.client.gui.inventory.GuiChest;
//import net.minecraft.client.gui.inventory.GuiContainer;
//import net.minecraft.client.gui.inventory.GuiInventory;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.init.Enchantments;
//import net.minecraft.init.Items;
//import net.minecraft.inventory.*;
//import net.minecraft.inventory.Container;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemSword;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.network.play.client.CPacketCustomPayload;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.TextFormatting;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class HytSkywarsGui extends GuiChest {
//    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
//
//    public static Item singleMaterial = Items.IRON_SWORD;
//    public static Item doubleMaterial = Items.DIAMOND_SWORD;
//
//    private final GermMetadata singleMode;
//    private final GermMetadata doubleMode;
//
//    public HytSkywarsGui(GermMetadata singleMode, GermMetadata doubleMode) {
//        super(Southside.MC.mc.player.inventory, new HytSkywarsContainer().inventory);
//        this.singleMode = singleMode;
//        this.doubleMode = doubleMode;
//    }
//
//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        Slot slot = getSlotAtPosition(mouseX, mouseY);
//        if (slot != null && slot.getHasStack()) {
//            ItemStack clickedStack = slot.getStack();
//            if (clickedStack.getItem() instanceof ItemSword sword) {
//                GermMetadata clicked = null;
//                if (sword.equals(singleMaterial)) {
//                    clicked = singleMode;
//                }
//                if (sword.equals(doubleMaterial)) {
//                    clicked = doubleMode;
//                }
//                if (clicked != null) {
//                    mc.getConnection().sendPacket(new CPacketCustomPayload(GermModProcessor.CLIENT_CHANNEL, new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
//                            .writeString(clicked.getParentUuid())
//                            .writeString(clicked.getPath())
//                            .writeInt(0))
//                    ));
//                    mc.getConnection().sendPacket(new CPacketCustomPayload(GermModProcessor.CLIENT_CHANNEL, new PacketBuffer(Unpooled.buffer().writeInt(11))
//                            .writeString(clicked.getParentUuid())
//                    ));
//                    mc.displayGuiScreen(null);
//                }
//            }
//            return;
//        }
//        super.mouseClicked(mouseX, mouseY, mouseButton);
//    }
//
//    @Override
//    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType type) {
//        if (type == ClickType.THROW) return;
//        if (type == ClickType.SWAP) return;
//        super.handleMouseClick(slotIn, slotId, clickedButton, type);
//    }
//
//    private static class HytSkywarsContainer extends Container {
//        private final IInventory inventory;
//        private final ArrayList<Slot> slots = new ArrayList<>();
//
//        public HytSkywarsContainer() {
//            inventory = new InventoryBasic("[Southside] Hyt Skywars GUI", false, 9);
//            int playerInventoryStartX = 8;
//            int playerInventoryStartY = 51;
//            for (int j = 0; j < 9; j++) {
//                Slot slot = new Slot(inventory, j, playerInventoryStartX + j * 18, playerInventoryStartY);
//                slots.add(slot);
//                addSlotToContainer(slot);
//            }
//            slots.get(0).putStack(HytContainerGuiUtil.getItemStack(singleMaterial, "" + TextFormatting.GREEN + TextFormatting.BOLD + "单人模式"));
//            slots.get(1).putStack(HytContainerGuiUtil.getItemStack(doubleMaterial, "" + TextFormatting.GREEN + TextFormatting.BOLD + "双人模式"));
//        }
//
//        @Override
//        public boolean canInteractWith(EntityPlayer playerIn) {
//            return true;
//        }
//    }
//
//}
//
