package dev.diona.southside.gui.font;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.TextEvent;
import dev.diona.southside.managers.RenderManager;
import dev.diona.southside.util.misc.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGTextRow;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static dev.diona.southside.Southside.MC.mc;
import static dev.diona.southside.managers.RenderManager.sr;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class NvgFontRenderer {
    private final long nvg;
    private final String fontName;
    private final int[] colorCode = new int[32];

    public NvgFontRenderer(long nvg, String fontName) {
        this.nvg = nvg;
        this.fontName = fontName;
        this.loadFont();
        this.setupMinecraftColorcodes();
    }

    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index >> 0 & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }

    public void loadFont() {
        try (InputStream inputStream = mc.getResourceManager().getResource(new ResourceLocation("southside/fonts/" + fontName + ".ttf")).getInputStream()) {
            assert inputStream != null : "Error: Could not open file for font: '" + fontName + "'";
            byte[] fontData = inputStream.readAllBytes();
            ByteBuffer fontDataBuffer = MemoryUtil.memAlloc(fontData.length + 1);
            fontDataBuffer.put(fontData).put((byte) 0).flip();

            ByteBuffer fontNameData = MemoryUtil.memUTF8(fontName);
            if (nvgCreateFontMem(nvg, fontNameData, fontDataBuffer, true) == -1) {
                throw new IOException("Failed to load font: " + fontName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawStringS(float fontSize, String str, float x, float y, Color color) {
        TextEvent textEvent = new TextEvent(str);
        Southside.eventBus.post(textEvent);
        String text = textEvent.getText();

        float offset = 0F;
        Color currentColor = color;
        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.equals("§") && i + 1 < text.length()) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex != -1) {
                    i++;
                    continue;
                }
            }
            this.drawUnformattedString(fontSize, s, x + offset, y, currentColor);
            offset += this.getStringWidth(fontSize, s);
        }
    }

    public void drawString(float fontSize, String str, float x, float y, Color color) {
        TextEvent textEvent = new TextEvent(str);
        Southside.eventBus.post(textEvent);
        String text = textEvent.getText();

        float offset = 0F;
        Color currentColor = color;
        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.equals("§") && i + 1 < text.length()) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex != -1) {
                    switch (colorIndex) {
                        case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 -> {
                            currentColor = new Color(colorCode[colorIndex]);
                        }
                        case 21 -> {
                            currentColor = color;
                        }
                    }
                    i++;
                    continue;
                }
            }
            this.drawUnformattedString(fontSize, s, x + offset, y, currentColor);
            offset += this.getStringWidth(fontSize, s);
        }
    }

    public void drawUnformattedString(float fontSize, String text, float x, float y, Color color) {
        nvgBeginPath(nvg);
        nvgFontSize(nvg, fontSize * sr.getScaleFactor());
        nvgFontFace(nvg, fontName);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(nvg, convert(color));
        nvgText(nvg, x * sr.getScaleFactor(), y * sr.getScaleFactor(), text);
        nvgClosePath(nvg);
        GlStateManager.bindTexture(0);
        GlStateManager.resetColor();
    }

    public void drawCenteredString(float fontSize, String text, float x, float y, Color color) {
        this.drawString(fontSize, text, x - this.getStringWidth(fontSize, text) / 2F, y, color);
    }

    public final void drawStringWithShadow(final float fontSize, final String text, final float x, final float y, Color color) {
        final var o_x = x - this.getStringWidth(fontSize, text) / 2F;
        final var o_y = y;
        this.drawStringS(fontSize, text, o_x + 0.5f, o_y + 0.5f, new Color(0, 0, 0));
        this.drawString(fontSize, text, o_x, o_y, color);
    }

    public float getStringWidth(float fontSize, String str) {
        TextEvent textEvent = new TextEvent(str);
        Southside.eventBus.post(textEvent);
        String text = textEvent.getText();

        float width = 0F;
        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.equals("§") && i + 1 < text.length()) {
                String format = text.substring(i + 1, i + 2);
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex != -1) {
                    continue;
                }
            }
            width += this.getCharWidth(fontSize, s);
        }
        return width;
    }

    public float getCharWidth(float fontSize, String text) {
        text = TextUtil.removeFormattingCodes(text);
        if (text.isEmpty()) return 0F;
        nvgFontFace(nvg, fontName);
        nvgFontSize(nvg, fontSize * sr.getScaleFactor());
        return nvgTextBounds(nvg, 0f, 0f, text, (FloatBuffer) null) / sr.getScaleFactor();
    }

    private NVGColor convert(Color color) {
        NVGColor nvgColor = NVGColor.calloc();
        nvgColor.r(color.getRed() / 255.0f);
        nvgColor.g(color.getGreen() / 255.0f);
        nvgColor.b(color.getBlue() / 255.0f);
        nvgColor.a(color.getAlpha() / 255.0f);
        return nvgColor;
    }

    public float getHeight(float size) {
//        nvgFontSize(nvg, size * sr.getScaleFactor());
        nvgFontFace(nvg, fontName);
        float[] ascender = new float[1];
        float[] descender = new float[1];
        float[] lineh = new float[1];
        nvgFontSize(nvg, size * sr.getScaleFactor());
        nvgTextMetrics(nvg, ascender, descender, lineh);
        return lineh[0] / sr.getScaleFactor();
    }
}