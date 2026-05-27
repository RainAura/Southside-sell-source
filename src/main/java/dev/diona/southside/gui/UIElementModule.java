//package dev.diona.southside.gui;
//
//import dev.diona.southside.Southside;
//import dev.diona.southside.event.EventState;
//import dev.diona.southside.event.events.Bloom2DEvent;
//import dev.diona.southside.gui.click.ClickGuiScreen;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.Module;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import dev.diona.southside.util.render.RenderUtil;
//import net.minecraft.client.gui.GuiChat;
//import org.lwjglx.input.Mouse;
//
//import java.awt.*;
//
//import static dev.diona.southside.Southside.MC.mc;
//
//public abstract class UIElementModule extends Module {
//    protected final Slider xValue;
//    protected final Slider yValue;
//    private final String displayName;
//    private boolean dragging, drag, lastLeft, lastRight;
//    private float dragX, dragY;
//
//    public UIElementModule(String name, String description, Category category, boolean visible, String displayName, double defaultX, double defaultY) {
//        super(name, description, category, visible);
//        this.displayName = displayName;
//        yValue = new Slider("X", defaultX, -0, 0, 0.1);
//        xValue = new Slider("Y", defaultY, 0, 0, 0.1);
//        xValue.setDisplay(() -> false);
//        yValue.setDisplay(() -> false);
//        this.getValues().add(xValue);
//        this.getValues().add(yValue);
//    }
//
//    public void onUIElementBloom(Bloom2DEvent event) {
//        if (event.getState() == EventState.PRE) {
//
//            if (!(mc.currentScreen instanceof GuiChat)) {
//                lastLeft = lastRight = false;
//                dragX = dragY = 0;
//                dragging = drag = false;
//                return;
//            }
//
//            boolean leftClick = false, rightClick = false;
//            if (Mouse.isButtonDown(0) && !lastLeft) {
//                leftClick = true;
//            }
//            if (Mouse.isButtonDown(1) && !lastRight) {
//                rightClick = true;
//            }
//
//            int i1 = event.getSr().getScaledWidth();
//            int j1 = event.getSr().getScaledHeight();
//            final int mouseX = Mouse.getX() * i1 / mc.displayWidth;
//            final int mouseY = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;
//
//            float x = xValue.getValue().floatValue(), y = yValue.getValue().floatValue();
//
//            if (drag) {
//                xValue.setValueNoSave((double) (mouseX - dragX));
//            }
//
//            if (drag) {
//                yValue.setValueNoSave((double) (mouseY - dragY));
//            }
//
//            if (ClickGuiScreen.isHovered(x, y - height() - 4f, x + width(), y - 4f, mouseX, mouseY) && leftClick) {
//                dragging = true;
//            } else if (!Mouse.isButtonDown(0)) {
//                if (dragging) {
//                    // Save values
//                    xValue.setValueForced(xValue.getValue());
//                    yValue.setValueForced(yValue.getValue());
//                }
//                dragging = false;
//            }
//
//            if (dragging) {
//                drag = true;
//                if (dragX == 0) {
//                    dragX = mouseX - x;
//                }
//                if (dragY == 0) {
//                    dragY = mouseY - y;
//                }
//            } else if (dragX != 0 || dragY != 0) {
//                dragX = 0;
//                dragY = 0;
//                drag = false;
//            }
//
//            lastLeft = Mouse.isButtonDown(0);
//            lastRight = Mouse.isButtonDown(1);
//        }
//
//        if (!(mc.currentScreen instanceof GuiChat)) return;
//        float x = xValue.getValue().floatValue(), y = yValue.getValue().floatValue();
//
//        RenderUtil.drawRect(x, y - height() - 4F, x + width(), y - 4F, event.getState() == EventState.PRE ? Color.BLACK.getRGB() : -1);
//        Southside.fontManager.font.drawCenteredString(8, this.displayName, x + width() / 2F, y - height() - 4F + 1F, Color.BLACK);
//    }
//
//    public abstract float width();
//
//    public float height() {
//        return Southside.fontManager.font.getHeight(8) + 2F;
//    }
//}
