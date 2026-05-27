package dev.diona.southside.gui.hud;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import cc.polyfrost.oneconfig.hud.Hud;
import cc.polyfrost.oneconfig.internal.gui.HudGui;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.libs.universal.UResolution;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.renderer.scissor.Scissor;
import cc.polyfrost.oneconfig.renderer.scissor.ScissorHelper;
import dev.diona.southside.Southside;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.render.ChromaJS;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static dev.diona.southside.Southside.MC.mc;

public class ArrayListHud extends Hud {
    public final Switch noRender = new Switch("No Render Modules", true);
    public final Switch background = new Switch("Background", true);
    public final Dropdown colorValue = new Dropdown("Color", new String[]{
            "Custom",
            "Rainbow",
            "LightRainbow",
            "Astolfo",
            "Weird",
            "Valentine"
    }, "Choose which page will show when you open OneConfig", 5, 1);

    public final cc.polyfrost.oneconfig.config.options.impl.Color customColor1 = new cc.polyfrost.oneconfig.config.options.impl.Color(
            "Custom Color A",
            new OneColor(Color.WHITE)
    );

    public final cc.polyfrost.oneconfig.config.options.impl.Color customColor2 = new cc.polyfrost.oneconfig.config.options.impl.Color(
            "Custom Color B",
            new OneColor(Color.WHITE)
    );

    private ChromaJS.Scale rainbow = null;

    public final Slider customSpeed = new Slider(
            "Custom Speed",
            10D, 5D, 20D
    );

    public ArrayListHud(float x, float y, int positionAlignment, float scale) {
        super(x, y, positionAlignment, scale);
    }

    private CopyOnWriteArrayList<ArrayListModule> enabledModules = new CopyOnWriteArrayList<>();
    private HashMap<Module, ArrayListModule> modules = new HashMap<>();

    private float lastScale = 1;
    private float lastHeight = 50;
    private float lastWidth = 50;

    public void reloadRainbow() {
        rainbow = new ChromaJS.Scale(customColor1.getValue().toJavaColor(), customColor2.getValue().toJavaColor(), 8);
    }

    public void syncModules() {
        float fontSize = 20 * 0.3f * lastScale;
        if (modules.isEmpty()) {
            for (int i = 0; i < Southside.moduleManager.getModules().size(); i++) {
                final var module = Southside.moduleManager.getModules().get(i);
                modules.put(module, new ArrayListModule(this, module, -1));
            }
        }
        enabledModules = new CopyOnWriteArrayList<>();
        for (Module module : Southside.moduleManager.getModules()) {
            if (shouldDisplay(module)) {
                enabledModules.add(modules.get(module));
            }
        }
        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
//        int reverse = switch (position.getValue().anchor) {
//            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> -1;
//            default -> 1;
//        };
        nanoVGHelper.setupAndDraw(true, vg -> {
            enabledModules.sort((o1, o2) -> (int) (10000F * nanoVGHelper.getTextWidth(vg, Southside.moduleManager.formatRaw(o2.module), fontSize, Fonts.WQY) - 10000F * nanoVGHelper.getTextWidth(vg, Southside.moduleManager.formatRaw(o1.module), fontSize, Fonts.WQY)));
        });
    }

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        if (rainbow == null) {
            this.reloadRainbow();
        }
        this.syncModules();
        lastScale = scale;
        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        ScissorHelper scissorHelper = ScissorHelper.INSTANCE;
        AtomicBoolean updatedSize = new AtomicBoolean(false);
        AtomicReference<Float> thisHeight = new AtomicReference<>(0f);
        AtomicReference<Float> thisWidth = new AtomicReference<>(0f);
        nanoVGHelper.setupAndDraw(true, vg -> {
            float fontSize = 20 * 0.3F * scale;

            float moduleY = 0;
            int reverse = switch (position.getValue().anchor) {
                case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> -1;
                default -> 1;
            };
            int reverseX = switch (position.getValue().anchor) {
                case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> -1;
                default -> 1;
            };
            Scissor scissor = switch (position.getValue().anchor) {
                case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> scissorHelper.scissor(vg, x, 0, UResolution.getScaledWidth(), UResolution.getScaledHeight());
                default -> scissorHelper.scissor(vg, 0, 0, x + this.getWidth(scale, example), UResolution.getScaledHeight());
            };
            for (ArrayListModule module : enabledModules) {
//                final var font = Southside.fontManager.roboto;
                module.y.update(reverse * moduleY);
                module.y.freeze();
                module.x.update(0);
                float width = module.draw(vg, x, y, background.getValue(), scale);
                moduleY += nanoVGHelper.getTextHeight(vg, fontSize, Fonts.WQY) + 10 * 0.3F * scale;
                updatedSize.set(true);
                thisWidth.set(Math.max(thisWidth.get(), width));
            }

            thisHeight.set(moduleY);

            for (ArrayListModule module : modules.values()) {
                if (shouldDisplay(module.module)) continue;
                module.y.update(0);
                module.x.update(scale * reverseX * 110 * 0.3F);
                module.draw(vg, x, y, background.getValue(), scale);
            }
            scissorHelper.resetScissor(vg, scissor);
        });

        if (updatedSize.get()) {
            if (mc.currentScreen instanceof HudGui hudGui && hudGui.isScaling) return;
            lastHeight = thisHeight.get() / scale;
            lastWidth = thisWidth.get() / scale;
        }
    }

    private boolean shouldDisplay(Module module) {
        return module.isEnabled() && module.isVisible() && (module.getCategory() != Category.Render || !noRender.getValue());
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return lastWidth * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return lastHeight * scale;
    }

    public int getRainbow(int speed, int offset, float s) {
        float hue = (float) ((System.currentTimeMillis() / 10 * customSpeed.getValue().doubleValue() + offset) % speed);
        hue /= speed;
        return Color.getHSBColor(hue, s, 1f).getRGB();
    }

    public Color getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            long off = (long) offset;
            offset = off % 2 == 0 ? left : 1 - left;

        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public int getCustomOffset(double offset) {
        double prev = offset;
        if (offset > 1) {
            double left = offset % 1;
            long off = (long) offset;
            offset = off % 2 == 0 ? left : 1 - left;

        }
        return rainbow.getColorRGB(offset);
    }

    public static class ArrayListModule {
        public ArrayListHud parent;
        public Module module;
        private final int color;
        public BezierUtil x = new BezierUtil(3, 0), y = new BezierUtil(3, 0);

        public ArrayListModule(ArrayListHud parent, Module module, int color) {
            this.parent = parent;
            this.module = module;
            this.color = color;
        }

        public float draw(long vg, float targetX, float targetY, boolean background, float scale) {
            float fontSize = 20 * 0.3F * scale;
            int reverseX =  switch (parent.position.getValue().anchor) {
                case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> -1;
                default -> 1;
            };
            if (reverseX == 1 && (x.get() >= 100 * 0.3F * scale)) return 0f;
            if (reverseX == -1 && (x.get() <= -100 * 0.3F * scale)) return 0f;
            NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
            float moduleWidth = nanoVGHelper.getTextWidth(vg, Southside.moduleManager.formatRaw(module), fontSize, Fonts.WQY);
            float xPos = targetX + x.get() - moduleWidth;
            float yPos = targetY + y.get();

            float fontHeight = nanoVGHelper.getTextHeight(vg, fontSize, Fonts.WQY);
            float height = fontHeight + 10 * 0.3F * scale;

            switch (parent.position.getValue().anchor) {
                case TOP_LEFT, MIDDLE_LEFT -> {
                    xPos += moduleWidth + 7F * 0.3F * scale;
                }
                case BOTTOM_LEFT -> {
                    xPos += moduleWidth + 7F * 0.3F * scale;
                    yPos += parent.getHeight(scale, false) - height;
                }
                case TOP_RIGHT, MIDDLE_RIGHT, TOP_CENTER, MIDDLE_CENTER -> {
                    xPos += parent.getWidth(scale, false);
                }
                case BOTTOM_RIGHT, BOTTOM_CENTER -> {
                    xPos += parent.getWidth(scale, false);
                    yPos += parent.getHeight(scale, false) - height;
                }
            }
            if (background) {
                nanoVGHelper.drawRect(vg, xPos - 7F * 0.3F * scale, yPos, moduleWidth + 7F * 0.3F * scale, height, new Color(32, 32, 32, 100).getRGB());
//                RenderUtil.drawRect(xPos - 3, yPos - 2F, x.get() + width - 2F, yPos + height - 2F, new Color(32, 32, 32, 100).getRGB());
                //RenderUtil.drawRect(x.get() + width - 2F, yPos - 1F, x.get() + width, yPos + height - 1.5, color.getRGB());
            }

//            RenderUtil.drawRect(xPos, (yPos - Southside.fontManager.font.getHeight(fontSize) / 2F + height / 2F - 2F) + hud.moduleOffsetValue.getValue().floatValue(), xPos + Southside.fontManager.font.getStringWidth(fontSize, Southside.moduleManager.format(module)), (yPos - Southside.fontManager.font.getHeight(fontSize) / 2F + height / 2F - 2F) + hud.moduleOffsetValue.getValue().floatValue() + 2F, -1);
            float maybeY = yPos + height / 2F + 1 * 0.3F * scale;
            int colour;
            switch (parent.colorValue.getValue()) {
                case 0 -> colour = parent.getCustomOffset((Math.abs(((System.currentTimeMillis()) / 100D * parent.customSpeed.getValue().doubleValue())) / 100D) + (maybeY / 50));
                case 1 -> colour = parent.getRainbow(6000, (int) (maybeY * 30), 0.85f);
                case 2 -> colour = parent.getRainbow(6000, (int) (maybeY * 30), 0.55f);
                case 3 -> colour = parent.getGradientOffset(new Color(255, 60, 234), new Color(27, 179, 255), (Math.abs(((System.currentTimeMillis()) / 100D * parent.customSpeed.getValue().doubleValue())) / 100D) + (maybeY / 50)).getRGB();
                case 4 -> colour = parent.getGradientOffset(new Color(128, 171, 255), new Color(160, 72, 255), (Math.abs(((System.currentTimeMillis()) / 100D * parent.customSpeed.getValue().doubleValue())) / 100D) + (maybeY / 50)).getRGB();
                case 5 -> colour = parent.getGradientOffset(new Color(255, 129, 202), new Color(255, 15, 0), (Math.abs(((System.currentTimeMillis()) / 100D * parent.customSpeed.getValue().doubleValue())) / 100D) + (maybeY / 50)).getRGB();
                default -> colour = -1;
            }
            nanoVGHelper.drawTextWithFormatting(vg, Southside.moduleManager.format(module), xPos - 2F * 0.3F * scale, maybeY, colour, fontSize, Fonts.WQY);

            return moduleWidth + 7F * 0.3F * scale;
        }
    }
}
