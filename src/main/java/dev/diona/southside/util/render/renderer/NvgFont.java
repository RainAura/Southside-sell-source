package dev.diona.southside.util.render.renderer;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.TextEvent;
import dev.diona.southside.util.misc.FileUtil;
import dev.diona.southside.util.misc.TextUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static dev.diona.southside.Southside.MC.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class NvgFont {
    private final String fontName;
    private final int[] colorCode = new int[32];

    private final String fileName;
    private boolean loaded = false;
    private ByteBuffer buffer = null;

    public NvgFont(final String fontName, final String fileName) {
        this.fontName = fontName;
        this.fileName = fileName;
        this.setupMinecraftColorcodes();
    }

    public void loadFont(long vg) {
        if (this.isLoaded()) return;
        int loaded = -1;
        try {
            ByteBuffer buffer = FileUtil.resourceToByteBuffer(this.getFileName(), this.getClass());
            loaded = nvgCreateFontMem(vg, this.fontName, buffer, true);
            this.setBuffer(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (loaded == -1) {
            throw new RuntimeException("Failed to initialize font " + this.fontName);
        } else {
            this.setLoaded(true);
        }
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

    public void drawStringS(final long nvg, float fontSize, String str, float x, float y, Color color) {
        TextEvent textEvent = new TextEvent(str);
        Southside.eventBus.post(textEvent);
        String text = textEvent.getText();

        float offset = 0F;
        Color currentColor = color;
        for (int i = 0; i < text.length(); i++) {
            String s = text.substring(i, i + 1);
            if (s.equals("§") && i + 1 < text.length()) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex != -1) {
                    i++;
                    continue;
                }
            }
            this.drawUnformattedString(nvg, fontSize, s, x + offset, y, currentColor);
            offset += this.getStringWidth(nvg, fontSize, s);
        }
    }

    public void drawString(final long vg, float fontSize, String str, float x, float y, Color color) {
        TextEvent textEvent = new TextEvent(str);
        Southside.eventBus.post(textEvent);
        String text = textEvent.getText();

        float offset = 0F;
        Color currentColor = color;
        for (int i = 0; i < text.length(); i++) {
            String s = text.substring(i, i + 1);
            if (s.equals("§") && i + 1 < text.length()) {
                String format = text.substring(i + 1, i + 2);
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
            this.drawUnformattedString(vg, fontSize, s, x + offset, y, currentColor);
            offset += this.getStringWidth(vg, fontSize, s);
        }
    }

    public void drawUnformattedString(final long nvg, float fontSize, String text, float x, float y, Color color) {
        nvgBeginPath(nvg);
        nvgFontSize(nvg, fontSize);
        nvgFontFace(nvg, fontName);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(nvg, convert(color));
        nvgText(nvg, x, y, text);
        nvgClosePath(nvg);
    }

    public void drawCenteredString(final long nvg, float fontSize, String text, float x, float y, Color color) {
        this.drawString(nvg, fontSize, text, x - this.getStringWidth(nvg, fontSize, text) / 2F, y, color);
    }

    public void drawStringWithShadow(final long nvg, final float fontSize, final String text, final float x, final float y, Color color) {
        final var o_x = x - this.getStringWidth(nvg, fontSize, text) / 2F;
        this.drawStringS(nvg, fontSize, text, o_x + 0.5f, y + 0.5f, new Color(0, 0, 0));
        this.drawString(nvg, fontSize, text, o_x, y, color);
    }

    public float getStringWidth(final long nvg, float fontSize, String str) {
        TextEvent textEvent = new TextEvent(str);
        Southside.eventBus.post(textEvent);
        String text = textEvent.getText();

        float width = 0F;
        for (int i = 0; i < text.length(); i++) {
            final var s = String.valueOf(text.charAt(i));
            if (s.equals("§") && i + 1 < text.length()) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex != -1) {
                    continue;
                }
            }
            width += this.getCharWidth(nvg, fontSize, s);
        }
        return width;
    }

    public float getCharWidth(final long nvg, float fontSize, String text) {
        text = TextUtil.removeFormattingCodes(text);
        if (text.isEmpty()) return 0F;
        float[] bounds = new float[4];
        nvgFontSize(nvg, fontSize);
        nvgFontFace(nvg, fontName);
        return nvgTextBounds(nvg, 0, 0, text, bounds);
    }

    private NVGColor convert(Color color) {
        NVGColor nvgColor = NVGColor.calloc();
        nvgColor.r(color.getRed() / 255.0f);
        nvgColor.g(color.getGreen() / 255.0f);
        nvgColor.b(color.getBlue() / 255.0f);
        nvgColor.a(color.getAlpha() / 255.0f);
        return nvgColor;
    }

    public float getHeight(final long nvg, float size) {
//        nvgFontSize(nvg, size * sr.getScaleFactor());
        nvgFontFace(nvg, fontName);
        float[] ascender = new float[1];
        float[] descender = new float[1];
        float[] lineh = new float[1];
        nvgFontSize(nvg, size);
        nvgTextMetrics(nvg, ascender, descender, lineh);
        return lineh[0];
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}