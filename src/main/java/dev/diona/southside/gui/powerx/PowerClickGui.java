//package dev.diona.southside.gui.powerx;
//
//import dev.diona.southside.Southside;
//import dev.diona.southside.managers.RenderManager;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.Module;
//import dev.diona.southside.module.Value;
//import dev.diona.southside.module.values.BooleanValue;
//import dev.diona.southside.module.values.ModeValue;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import dev.diona.southside.util.render.RenderUtil;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.gui.ScaledResolution;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.util.ResourceLocation;
//import org.lwjgl.opengl.GL11;
//import org.lwjglx.input.Mouse;
//
//import java.awt.*;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//@SuppressWarnings(value = {"rawtypes", "unchecked"})
//public class PowerClickGui extends GuiScreen {
//    public static boolean binding = false;
//    public static Module currentMod = null;
//    private final PowerButton handlerMid;
//    private final PowerButton handlerRight;
//    private final PowerButton handler;
//    public int moveX;
//    public int moveY;
//    public int startX;
//    public int startY;
//    public int selectCategory;
//    public Module bmod;
//    public boolean dragging;
//    public boolean drag;
//    public boolean Mdrag;
//    ArrayList<Module> mods;
//    ScaledResolution res;
//    Value<?> value;
//    ScaledResolution sr;
//    private float scrollY;
//    private float modscrollY;
//
//    public PowerClickGui() {
//        this.mods = new ArrayList<>(Southside.moduleManager.getopenValues());
//        this.handlerMid = new PowerButton(2);
//        this.handlerRight = new PowerButton(1);
//        this.handler = new PowerButton(0);
//        this.res = new ScaledResolution(Minecraft.getMinecraft());
//        this.moveX = 0;
//        this.moveY = 0;
//        this.startX = 50;
//        this.startY = 40;
//        this.selectCategory = 0;
//        this.sr = new ScaledResolution(Minecraft.getMinecraft());
//    }
//
//    public static void erase(boolean stencil) {
//        GL11.glStencilFunc(stencil ? 514 : 517, 1, '\uffff');
//        GL11.glStencilOp(7680, 7680, 7681);
//        GlStateManager.colorMask(true, true, true, true);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
//        GL11.glAlphaFunc(516, 0.0F);
//    }
//
//    public static List getValueList(Module module) {
//
//        return module.getValues();
//    }
//
//    public static List<Module> getModsInCategory(Category cat) {
//        ArrayList<Module> list = new ArrayList<>();
//
//        for (Module m : Southside.moduleManager.getModules()) {
//            if (m.getCategory() == cat) {
//                list.add(m);
//            }
//        }
//
//        return list;
//    }
//
//    public boolean doesGuiPauseGame() {
//        return false;
//    }
//
//    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        if (binding) {
//            if (keyCode != 1 && keyCode != 211) {
//                this.bmod.setBind(keyCode);
//            } else if (keyCode == 211) {
//                this.bmod.setBind(0);
//            }
//
//            binding = false;
//        }
//
//        super.keyTyped(typedChar, keyCode);
//    }
//
//    @Override
//    public void mouseReleased(int mouseX, int mouseY, int state) {
//        if (this.dragging) {
//            this.dragging = false;
//        }
//
//        if (this.drag) {
//            this.drag = false;
//        }
//
//        super.mouseReleased(mouseX, mouseY, state);
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        RenderManager.beginNvgFrame();
//        this.sr = new ScaledResolution(Minecraft.getMinecraft());
//        if (this.isHovered((float) this.startX, (float) (this.startY - 8), (float) (this.startX + 300), (float) (this.startY + 5), mouseX, mouseY) && !this.isHovered((float) (this.startX + 289), (float) (this.startY - 8), (float) (this.startX + 296), (float) (this.startY), mouseX, mouseY) && this.handler.canExcecute()) {
//            this.dragging = true;
//        }
//
//        if (this.dragging) {
//            if (this.moveX == 0 && this.moveY == 0) {
//                this.moveX = mouseX - this.startX;
//                this.moveY = mouseY - this.startY;
//            } else {
//                this.startX = mouseX - this.moveX;
//                this.startY = mouseY - this.moveY;
//            }
//        } else if (this.moveX != 0 || this.moveY != 0) {
//            this.moveX = 0;
//            this.moveY = 0;
//        }
//
//        if ((float) this.startX > (float) (this.sr.getScaledWidth() - 303)) {
//            this.startX = this.sr.getScaledWidth() - 303;
//        }
//
//        if (this.startX < 3) {
//            this.startX = 3;
//        }
//
//        if ((float) this.startY > (float) (this.sr.getScaledHeight() - 190)) {
//            this.startY = this.sr.getScaledHeight() - 190;
//        }
//
//        if ((float) this.startY < 12.0F) {
//            this.startY = 12;
//        }
//
//        GL11.glPushMatrix();
//        erase(false);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/menu.png"), this.startX - 10, this.startY - 18, 320, 216, new Color(255, 255, 255));
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelright.png"), this.startX + 59, this.startY + 5, 9, 182);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelright.png"), this.startX + 59, this.startY + 5, 9, 182);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelright.png"), this.startX + 59, this.startY + 5, 9, 182);
//        GL11.glEnable(3089);
//        RenderUtil.scissorStart((float) this.startX, (float) (this.startY + 5), (float) (300), (float) (180));
//        int y = 0;
//        Category[] moduleTypes = Category.values();
//        int length = moduleTypes.length;
//
//        for (int i = 0; i < length; ++i) {
//            Category moduleType = Category.values()[i];
//            String str = moduleType.name().replaceAll("Movement", "Move").replaceAll("MiniGames", "GAMES");
//            RenderUtil.drawImage(new ResourceLocation("southside/clickgui/" + moduleType.name().toUpperCase() + ".png"), this.startX + 4, this.startY + 16 + y, 12, 12, this.selectCategory == i ? new Color(43, 110, 141) : new Color(170, 170, 170));
//            Southside.fontManager.zhijun.drawCenteredString(7,str.charAt(0) + str.toLowerCase().substring(1, str.length()), (float) (this.startX + 36), (float) (this.startY + 18 + y), this.selectCategory == i ? (new Color(0, 170, 255)) : (new Color(170, 170, 170)));
//            if (this.isHovered((float) (this.startX + 3), (float) (this.startY + 14 + y), (float) (this.startX + 50), (float) (this.startY + 32 + y), mouseX, mouseY) && this.handler.canExcecute()) {
//                this.selectCategory = i;
//            }
//
//            y += 25;
//        }
//
//        int buttonX = this.startX + 64;
//        int buttonY = this.startY + 12;
//
//        int modulePos = this.startY + 8;
//
//
//        for (int i = 0; i < getModsInCategory(Category.values()[this.selectCategory]).size(); ++i) {
//            Module mod = getModsInCategory(Category.values()[this.selectCategory]).get(i);
//            if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 185), mouseX, mouseY) && getModsInCategory(Category.values()[this.selectCategory]).size() > 11 && this.isHovered((float) buttonX, (float) (modulePos - 2), (float) (buttonX + 82), (float) (modulePos + 12), mouseX, mouseY)) {
//                float wheel = (float) Mouse.getDWheel();
//                this.modscrollY += wheel * 10;
//
//            }
//
//            if (getModsInCategory(Category.values()[this.selectCategory]).size() < 12) {
//                this.modscrollY = 0.0F;
//            }
//
//            if ((double) this.modscrollY > 0.0D) {
//                this.modscrollY = 0.0F;
//            }
//
//            if (getModsInCategory(Category.values()[this.selectCategory]).size() > 11 && this.modscrollY < (float) ((getModsInCategory(Category.values()[this.selectCategory]).size() - 11) * -16)) {
//                this.modscrollY = (float) ((getModsInCategory(Category.values()[this.selectCategory]).size() - 11) * -16);
//            }
//
//            if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY)) {
//                RenderUtil.drawImage(new ResourceLocation("southside/clickgui/mod.png"), buttonX, (int) ((float) (modulePos - 2) + this.modscrollY), 82, 14, new Color(180, 180, 180, 80));
//            } else {
//                RenderUtil.drawImage(new ResourceLocation("southside/clickgui/mod.png"), buttonX, (int) ((float) (modulePos - 2) + this.modscrollY), 82, 14, mod.isEnabled() ? new Color(60, 60, 60) : new Color(40, 40, 40));
//            }
//
//            RenderUtil.drawGoodCircle( (buttonX + 8), (modulePos + 5) + this.modscrollY, 1.5F, mod.isEnabled() ? (new Color(0, 124, 255)).getRGB() : (new Color(153, 153, 153)).getRGB());
//            Southside.fontManager.zhijun.drawCenteredString(7,binding ? (mod == this.bmod ? "Binding Key" : mod.getName()) : mod.getName(), (float) (buttonX + 40), (float) (modulePos + 1) + this.modscrollY, mod.isEnabled() ? (new Color(220, 220, 220)) : (new Color(90, 90, 90)));
//            Southside.fontManager.zhijun.drawCenteredString(7,!mod.getValues().isEmpty() ? (mod.openValues ? "-" : "+") : "", (float) (buttonX + 76), (float) (modulePos + 1) + this.modscrollY, (new Color(153, 153, 153)));
//            if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handlerMid.canExcecute()) {
//                binding = true;
//                this.bmod = mod;
//            }
//
//            if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handler.canExcecute()) {
//                mod.setEnable(!mod.isEnabled());
//            }
//
//            if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handlerRight.canExcecute() && !mod.openValues && !mod.getValues().isEmpty()) {
//                mod.openValues = !mod.openValues;
//                currentMod = mod;
//                this.scrollY = 0.0F;
//
//                for (Module m : Southside.moduleManager.getModules()) {
//                    if (m.openValues && !Objects.equals(m.getName(), mod.getName())) {
//                        m.openValues = false;
//                    }
//                }
//            }
//
//            if (mod.openValues) {
//                for (Value value : mod.getValues()) {
//                    if (value instanceof ModeValue) {
//                        String name = ((ModeValue) value).getValue();
//
//                        String[] strings = ((ModeValue) value).getStrings();
//                        String pre = "", suf = "";
//                        for (int sb = 0; sb < strings.length; sb++) {
//                            if (strings[sb].equals(value.getValue())) {
//                                pre = strings[sb == 0 ? strings.length - 1 : sb - 1];
//                                suf = strings[sb == strings.length - 1 ? 0 : sb + 1];
//                            }
//                        }
//                        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/boolean_left.png"), buttonX + 144, (int) ((float) buttonY + this.scrollY - 2.0F), 10, 10);
//                        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/mode_bg.png"), buttonX + 154, (int) ((float) buttonY + this.scrollY - 4.0F), 54, 14);
//                        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/boolean_right.png"), buttonX + 208, (int) ((float) buttonY + this.scrollY - 2.0F), 10, 10);
//                        Southside.fontManager.zhijun.drawString(7,name, (float) (buttonX + 180 - Southside.fontManager.zhijun.getStringWidth(7,"" + name) / 2), (float) buttonY + this.scrollY - 1.0F, (new Color(200, 200, 200)));
//                        Southside.fontManager.zhijun.drawString(7,value.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY+ 2, (new Color(153, 153, 169)));
//                        if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) (buttonX + 144), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 153), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
//                            value.setValue(pre);
//                        }
//
//                        if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) (buttonX + 208), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 217), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
//                            value.setValue(suf);
//                        }
//
//                        buttonY += 18;
//                    }
//                }
//
//                for (Value number : mod.getValues()) {
//                    if (number instanceof NumberValue) {
//                        this.width = 100;
//                        double num = ((Double) number.getValue() - ((NumberValue) number).getMinimum().doubleValue()) / (((NumberValue) number).getMaximum().doubleValue() - ((NumberValue) number).getMinimum().doubleValue());
//                        double maxX = mouseX - (buttonX + 145);
//                        double minPos = maxX / 83.0D;
//                        minPos = Math.min(Math.max(0.0D, minPos), 1.0D);
//                        double max = (((NumberValue) number).getMaximum().doubleValue() - ((NumberValue) number).getMinimum().doubleValue()) * minPos;
//                        double min = ((NumberValue) number).getMinimum().doubleValue() + max;
//                        double slider = (double) (buttonX + 145) + 83.0D * num;
//                        RenderUtil.drawRect((float) (buttonX + 145), (float) (buttonY + 4) + this.scrollY, (float) (buttonX + 230), (float) (buttonY + 6) + this.scrollY, (new Color(53, 54, 53)).getRGB());
//                        RenderUtil.drawRect( buttonX + 145.5f,  buttonY + 4.5f + this.scrollY, (float) (slider + 2.0f), (float) (buttonY + 5.5f + (double) this.scrollY), (new Color(0, 100, 242)).getRGB());
//                        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/slider.png"), (int) slider - 1, (int) ((float) (buttonY + 3) + this.scrollY), 4, 4, new Color(0, 100, 242));
//                        Southside.fontManager.zhijun.drawString(7,"" + number.getValue(), (float) (buttonX + 229 - Southside.fontManager.zhijun.getStringWidth(7,"" + number.getValue())), (float) (buttonY - 4) + this.scrollY, (new Color(153, 153, 169)));
//                        Southside.fontManager.zhijun.drawString(7,number.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY + 2, (new Color(153, 153, 169)));
//                        if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY) && this.isHovered((float) (buttonX + 145), (float) (buttonY + 1) + this.scrollY, (float) (buttonX + 230), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
//                            this.value = number;
//                            this.drag = true;
//                        }
//
//                        if (this.drag && number == this.value) {
//                            min = (double) Math.round(min * (1.0D / ((NumberValue) number).getIncrement().doubleValue())) / (1.0D / ((NumberValue) number).getIncrement().doubleValue());
//                            number.setValue(min);
//                        } else {
//                            min = (double) Math.round((Double) number.getValue() * (1.0D / ((NumberValue) number).getIncrement().doubleValue())) / (1.0D / ((NumberValue) number).getIncrement().doubleValue());
//                            number.setValue(min);
//                        }
//
//                        buttonY += 18;
//                    }
//                }
//
//                for (Value booleanValue : mod.getValues()) {
//                    if (booleanValue instanceof BooleanValue) {
//                        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/value_boolean_bg.png"), buttonX + 214, (int) ((float) buttonY + this.scrollY - 1.0F), 16, 8, new Color(255, 255, 255));
//                        if ((Boolean) booleanValue.getValue()) {
//                            RenderUtil.drawImage(new ResourceLocation("southside/clickgui/value_boolean_button.png"), buttonX + 222, (int) ((float) buttonY + this.scrollY - 1.0F), 8, 8, new Color(0, 125, 255));
//                        } else {
//                            RenderUtil.drawImage(new ResourceLocation("southside/clickgui/value_boolean_button.png"), buttonX + 214, (int) ((float) buttonY + this.scrollY - 1.0F), 8, 8, new Color(153, 153, 153));
//                        }
//
//                        if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY) && this.isHovered((float) (buttonX + 214), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 230), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
//                            booleanValue.setValue(!(Boolean) booleanValue.getValue());
//                        }
//
//                        Southside.fontManager.zhijun.drawString(7,booleanValue.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY+ 2, (new Color(153, 153, 169)));
//                        buttonY += 18;
//                    }
//                }
//
//                if (getValueList(mod).size() > 10 && buttonY > this.startY + 185 && this.isHovered((float) (this.startX + 151), (float) (this.startY - 8), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY)) {
//                    float wheel = (float) Mouse.getDWheel();
//                    this.scrollY += wheel * 3;
//                }
//
//                if ((double) this.scrollY > 0.0D) {
//                    this.scrollY = 0.0F;
//                }
//
//                if (getValueList(mod).size() > 10 && this.scrollY < (float) ((getValueList(mod).size() - 10) * -18)) {
//                    this.scrollY = (float) ((getValueList(mod).size() - 10) * -18);
//                }
//            }
//
//            modulePos += 16;
//        }
//        GL11.glDisable(3089);
//
//        RenderUtil.scissorEnd();
//        if (this.isHovered((float) (this.startX + 289), (float) (this.startY - 8), (float) (this.startX + 296), (float) (this.startY), mouseX, mouseY)) {
//            RenderUtil.drawImage(new ResourceLocation("southside/clickgui/open.png"), this.startX + 288, this.startY - 8, 10, 10, new Color(255, 0, 0));
//            if (this.handler.canExcecute()) {
//                this.mc.displayGuiScreen(null);
//                this.mc.setIngameFocus();
//            }
//        } else {
//            RenderUtil.drawImage(new ResourceLocation("southside/clickgui/open.png"), this.startX + 288, this.startY - 8, 10, 10, new Color(0, 125, 255));
//        }
//
//        Southside.fontManager.zhijun.drawCenteredString(9,"Southside", (float) (this.startX + 28), (float) (this.startY - 6), (new Color(170, 170, 170)));
//        Southside.fontManager.zhijun.drawCenteredString(7,Category.values()[this.selectCategory].name(), (float) (this.startX + 80), (float) (this.startY - 6), (new Color(153, 153, 159)));
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelbottom.png"), this.startX, this.startY + 5, 301, 9);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelright.png"), this.startX + 150, this.startY + 5, 9, 180);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelleft.png"), this.startX + 141, this.startY + 5, 9, 180);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelleft.png"), this.startX + 292, this.startY + 5, 9, 180);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/panelright.png"), this.startX, this.startY + 5, 9, 180);
//        RenderUtil.drawImage(new ResourceLocation("southside/clickgui/paneltop.png"), this.startX, this.startY + 179, 301, 9);
//        GL11.glPopMatrix();
//
//        RenderManager.endNvgFrame();
//    }
//
//    public boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
//        return (float) mouseX >= x && (float) mouseX <= width && (float) mouseY >= y && (float) mouseY <= height;
//    }
//
//    public void onGuiClosed() {
//        if (this.mc.entityRenderer.getShaderGroup() != null) {
//            this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
//        }
//
//        this.dragging = false;
//        this.drag = false;
//        this.Mdrag = false;
//        super.onGuiClosed();
//    }
//}
