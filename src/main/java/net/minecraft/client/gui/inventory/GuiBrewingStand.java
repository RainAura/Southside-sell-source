package net.minecraft.client.gui.inventory;

import dev.diona.southside.Southside;
import dev.diona.southside.module.modules.player.Stealer;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjglx.opengl.GL11;

import java.awt.*;

public class GuiBrewingStand extends GuiContainer
{
    private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[] {29, 24, 20, 16, 11, 6, 0};

    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private final IInventory tileBrewingStand;

    public GuiBrewingStand(InventoryPlayer playerInv, IInventory inventoryIn)
    {
        super(new ContainerBrewingStand(playerInv, inventoryIn));
        this.playerInventory = playerInv;
        this.tileBrewingStand = inventoryIn;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (Stealer.isSilentStealing()) {
            GuiScreen guiScreen = mc.currentScreen;
            mc.setIngameFocus();
            mc.currentScreen = guiScreen;

            float height = 12F;
            float width = 70F;
            Vec2f base = RenderUtil.worldScreenPos(new Vec3d(
                    Stealer.currentChest.getX() + 0.5D,
                    Stealer.currentChest.getY() + 0.5D,
                    Stealer.currentChest.getZ() + 0.5D
            ));
            if (base == null) return;
            Vec2f pos = new Vec2f(base.x, base.y);

            Color backgroundColor = new Color(20, 20, 20, 130);
            float startY = pos.y - 65;
            float fontSize = 10;

            // draw rect
            RenderUtil.drawRect(pos.x - width / 2, startY - Southside.fontManager.font.getHeight(fontSize) - 5F, pos.x + width / 2 , startY + height, backgroundColor.getRGB());

//            RenderUtil.drawRect(pos.x - width / 2, startY - Southside.fontManager.font.getHeight(fontSize) - 5F, pos.x + width / 2, startY - height, backgroundColor.getRGB());
//            String str = chest.getLowerChestInventory().getDisplayName().getUnformattedText();
//            Southside.fontManager.font.drawString(fontSize, str, pos.x - (width / 2F) + (Southside.fontManager.font.getStringWidth(fontSize, str) * 0.5F), startY - (Southside.fontManager.font.getHeight(fontSize)) - 2.5F, Color.WHITE);

            float bar = 154F;
            RoundUtil.drawRound(pos.x - bar / 2, startY - 5F, bar * Stealer.progress.get() / Stealer.count, 2, 1, Color.WHITE);

            // render item
            GL11.glPushMatrix();
            GL11.glTranslated(pos.x - width / 2 - 52.5F, startY, 0);
            RenderHelper.enableGUIStandardItemLighting();


            for (int i1 = 0; i1 <= 5; i1++) {
                Slot slot = this.inventorySlots.inventorySlots.get(i1);
                ItemStack is = slot.getStack();
                this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, is, slot.xPos, -10);
//                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, is, i, j, s);
            }

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

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileBrewingStand.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        int k = this.tileBrewingStand.getField(1);
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);

        if (l > 0)
        {
            this.drawTexturedModalRect(i + 60, j + 44, 176, 29, l, 4);
        }

        int i1 = this.tileBrewingStand.getField(0);

        if (i1 > 0)
        {
            int j1 = (int)(28.0F * (1.0F - (float)i1 / 400.0F));

            if (j1 > 0)
            {
                this.drawTexturedModalRect(i + 97, j + 16, 176, 0, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];

            if (j1 > 0)
            {
                this.drawTexturedModalRect(i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
            }
        }
    }
}
