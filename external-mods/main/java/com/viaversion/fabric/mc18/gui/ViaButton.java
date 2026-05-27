package com.viaversion.fabric.mc18.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class ViaButton extends GuiButton {
    // Meant to be similar to higher versions TexturedButtonWidget
    private final int startU;
    private final int startV;
    private final int offsetHoverV;
    private final ResourceLocation texturePath;
    private final Consumer<ViaButton> onClick;

    public ViaButton(int id, int x, int y, int width, int height, int startU, int startV, int offsetHoverV, ResourceLocation texturePath,
                     int textureSizeX, int textureSizeY, Consumer<ViaButton> onClick, String altTxt) {
        super(id, x, y, width, height, altTxt);
        this.startU = startU;
        this.startV = startV;
        this.offsetHoverV = offsetHoverV;
        this.texturePath = texturePath;
        assert textureSizeX == 256;
        assert textureSizeY == 256;
        this.onClick = onClick;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(texturePath);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int v = startV;
            if (hover) {
                v += offsetHoverV;
            }
            this.drawTexturedModalRect(this.x, this.y, startU, v, this.width, this.height);
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        super.playPressSound(soundHandlerIn);
        onClick.accept(this);
    }
}
