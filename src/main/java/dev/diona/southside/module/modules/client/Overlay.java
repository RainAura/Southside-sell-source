//package dev.diona.southside.module.modules.client;
//
//import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import cc.polyfrost.oneconfig.config.options.impl.Switch;
//import dev.diona.southside.Southside;
//import dev.diona.southside.event.events.Render2DEvent;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.Module;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import dev.diona.southside.util.render.DrawUtil;
//import dev.diona.southside.util.render.renderer.NanoVGRenderer;
//import me.bush.eventbus.annotation.EventListener;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.FontRenderer;
//import net.minecraft.client.gui.ScaledResolution;
//
//import java.awt.*;
//import java.util.Comparator;
//import java.util.stream.Collectors;
//
//public class Overlay extends Module {
//    //private final ModeValue logoValue = new ModeValue("Logo","Jigsaw", "Jigsaw", "FoughtKnight");
//
//    @EventListener
//    public final void onRender2DEvent(final Render2DEvent event) {
//        final ScaledResolution scaledResolution = event.getSr();
//        final int screenRight = scaledResolution.getScaledWidth();
//        final int bottom = scaledResolution.getScaledHeight();
//        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
//
//        fontRenderer.drawStringWithShadow("Southside", 2, 2 , -1);
//        final var enabledModules = Southside.moduleManager.getModules().stream()
//                .filter(Module::isEnabled)
//                .filter(Module::isVisible)
//                .filter( m -> m.getCategory() != Category.Render)
//                .sorted((m1, m2) -> Double.compare(fontRenderer.getStringWidth(m2.formattedName()), fontRenderer.getStringWidth(m1.formattedName())))
//                .toList();
//        double lastModuleWidth = 0.0;
//
//        for (int i = 0, size = enabledModules.size(); i < size; i++) {
//            final Module module = enabledModules.get(i);
//
//            final String moduleName = module.formattedName();
//
//            final double nameHeight = fontRenderer.FONT_HEIGHT;
//            final double nameWidth = fontRenderer.getStringWidth(moduleName);
//            final double moduleSpacing = nameHeight + this.moduleSpacingProperty.getValue().doubleValue();
//
//            final double lineThickness = this.lineProperty.getValue() ? 1 : 0;
//            final double outlineThickness = this.outlineProperty.getValue() ? 1 : 0;
//
//            final int xBuffer = 2;
//
//            final boolean top = this.positionValue.getValue() == 0;
//
//            final double y = top ? i * moduleSpacing : bottom - (i + 1) * moduleSpacing;
//
//            int colour;
//            switch (colorValue.getValue()) {
//                case 0 -> colour = getRainbow(6000, (int) (y * 30), 0.85f);
//                case 1 -> colour = getRainbow(6000, (int) (y * 30), 0.55f);
//                case 2 -> colour = getGradientOffset(new Color(255, 60, 234), new Color(27, 179, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / 50)).getRGB();
//                case 3 -> colour = getGradientOffset(new Color(128, 171, 255), new Color(160, 72, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / 50)).getRGB();
//                case 4 -> colour = getGradientOffset(new Color(255, 129, 202), new Color(255, 15, 0), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / 50)).getRGB();
//                default -> colour = -1;
//            }
//
//            if (this.backgroundProperty.getValue()) {
//                final double backgroundLeft = screenRight - nameWidth - xBuffer * 2 - lineThickness;
//                final double backgroundWidth = nameWidth + xBuffer * 2 + lineThickness;
//
//                DrawUtil.glDrawFilledQuad(backgroundLeft, y, backgroundWidth, moduleSpacing,
//                        (int) (0xFF * (this.backgroundOpacityProperty.getValue().intValue() / 100.0)) << 24);
//            }
//
//            if (lineThickness != 0) {
//                if (top) {
//                    DrawUtil.glDrawFilledQuad(screenRight - lineThickness, y,
//                            lineThickness, moduleSpacing,
//                            colour);
//                } else {
//                    DrawUtil.glDrawFilledQuad(screenRight - lineThickness, y,
//                            lineThickness, moduleSpacing,
//                            colour);
//                }
//            }
//
//            if (outlineThickness != 0) {
//                // Left line
//                if (top) {
//                    DrawUtil.glDrawFilledQuad(screenRight - nameWidth - xBuffer * 2 - outlineThickness - lineThickness, y,
//                            outlineThickness, moduleSpacing,
//                            colour);
//                } else {
//                    DrawUtil.glDrawFilledQuad(screenRight - nameWidth - xBuffer * 2 - outlineThickness - lineThickness, y,
//                            outlineThickness, moduleSpacing,
//                            colour);
//                }
//
//                if (lastModuleWidth != 0) {
//                    if (lastModuleWidth - nameWidth > 0) {
//                        // Top line
//                        DrawUtil.glDrawFilledQuad(screenRight - lastModuleWidth - xBuffer * 2 - lineThickness - outlineThickness,
//                                top ? i * moduleSpacing : bottom - i * moduleSpacing,
//                                lastModuleWidth - nameWidth + outlineThickness, outlineThickness,
//                                colour);
//                    }
//                }
//
//                if (i == size - 1) {
//                    DrawUtil.glDrawFilledQuad(screenRight - nameWidth - xBuffer * 2 - lineThickness - outlineThickness, top ? y + moduleSpacing : y,
//                            nameWidth + lineThickness + outlineThickness + xBuffer * 2, outlineThickness,
//                            colour);
//                }
//            }
//
//            fontRenderer.drawStringWithShadow(moduleName,
//                    (float) (screenRight - nameWidth - xBuffer - lineThickness + .5),
//                    (float) (y + this.moduleSpacingProperty.getValue().floatValue() / 2.0),
//                    colour);
//
//            lastModuleWidth = nameWidth;
//        }
//        /*
//        final var modules = c.moduleManager.getModules();
//        NanoVGRenderer.INSTANCE.draw(vg -> {
//            final var fontSize = fontSizeValue.getValue().floatValue();
//            switch (logoValue.getValue()) {
//                case "Jigsaw" -> NanoVGRenderer.INSTANCE.drawImage(vg, "/assets/southside.png", 1f, 5, (float) 1361 / 10f, (float) 236 / 10f, -1, this.getClass());
//                case "FoughtKnight" -> NanoVGRenderer.Fonts.foughtKnight.drawString(vg, 25, "SOUTHSIDE", 5, 5, new Color(-1));
//            }
//
//            float y = mc.player.getActivePotionEffects().isEmpty() ? 3 : 28;
//            modules.sort(Comparator.comparingDouble(module -> -NanoVGRenderer.Fonts.jigsaw.getStringWidth(vg, fontSize, module.getName())));
//            modules.removeIf(module->!module.isEnabled() || !module.isVisible());
//            final var NAME_HEIGHT = NanoVGRenderer.Fonts.jigsaw.getHeight(vg, fontSize) + 4;
//            for (final var module : modules) {
//                int color;
//                switch (colorValue.getValue()) {
//                    case "Rainbow" -> color = getRainbow(6000, (int) (y * 30), 0.85f);
//                    case "LightRainbow" -> color = getRainbow(6000, (int) (y * 30), 0.55f);
//                    case "Astolfo" -> color = getGradientOffset(new Color(255, 60, 234), new Color(27, 179, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / ((NAME_HEIGHT + 4) / 2))).getRGB();
//                    case "Weird" -> color = getGradientOffset(new Color(128, 171, 255), new Color(160, 72, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / (NAME_HEIGHT / 2))).getRGB();
//                    case "Valentine" -> color = getGradientOffset(new Color(255, 129, 202), new Color(255, 15, 0), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / (NAME_HEIGHT / 2))).getRGB();
//                    default -> color = -1;
//                }
//                //final var textWidth = NanoVGRenderer.Fonts.jigsaw.getStringWidth(vg, fontSize, module.getName()) / 2;
//                //final var Xpos = event.getSr().getScaledWidth() - 3 - textWidth;
//                //NanoVGRenderer.INSTANCE.drawRect(vg, Xpos - textWidth - 3, y - 2, textWidth * 2 + 4, NanoVGRenderer.Fonts.jigsaw.getHeight(vg, fontSize) + 2, new Color(0, 0, 0, 80).getRGB());
//                //NanoVGRenderer.INSTANCE.drawRect(vg, Xpos + textWidth + 1 ,  y - 2 , 2, NanoVGRenderer.Fonts.jigsaw.getHeight(vg, fontSize) + 2, color);
//                //NanoVGRenderer.Fonts.jigsaw.drawString(vg, fontSize, module.getName(), Xpos - 2, y, new Color(color));
//                //NanoVGRenderer.INSTANCE.drawText(vg, module.getName(), Xpos - 2, y, color, fontSize, NanoVGRenderer.Fonts.jigsaw);
//                //y += NanoVGRenderer.Fonts.jigsaw.getHeight(vg, fontSize) + 2;
//            }
//        });
//
//         */
//    }
//
//    public static int getRainbow(int speed, int offset, float s) {
//        float hue = (System.currentTimeMillis() + offset) % speed;
//        hue /= speed;
//        return Color.getHSBColor(hue, s, 1f).getRGB();
//    }
//
//    public Color getGradientOffset(Color color1, Color color2, double offset) {
//        if (offset > 1) {
//            double left = offset % 1;
//            int off = (int) offset;
//            offset = off % 2 == 0 ? left : 1 - left;
//
//        }
//        double inverse_percent = 1 - offset;
//        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
//        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
//        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
//        return new Color(redPart, greenPart, bluePart);
//    }
//}
