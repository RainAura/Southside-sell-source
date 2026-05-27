package dev.diona.southside.util.render.glyph;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public enum GlyphFontManager {
    INSTANCE;
    public GlyphFontRenderer tohoma19;
    public GlyphFontRenderer msyh30;
    public final void initialize() {
        tohoma19 = new GlyphFontRenderer(new Font("Tohoma", Font.PLAIN, 19));
        msyh30 = new GlyphFontRenderer(fontFromTTF(new File("C:\\Windows\\Fonts\\msyh.ttc"), 30, Font.PLAIN));
    }

    public static Font fontFromTTF(File file, float fontSize, int fontType) {
        Font output = null;
        try {
            output = Font.createFont(fontType, new FileInputStream(file));
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
