package com.netease.mc.mod.departmod.coremod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjglx.LWJGLException;

public class NewDrawSplashScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static ResourceLocation mojangLogo;
    private static final ResourceLocation LOCATION_MOJANG_PNG;
    private static final ResourceLocation LOCATION_URL_PNG;
    private static final Logger LOGGER;

    public static void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i = scaledresolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, NewDrawSplashScreen.mc.displayHeight, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(NewDrawSplashScreen.mc.displayWidth, NewDrawSplashScreen.mc.displayHeight, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(NewDrawSplashScreen.mc.displayWidth, 0.0, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        textureManagerInstance.bindTexture(LOCATION_MOJANG_PNG);
        int mojangPicWidth = 516 / i;
        int mojangPicHeight = 152 / i;
        Gui.drawModalRectWithCustomSizedTexture((scaledresolution.getScaledWidth() - mojangPicWidth) / 2, (scaledresolution.getScaledHeight() - mojangPicHeight) / 2, 0.0f, 0.0f, mojangPicWidth, mojangPicHeight, (float)mojangPicWidth, (float)mojangPicHeight);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        mc.updateDisplay();
    }

    static {
        LOCATION_MOJANG_PNG = new ResourceLocation("depart/textures/gui/mojang_new.png");
        LOCATION_URL_PNG = new ResourceLocation("depart/textures/gui/url.png");
        LOGGER = LogManager.getLogger();
    }
}
