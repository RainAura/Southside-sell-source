package com.mumfrey.liteloader.client.gui.startup;

import com.mumfrey.liteloader.common.LoadingProgress;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.GL11;

public class LoadingBar
        extends LoadingProgress {
    private static LoadingBar instance;
    private static final int MAX_MINECRAFT_PROGRESS = 90;
    private static final int LITELOADER_PROGRESS_SCALE = 2;
    private static final String LOADING_MESSAGE_1 = "Starting Game...";
    private static final String LOADING_MESSAGE_2 = "Initialising...";
    private int minecraftProgress = 0;
    private int totalMinecraftProgress = 90;
    private int liteLoaderProgress = 0;
    private int totalLiteLoaderProgress = 0;
    private ResourceLocation textureLocation = new ResourceLocation("textures/gui/title/mojang.png");
    private String minecraftMessage = "Starting Game...";
    private String message = "";
    private Minecraft minecraft;
    private TextureManager textureManager;
    private FontRenderer fontRenderer;
    private Framebuffer fbo;
    private boolean enabled = true;
    private boolean errored;
    private boolean calculatedColour = false;
    private int barLuma = 0;
    private int r2 = 246;
    private int g2 = 136;
    private int b2 = 62;
    private int logIndex = 0;
    private List<String> logTail = new ArrayList<String>();

    public LoadingBar() {
        instance = this;
    }

    @Override
    protected void _setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void _dispose() {
        this.minecraft = null;
        this.textureManager = null;
        this.fontRenderer = null;
        this.disposeFbo();
    }

    private void disposeFbo() {
        if (this.fbo != null) {
            this.fbo.deleteFramebuffer();
            this.fbo = null;
        }
    }

    public static void incrementProgress() {
        if (instance != null) {
            instance._incrementProgress();
        }
    }

    protected void _incrementProgress() {
        this.message = this.minecraftMessage;
        ++this.minecraftProgress;
        this.render();
    }

    public static void initTextures() {
        if (instance != null) {
            instance._initTextures();
        }
    }

    protected void _initTextures() {
        this.minecraftMessage = LOADING_MESSAGE_2;
    }

    @Override
    protected void _incLiteLoaderProgress() {
        this.liteLoaderProgress += 2;
        this.render();
    }

    @Override
    protected void _setMessage(String message) {
        this.message = message;
        this.render();
    }

    @Override
    protected void _incLiteLoaderProgress(String message) {
        this.message = message;
        this.liteLoaderProgress += 2;
        this.render();
    }

    @Override
    protected void _incTotalLiteLoaderProgress(int by) {
        this.totalLiteLoaderProgress += by * 2;
        this.render();
    }

    private void render() {
        if (!this.enabled || this.errored) {
            return;
        }
        try {
            if (this.minecraft == null) {
                this.minecraft = Minecraft.getMinecraft();
            }
            if (this.textureManager == null) {
                this.textureManager = this.minecraft.getTextureManager();
            }
            if (Display.isCreated() && this.textureManager != null) {
                if (this.fontRenderer == null) {
                    this.fontRenderer = new FontRenderer(this.minecraft.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.textureManager, false);
                    this.fontRenderer.onResourceManagerReload(this.minecraft.getResourceManager());
                }
                double totalProgress = this.totalMinecraftProgress + this.totalLiteLoaderProgress;
                double progress = (double)(this.minecraftProgress + this.liteLoaderProgress) / totalProgress;
                this.render(progress);
            }
        }
        catch (Exception ex) {
            this.errored = true;
        }
    }

    private void render(double progress) {
        if (this.totalMinecraftProgress == -1) {
            this.totalMinecraftProgress = 90 - this.minecraftProgress;
            this.minecraftProgress = 0;
        }
        if (!this.calculatedColour) {
            this.calculatedColour = true;
            ITextureObject texture = this.textureManager.getTexture(this.textureLocation);
            if (texture == null) {
                try {
                    DynamicTexture textureData = this.loadTexture(this.minecraft.getResourceManager(), this.textureLocation);
                    this.textureLocation = this.minecraft.getTextureManager().getDynamicTextureLocation("loadingScreen", textureData);
                    this.findMostCommonColour(textureData.getTextureData());
                    textureData.updateDynamicTexture();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        ScaledResolution scaledResolution = new ScaledResolution(this.minecraft);
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        int fboWidth = scaledWidth * scaleFactor;
        int fboHeight = scaledHeight * scaleFactor;
        if (this.fbo == null) {
            this.fbo = new Framebuffer(fboWidth, fboHeight, true);
        } else if (this.fbo.framebufferWidth != fboWidth || this.fbo.framebufferHeight != fboHeight) {
            this.fbo.createBindFramebuffer(fboWidth, fboHeight);
        }
        this.fbo.bindFramebuffer(false);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, scaledWidth, scaledHeight, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
        GL11.glClear(16640);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        this.textureManager.bindTexture(this.textureLocation);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        vertexBuffer.pos(0.0, (double)scaledHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        vertexBuffer.pos((double)scaledWidth, (double)scaledHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        vertexBuffer.pos((double)scaledWidth, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        vertexBuffer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int left = (scaledWidth - 256) / 2;
        int top = (scaledHeight - 256) / 2;
        int u1 = 0;
        int v1 = 0;
        int u2 = 256;
        int v2 = 256;
        float texMapScale = 0.00390625f;
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexBuffer.pos((double)(left + 0), (double)(top + v2), 0.0).tex((double)((float)(u1 + 0) * texMapScale), (double)((float)(v1 + v2) * texMapScale)).color(255, 255, 255, 255).endVertex();
        vertexBuffer.pos((double)(left + u2), (double)(top + v2), 0.0).tex((double)((float)(u1 + u2) * texMapScale), (double)((float)(v1 + v2) * texMapScale)).color(255, 255, 255, 255).endVertex();
        vertexBuffer.pos((double)(left + u2), (double)(top + 0), 0.0).tex((double)((float)(u1 + u2) * texMapScale), (double)((float)(v1 + 0) * texMapScale)).color(255, 255, 255, 255).endVertex();
        vertexBuffer.pos((double)(left + 0), (double)(top + 0), 0.0).tex((double)((float)(u1 + 0) * texMapScale), (double)((float)(v1 + 0) * texMapScale)).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableColorLogic();
        GL11.glLogicOp(5387);
        this.fontRenderer.drawString(this.message, 1, scaledHeight - 19, -16777216);
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
        double barHeight = 10.0;
        double barWidth = scaledResolution.getScaledWidth_double() - 2.0;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glBlendFunc(770, 771);
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION);
        float luma = (float)this.barLuma / 255.0f;
        GL11.glColor4f(luma, luma, luma, 0.5f);
        vertexBuffer.pos(0.0, (double)scaledHeight, 0.0).endVertex();
        vertexBuffer.pos(0.0 + (double)scaledWidth, (double)scaledHeight, 0.0).endVertex();
        vertexBuffer.pos(0.0 + (double)scaledWidth, (double)scaledHeight - barHeight, 0.0).endVertex();
        vertexBuffer.pos(0.0, (double)scaledHeight - barHeight, 0.0).endVertex();
        tessellator.draw();
        barHeight -= 1.0;
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        float r2 = (float)this.r2 / 255.0f;
        float g2 = (float)this.g2 / 255.0f;
        float b2 = (float)this.b2 / 255.0f;
        vertexBuffer.pos(1.0 + barWidth * progress, (double)(scaledHeight - 1), 1.0).color(r2, g2, b2, 1.0f).endVertex();
        vertexBuffer.pos(1.0 + barWidth * progress, (double)scaledHeight - barHeight, 1.0).color(r2, g2, b2, 1.0f).endVertex();
        vertexBuffer.pos(1.0, (double)scaledHeight - barHeight, 1.0).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.pos(1.0, (double)(scaledHeight - 1), 1.0).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex();
        tessellator.draw();
        GL11.glAlphaFunc(516, 0.1f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        this.fbo.unbindFramebuffer();
        this.fbo.framebufferRender(fboWidth, fboHeight);
        GlStateManager.enableAlpha();
        GL11.glAlphaFunc(516, 0.1f);
        this.minecraft.updateDisplay();
    }

    private void findMostCommonColour(int[] textureData) {
        int paletteIndex;
        int[] freq = new int[512];
        for (int pos = 0; pos < textureData.length; ++pos) {
            int n = paletteIndex = ((textureData[pos] >> 21 & 7) << 6) + ((textureData[pos] >> 13 & 7) << 3) + (textureData[pos] >> 5 & 7);
            freq[n] = freq[n] + 1;
        }
        int peak = 0;
        for (paletteIndex = 2; paletteIndex < 511; ++paletteIndex) {
            if (freq[paletteIndex] <= peak) continue;
            peak = freq[paletteIndex];
            this.setBarColour(paletteIndex);
        }
    }

    private void setBarColour(int paletteIndex) {
        this.r2 = this.padComponent((paletteIndex & 0x1C0) >> 1);
        this.g2 = this.padComponent((paletteIndex & 0x38) << 2);
        this.b2 = this.padComponent((paletteIndex & 7) << 5);
        this.barLuma = Math.max(this.r2, Math.max(this.g2, this.b2)) < 64 ? 255 : 0;
    }

    private int padComponent(int component) {
        return component > 31 ? component | 0x1F : component;
    }

    private DynamicTexture loadTexture(IResourceManager resourceManager, ResourceLocation textureLocation) throws IOException {
        InputStream inputStream = null;

        DynamicTexture var6;
        try {
            IResource resource = resourceManager.getResource(textureLocation);
            inputStream = resource.getInputStream();
            BufferedImage image = ImageIO.read(inputStream);
            var6 = new DynamicTexture(image);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return var6;
    }
}
