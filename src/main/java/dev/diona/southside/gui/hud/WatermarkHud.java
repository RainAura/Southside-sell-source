package dev.diona.southside.gui.hud;

import cc.polyfrost.oneconfig.hud.Hud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import dev.diona.southside.Southside;

import java.awt.*;

public class WatermarkHud extends Hud {
    private float lastTextWidth = 0;
    private float lastTextHeight = 0;
    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        NanoVGHelper nanovg = NanoVGHelper.INSTANCE;
        nanovg.setupAndDraw(true, vg -> {
            final String tempClientName = Southside.CLIENT_NAME;
            lastTextWidth = nanovg.getTextWidth(vg, tempClientName, 10, Fonts.WQY) + 6;
            lastTextHeight = nanovg.getTextHeight(vg, 15, Fonts.WQY);
            float width = getWidth(scale, example);
            float height = getHeight(scale, example);
            nanovg.drawRect(vg, x, y, width, height, new Color(0, 0, 0, 100).getRGB());
            nanovg.drawRect(vg, x, (float) (y + 2.5 * scale), 1 * scale, height - 5f * scale, Color.WHITE.getRGB());
            nanovg.drawDropShadow(vg, x, (float) (y + 2.5 * scale), 1 * scale, height - 5f * scale, 3, 0.01F, 0, new Color(255, 255, 255, 255));
            nanovg.drawRawTextWithFormatting(vg, tempClientName, x + (width - lastTextWidth) / 2 + 4F * scale, y + (height - lastTextHeight) / 2 + 2.6F * scale, -1, 10, Fonts.WQY);
        });
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return lastTextWidth * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return lastTextHeight * scale;
    }
}
