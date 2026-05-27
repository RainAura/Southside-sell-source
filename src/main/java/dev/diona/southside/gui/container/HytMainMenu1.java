package dev.diona.southside.gui.container;

import dev.diona.southside.util.quickmacro.HytContainerGuiUtil;
import dev.diona.southside.util.quickmacro.metadatas.GermItem;
import dev.diona.southside.util.quickmacro.metadatas.GermMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HytMainMenu1 extends GuiChest {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private String uid;

    public HytMainMenu1(String uid, List<GermMetadata> data) {
//        super(Minecraft.getMinecraft().player.inventory, Minecraft.getMinecraft().player.inventory);
        super(Minecraft.getMinecraft().player.inventory, new HytMainMenu1Container(data).inventory);
        this.uid = uid;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        if (slot != null && slot.getHasStack()) {
            if (slot.getStack() instanceof GermItem clickedStack) {
                clickedStack.metadata.mouseClicked(uid);
            }
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType type) {
        if (type == ClickType.THROW) return;
        if (type == ClickType.SWAP) return;
        super.handleMouseClick(slotIn, slotId, clickedButton, type);
    }

    private static class HytMainMenu1Container extends Container {
        private final IInventory inventory;
        private final ArrayList<Slot> slots = new ArrayList<>();
        private int slotCount = 0;

        private void put(GermItem germItem) {
            slots.get(slotCount++).putStack(germItem);
        }

        public HytMainMenu1Container(List<GermMetadata> data) {
//            for (GermMetadata metadata : data) {
//                ChatUtil.info(metadata.getText());
//            }
            inventory = new InventoryBasic("[Southside] Hyt Main Menu", false, 9);
            int playerInventoryStartX = 8;
            int playerInventoryStartY = 51;
            for (int j = 0; j < 9; j++) {
                Slot slot = new Slot(inventory, j, playerInventoryStartX + j * 18, playerInventoryStartY);
                slots.add(slot);
                addSlotToContainer(slot);
            }
            data.forEach(metadata -> {
                switch (metadata.getText().trim()) {
                    case "起床战争" -> this.put(HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "起床战争"));

                    case "练习场" -> this.put(HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "练习场"));
                    case "8队单人绝杀模式" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "8队单人绝杀模式").setCount(1));
                    case "8队双人绝杀模式" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "8队双人绝杀模式").setCount(2));
                    case "4队4人绝杀模式" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "4队4人绝杀模式").setCount(4));
                    case "无限火力16" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "无限火力16").setCount(16));
                    case "无限火力32" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.BED, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "无限火力32").setCount(32));

                    case "空岛战争" -> this.put(HytContainerGuiUtil.getItemStack(Items.FEATHER, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "空岛战争"));

                    case "空岛战争单人" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.FEATHER, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "空岛战争单人").setCount(1));
                    case "空岛战争双人" -> this.put((GermItem) HytContainerGuiUtil.getItemStack(Items.FEATHER, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "空岛战争双人").setCount(2));

                    case "休闲游戏" -> this.put(HytContainerGuiUtil.getItemStack(Items.ENDER_EYE, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "休闲游戏"));

                    case "守卫水晶" -> this.put(HytContainerGuiUtil.getItemStack(Items.END_CRYSTAL, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "守卫水晶"));
                    case "小游戏派对" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemFromBlock(Blocks.CHEST), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "小游戏派对"));
                    case "抢羊大作战" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemFromBlock(Blocks.WOOL), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "抢羊大作战"));
                    case "叠叠乐" -> this.put(HytContainerGuiUtil.getItemStack(Items.DIAMOND_BOOTS, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "叠叠乐"));
                    case "烫手山芋" -> this.put(HytContainerGuiUtil.getItemStack(Items.BAKED_POTATO, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "烫手山芋"));
                    case "狼人杀" -> this.put(HytContainerGuiUtil.getItemStack(Items.BOW, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "狼人杀"));

                    case "竞技游戏" -> this.put(HytContainerGuiUtil.getItemStack(Items.IRON_SWORD, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "竞技游戏"));

                    case "废土" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemFromBlock(Blocks.DIRT), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "废土"));
                    case "吃鸡荒野" -> this.put(HytContainerGuiUtil.getItemStack(Items.COOKED_CHICKEN, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "吃鸡荒野"));
                    case "职业战争" -> this.put(HytContainerGuiUtil.getItemStack(Items.DIAMOND_AXE, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "职业战争"));
                    case "竞技场（等级限制）" -> this.put(HytContainerGuiUtil.getItemStack(Items.DIAMOND_SWORD, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "竞技场（等级限制）"));
                    case "天坑之战" -> this.put(HytContainerGuiUtil.getItemStack(Items.IRON_SWORD, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "天坑之战"));

                    case "生存" -> this.put(HytContainerGuiUtil.getItemStack(Items.GOLDEN_APPLE, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "生存"));


                    case "单方块" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemFromBlock(Blocks.GRASS), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "单方块"));
                    case "空岛" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemById(6), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "空岛"));
                    case "监狱风云" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemFromBlock(Blocks.IRON_BARS), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "监狱风云"));
                    case "钻石大陆" -> this.put(HytContainerGuiUtil.getItemStack(Item.getItemFromBlock(Blocks.DIAMOND_ORE), metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "钻石大陆"));

                    case "战争" -> this.put(HytContainerGuiUtil.getItemStack(Items.DIAMOND_SWORD, metadata, "" + TextFormatting.GREEN + TextFormatting.BOLD + "战争"));
                }
            });
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }

}

