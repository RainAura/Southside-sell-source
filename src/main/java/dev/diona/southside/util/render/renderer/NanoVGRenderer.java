package dev.diona.southside.util.render.renderer;

import dev.diona.southside.util.misc.FileUtil;
import dev.diona.southside.util.misc.nvgasset.NanoVGAssetHelperImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.function.LongConsumer;

import static dev.diona.southside.Southside.MC.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public enum NanoVGRenderer {
    INSTANCE;

    private long vg = -1;

    public void draw(final boolean mcScaling, final LongConsumer consumer) {
        if (vg == -1) {
            vg = nvgCreate(NVG_ANTIALIAS);
            if (vg == -1) {
                throw new RuntimeException("创建nanovg上下文失败, 滚");
            }
            Fonts.initialize(vg);
        }

        GlStateManager.disableAlpha();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        final var resolution = new ScaledResolution(Minecraft.getMinecraft());
        if (mcScaling) {
            nvgBeginFrame(vg, (float) resolution.getScaledWidth_double(), (float) resolution.getScaledHeight_double(), resolution.getScaleFactor());
        } else {
            nvgBeginFrame(vg, Display.getWidth(), Display.getHeight(), 1);
        }
        consumer.accept(vg);
        nvgEndFrame(vg);
        GL11.glPopAttrib();
        GlStateManager.enableAlpha();
    }

    public void draw(LongConsumer consumer) {
        draw(true, consumer);
    }

    public void drawRect(long vg, float x, float y, float width, float height, int color) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        final var nvgColor = color(vg, color);
        nvgFill(vg);
        nvgClosePath(vg);
        nvgColor.free();
    }

    public void drawRoundedRect(long vg, float x, float y, float width, float height, int color, float radius) {
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, radius);
        NVGColor nvgColor = color(vg, color);
        nvgFill(vg);
        nvgColor.free();
    }

    public void drawGradientRoundedRect(long vg, float x, float y, float width, float height, int color, int color2, float radius) {
        NVGPaint bg = NVGPaint.create();
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, radius);
        NVGColor nvgColor = color(vg, color);
        NVGColor nvgColor2 = color(vg, color2);
        nvgFillPaint(vg, nvgLinearGradient(vg, x, y, x + width, y, nvgColor, nvgColor2, bg));
        nvgFill(vg);
        nvgColor.free();
        nvgColor2.free();
    }

    public void drawCircle(long vg, float x, float y, float radius, int color) {
        nvgBeginPath(vg);
        nvgCircle(vg, x, y, radius);
        NVGColor nvgColor = color(vg, color);
        nvgFill(vg);
        nvgColor.free();
    }

    public NVGColor color(long vg, int color) {
        final var nvgColor = NVGColor.calloc();
        nvgRGBA((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF), nvgColor);
        nvgFillColor(vg, nvgColor);
        return nvgColor;
    }

    public void drawImage(long vg, String filePath, float x, float y, float width, float height, int color, Class<?> clazz) {
        final var assetHelper = NanoVGAssetHelperImpl.INSTANCE;
        if (assetHelper.loadImage(vg, filePath, clazz)) {
            NVGPaint imagePaint = NVGPaint.calloc();
            int image = assetHelper.getImage(filePath);
            nvgBeginPath(vg);
            nvgImagePattern(vg, x, y, width, height, 0, image, 1, imagePaint);
            drawImageCommon(vg, x, y, width, height, color, imagePaint);
            imagePaint.free();
        }
    }
    private void drawImageCommon(long vg, float x, float y, float width, float height, int color, NVGPaint imagePaint) {
        nvgRGBA((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF), imagePaint.innerColor());
        nvgRect(vg, x, y, width, height);
        nvgFillPaint(vg, imagePaint);
        nvgFill(vg);
    }

    /*
    public void drawText(long vg, String text, float x, float y, int color, float size, Fonts.Font font) {
        nvgBeginPath(vg);
        nvgFontSize(vg, size);
        nvgFontFace(vg, font.getName());
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        NVGColor nvgColor = color(vg, color);
        nvgText(vg, x, y, text);
        nvgColor.free();
    }

    public float getTextWidth(long vg, String text, float fontSize, Fonts.Font font) {
        float[] bounds = new float[4];
        nvgFontSize(vg, fontSize);
        nvgFontFace(vg, font.getName());
        return nvgTextBounds(vg, 0, 0, text, bounds);
    }

    public float getHeight(long vg, float size, Fonts.Font font) {
        nvgFontFace(vg, font.getName());
        float[] ascender = new float[1];
        float[] descender = new float[1];
        float[] lineh = new float[1];
        nvgFontSize(vg, size);
        nvgTextMetrics(vg, ascender, descender, lineh);
        return lineh[0];
    }


     */
    public static class Fonts {
        public static final NvgFont wqy_microhei = new NvgFont("wqy_microhei", "/assets/minecraft/southside/fonts/wqy_microhei.ttf");
        public static final NvgFont jigsaw = new NvgFont("jigsaw", "/assets/minecraft/southside/fonts/Jigsaw-Regular.otf");
        public static final NvgFont foughtKnight = new NvgFont("foughtKnight", "/assets/minecraft/southside/fonts/FoughtKnight.ttf");
        public static void initialize(long vg) {
            wqy_microhei.loadFont(vg);
            jigsaw.loadFont(vg);
            foughtKnight.loadFont(vg);
        }
    }
}
