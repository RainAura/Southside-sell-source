package dev.diona.southside.managers;

import dev.diona.southside.Southside;
import dev.diona.southside.gui.font.NvgFontRenderer;

public class FontManager {
    public final NvgFontRenderer font;
    public final NvgFontRenderer zhijun;
    public final NvgFontRenderer thin;
    public final NvgFontRenderer tahomabd;
    public final NvgFontRenderer roboto;

    public final NvgFontRenderer wqy_microhei;
    public FontManager() {
        long time = System.currentTimeMillis();
        font = new NvgFontRenderer(RenderManager.vg, "harmony");
        thin = new NvgFontRenderer(RenderManager.vg, "harmony_thin");
        tahomabd = new NvgFontRenderer(RenderManager.vg, "tahomabd");
        wqy_microhei = new NvgFontRenderer(RenderManager.vg, "wqy_microhei");
        roboto = new NvgFontRenderer(RenderManager.vg, "Roboto-Regular");
        zhijun = new NvgFontRenderer(RenderManager.vg, "baloo");
        Southside.LOGGER.info("Loaded fonts in: " + (System.currentTimeMillis() - time) + "ms");
    }
}
