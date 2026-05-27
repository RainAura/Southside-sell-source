package net.minecraft.client.gui.inventory;

import dev.diona.southside.Southside;
import dev.diona.southside.module.modules.player.Stealer;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.InventoryUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjglx.opengl.GL11;

import java.awt.*;

public class GuiChest extends GuiContainer
{
    /** The ResourceLocation containing the chest GUI texture. */
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final IInventory upperChestInventory;

    /**
     * The chest's inventory. Number of slots will vary based off of the type of chest.
     */
    private final IInventory lowerChestInventory;

    /**
     * Window height is calculated with these values; the more rows, the higher
     */
    private final int inventoryRows;

    public GuiChest(IInventory upperInv, IInventory lowerInv)
    {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().player));
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (Stealer.isSilentStealing()) {
            ContainerChest chest = (ContainerChest) mc.player.openContainer;

            if (Stealer.count == -1) {
                Stealer.count = InventoryUtil.count(chest);
                Stealer.progress.update(0);
                Stealer.progress.set(0);
            }

            int prog = Stealer.count - InventoryUtil.count(chest);
            if (prog > Stealer.progress.target) {
                Stealer.progress.update(prog);
            }

            GuiScreen guiScreen = mc.currentScreen;
            mc.setIngameFocus();
            mc.currentScreen = guiScreen;

            float height = 66F;
            float width = 174F;
            Vec2f base = RenderUtil.worldScreenPos(new Vec3d(
                    Stealer.currentChest.getX() + 0.5D,
                    Stealer.currentChest.getY() + 0.5D,
                    Stealer.currentChest.getZ() + 0.5D
            ));
            if (base == null) return;
            Vec2f pos = new Vec2f(base.x, base.y);

            Color backgroundColor = new Color(20, 20, 20, 130);
            float startY = pos.y - 120;
            float fontSize = 10;

            // draw rect
            RenderUtil.drawRect(pos.x - width / 2, startY - Southside.fontManager.font.getHeight(fontSize) - 5F, pos.x + width / 2 , startY + height, backgroundColor.getRGB());
            String str = chest.getLowerChestInventory().getDisplayName().toString();
            Southside.fontManager.font.drawString(fontSize, str, (width / 2F) - (Southside.fontManager.font.getStringWidth(fontSize, str) * 0.5F), -(Southside.fontManager.font.getHeight(fontSize)), Color.WHITE);

//            RenderUtil.drawRect(pos.x - width / 2, startY - Southside.fontManager.font.getHeight(fontSize) - 5F, pos.x + width / 2, startY - height, backgroundColor.getRGB());
//            String str = chest.getLowerChestInventory().getDisplayName().getUnformattedText();
//            Southside.fontManager.font.drawString(fontSize, str, pos.x - (width / 2F) + (Southside.fontManager.font.getStringWidth(fontSize, str) * 0.5F), startY - (Southside.fontManager.font.getHeight(fontSize)) - 2.5F, Color.WHITE);

            float bar = 154F;
            RoundUtil.drawRound(pos.x - bar / 2, startY - 5F, bar * Stealer.progress.get() / Stealer.count, 2, 1, Color.WHITE);

            // render item
            GL11.glPushMatrix();
            GL11.glTranslated(pos.x - width / 2, startY + 20F, 0);
            RenderHelper.enableGUIStandardItemLighting();
            renderInv(0, 8, 6, -12, chest);
            renderInv(9, 17, 6, 6, chest);
            renderInv(18, 26, 6, 24, chest);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GL11.glPopMatrix();

            return;
        }

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private void renderInv(int slot, int endSlot, int x, int y, Container container) {
        int xOffset = x;
        for (int i = slot; i <= endSlot; i++) {
            xOffset += 18;
            if (container.getSlot(i).getStack() != null) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(container.getSlot(i).getStack(), xOffset - 18, y);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, container.getSlot(i).getStack(), xOffset - 18, y);
            }
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
