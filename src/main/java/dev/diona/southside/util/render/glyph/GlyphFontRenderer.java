package dev.diona.southside.util.render.glyph;

import dev.diona.southside.util.render.glyph.cache.IBFFontRenderer;
import dev.diona.southside.util.render.glyph.cache.StringCache;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class GlyphFontRenderer implements IBFFontRenderer {
    /**
     * the height in pixels of default text
     */
    public int FONT_HEIGHT = 8;
    public int[] colorCode = new int[32];
    private StringCache stringCache;

    public GlyphFontRenderer(final Font font) {
        for(int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;
            if (i == 6) {
                k += 85;
            }

            if (Minecraft.getMinecraft().gameSettings.anaglyph) {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
        if (this.getStringCache() == null) {
            this.setStringCache(new StringCache(colorCode));
            this.getStringCache().setDefaultFont(font.getFontName(), font.getSize(), true);
        }
    }

    public void drawString(String string, float x, float y, int color) {
        stringCache.renderString(string, x, y, color,false);
    }

    public void drawStringWithShadow(String string, float x, float y, int color) {
        stringCache.renderString(string, x, y, color,true);
    }

    public float getStringWidth(String string) {
        return stringCache.getStringWidth(string);
    }


    public float getHeight() {
        return FONT_HEIGHT;
    }

    @Override
    public StringCache getStringCache() {
        return stringCache;
    }

    @Override
    public void setStringCache(StringCache value) {
        stringCache = value;
    }

    @Override
    public boolean isDropShadowEnabled() {
        return false;
    }

    @Override
    public void setDropShadowEnabled(boolean value) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean value) {

    }
}
