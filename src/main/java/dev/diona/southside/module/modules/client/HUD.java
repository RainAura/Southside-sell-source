//package dev.diona.southside.module.modules.client;
//
//import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import cc.polyfrost.oneconfig.config.options.impl.Switch;
//import de.florianmichael.vialoadingbase.ViaLoadingBase;
//import de.florianmichael.viamcp.ViaMCP;
//import dev.diona.southside.Southside;
//import dev.diona.southside.event.EventState;
//import dev.diona.southside.event.events.Bloom2DEvent;
//import dev.diona.southside.event.events.Render2DEvent;
//import dev.diona.southside.gui.UIElementModule;
//import dev.diona.southside.gui.font.NvgFontRenderer;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.Module;
//import dev.diona.southside.module.annotations.DefaultEnabled;
//import dev.diona.southside.module.values.*;
//import dev.diona.southside.util.misc.BezierUtil;
//import dev.diona.southside.util.misc.TimerUtil;
//import dev.diona.southside.util.render.GLUtil;
//import dev.diona.southside.util.render.RenderUtil;
//import me.bush.eventbus.annotation.EventListener;
//import me.bush.eventbus.annotation.ListenerPriority;
//import net.minecraft.client.Minecraft;
//import net.minecraft.util.text.TextFormatting;
//
//import java.awt.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//@DefaultEnabled
//public class HUD extends UIElementModule {
//    public static HUD INSTANCE;
//    private CopyOnWriteArrayList<ArrayListModule> enabledModules;
//    private HashMap<Module, ArrayListModule> modules;
//    public Switch arrayValue = new Switch("Array", true);
//
//    public Switch clientValue = new Switch("ClientName", true);
//
//    public Slider spacingValue = new Slider("Spacing",3,1,10,1);
//    public Switch noRenderValue = new Switch("No Render Modules", false);
//    public Dropdown theme = new Dropdown("Theme","Fancy", "Classic", "Southside","Fancy");
//    public Slider fontSizeValue = new Slider("Font Size", 12, 8, 24, 1);
//    public Slider moduleOffsetValue = new Slider("Module Offset", 0, -5, 5);
//    public Slider arrayXOffsetValue = new Slider("Array X Offset", 0, -5, 5);
//    public Slider arrayYOffsetValue = new Slider("Array Y Offset", 0, -5, 5);
//
//    public Switch backgroundValue = new Switch("Background", false);
//    public Switch rainbowValue = new Switch("Rainbow",false);
//
//    private TimerUtil timerUtil = new TimerUtil();
//
//    public HUD(String name, String description, Category category, boolean visible) {
//        super(name, description, category, visible, "Client Name", 10, 10);
//        INSTANCE = this;
//        xValue.setValueNoSave(4d);
//        yValue.setValueNoSave(4d);
//    }
//
//    public void syncModules() {
//        float fontSize = fontSizeValue.getValue().floatValue();
//        if (modules == null) {
//            modules = new HashMap<>();
//            Southside.moduleManager.getModules().forEach(module -> {
//                modules.put(module, new ArrayListModule(this, module));
//            });
//        }
//        enabledModules = new CopyOnWriteArrayList<>();
//        for (Module module : Southside.moduleManager.getModules()) {
//            if (shouldDisplay(module)) {
//                enabledModules.add(modules.get(module));
//            }
//        }
//        final var font = Southside.fontManager.roboto;
//        enabledModules.sort((o1, o2) -> (int) (10000F * font.getStringWidth(fontSize, Southside.moduleManager.formatRaw(o2.module)) - 10000F * font.getStringWidth(fontSize, Southside.moduleManager.formatRaw(o1.module))));
//    }
//
//    private boolean shouldDisplay(Module module) {
//        return module.isEnabled() && module.isVisible() && (module.getCategory() != Category.Render || !noRenderValue.getValue());
//    }
//
//    @EventListener
//    public final void onRender2DEvent(final Render2DEvent event) {
//        if (timerUtil.hasReached(1000)){
//            syncModules();
//            timerUtil.reset();
//        }
//    	if (theme.isMode("Classic")) {
//    		Color color = rainbow(16, 0, .6f, 1, 1);
//            final StringBuilder builder = new StringBuilder();
//            builder.append(String.format("§l%s%s%s§r", Southside.CLIENT_NAME.charAt(0), TextFormatting.GRAY , Southside.CLIENT_NAME.substring(1)));
//            Southside.fontManager.roboto.drawStringWithShadow(fontSizeValue.getValue().floatValue(), builder.toString(), Southside.fontManager.tahomabd.getStringWidth(fontSizeValue.getValue().floatValue(), builder.toString()) / 2 - 5, 5, color);
//            //mc.fontRenderer.drawStringWithShadow(builder.toString(), this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue() - 5f, color.getRGB());
//    	}
//
//
//    }
//
//    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
//        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
//        float hue = angle / 360f;
//        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
//        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
//    }
//
//
//    @EventListener(priority = ListenerPriority.LOW)
//    public void onBloom(Bloom2DEvent event) {
//        float fontSize = fontSizeValue.getValue().floatValue();
//        super.onUIElementBloom(event);
//        Color color = event.getState() ==  EventState.PRE ? Southside.styleManager.getStyle().getFontShadowColor() : Southside.styleManager.getStyle().getClickGuiModuleNameColor();
//        Color suffixColor = event.getState() == EventState.PRE ? Southside.styleManager.getStyle().getFontShadowColor() : Southside.styleManager.getStyle().getHudArrayListModuleSuffixColor();
//        if (clientValue.getValue() && theme.isMode("Southside")) {
//            Southside.fontManager.thin.drawString(60, Southside.CLIENT_NAME, this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue() - 5f, color);
//        }
//        if(theme.isMode("Fancy")){
//            if(rainbowValue.getValue()){
//                color = rainbow(16, 0, .6f, 1, 1);
//            }
//            String text = String.format("%s %s| %s | %s","SOUTHSIDE", TextFormatting.WHITE,mc.player.getName(),mc.isSingleplayer() ? "singleplayer" : mc.getCurrentServerData().serverIP);
//            RenderUtil.drawRect(xValue.getValue().floatValue(),yValue.getValue().floatValue(),xValue.getValue().floatValue() + Southside.fontManager.font.getStringWidth(fontSize,text) + 8,yValue.getValue().floatValue() + Southside.fontManager.font.getHeight(fontSize) + 8,new Color(32, 32, 32, 100).getRGB());
//            RenderUtil.drawRect(xValue.getValue().floatValue(),yValue.getValue().floatValue() + 1,xValue.getValue().floatValue() -1,yValue.getValue().floatValue() + Southside.fontManager.font.getHeight(fontSize) + 7,color.getRGB());
//            Southside.fontManager.font.drawString(fontSize,text,xValue.getValue().floatValue() + 5,yValue.getValue().floatValue() + 5, color);
//            color = Color.WHITE;
//        }
//        if (arrayValue.getValue()) {
//            int width = event.getSr().getScaledWidth();
//            float y = 4 + arrayYOffsetValue.getValue().floatValue();
//
//
//            int count = 0;
//            for (ArrayListModule module : enabledModules) {
//                final var font = Southside.fontManager.roboto;
//                if(rainbowValue.getValue()){
//                    color = rainbow(16, count * 10, .6f, 1, 1);
//                }
//                module.draw(0, y, width, event.getState() == EventState.PRE, color, suffixColor, this.backgroundValue.getValue());
//                y += (float) (font.getHeight(fontSize) + spacingValue.getValue().floatValue());
//                count++;
//            }
//
//            count = 0;
//            for (ArrayListModule module : modules.values()) {
//
//                if (shouldDisplay(module.module)) continue;
//                if(rainbowValue.getValue()){
//                    color = rainbow(16, count * 10, .6f, 1, 1);
//                }
//                module.draw(101, module.y.get(), width, event.getState() == EventState.PRE, color, suffixColor, this.backgroundValue.getValue());
//                y += (float) (Southside.fontManager.font.getHeight(fontSize) + spacingValue.getValue().floatValue());
//                count++;
//            }
//
//        }
//        GLUtil.endBlend();
//
//    }
//
//    @Override
//    public float width() {
//        return 80;
//    }
//
//    public static class ArrayListModule {
//        private HUD hud;
//        public Module module;
//        public BezierUtil x = new BezierUtil(3, 0), y = new BezierUtil(3, 0);
//
//        public ArrayListModule(HUD hud, Module module) {
//            this.hud = hud;
//            this.module = module;
//        }
//
//        public void draw(float targetX, float targetY, float width, boolean bloom, Color color, Color suffixColor, boolean background) {
//            float fontSize = hud.fontSizeValue.getValue().floatValue();
//            final var font = Southside.fontManager.roboto;
//            if (!bloom) {
//                x.freeze();
//                y.freeze();
//            }
//            x.update(targetX);
//            y.update(targetY);
//            if (x.get() >= 100) return;
//            float moduleWidth = font.getStringWidth(fontSize, Southside.moduleManager.formatRaw(module));
//            float xPos = x.get() + width - moduleWidth - 4 + hud.arrayXOffsetValue.getValue().floatValue();
//            float yPos = y.get();
//            float height = (float) (font.getHeight(fontSize) + hud.spacingValue.getValue().floatValue());
//            if (background) {
//                RenderUtil.drawRect(xPos - 3, yPos - 2F, x.get() + width - 2F, yPos + height - 2F, new Color(32, 32, 32, 100).getRGB());
//                //RenderUtil.drawRect(x.get() + width - 2F, yPos - 1F, x.get() + width, yPos + height - 1.5, color.getRGB());
//            }
//
////            RenderUtil.drawRect(xPos, (yPos - Southside.fontManager.font.getHeight(fontSize) / 2F + height / 2F - 2F) + hud.moduleOffsetValue.getValue().floatValue(), xPos + Southside.fontManager.font.getStringWidth(fontSize, Southside.moduleManager.format(module)), (yPos - Southside.fontManager.font.getHeight(fontSize) / 2F + height / 2F - 2F) + hud.moduleOffsetValue.getValue().floatValue() + 2F, -1);
//            font.drawString(fontSize, Southside.moduleManager.format(module), xPos, (yPos - font.getHeight(fontSize) / 2F + height / 2F - 1F) + hud.moduleOffsetValue.getValue().floatValue(), color);
//        }
//    }
//}
