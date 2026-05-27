//package dev.diona.southside.module.modules.render;
//
//import dev.diona.southside.Southside;
//import dev.diona.southside.event.EventState;
//import dev.diona.southside.event.events.Bloom2DEvent;
//import dev.diona.southside.gui.UIElementModule;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.util.render.RenderUtil;
//import dev.diona.southside.util.render.RoundUtil;
//import me.bush.eventbus.annotation.EventListener;
//
//import java.awt.*;
//
//public class TestUIModule extends UIElementModule {
//    public TestUIModule(String name, String description, Category category, boolean visible) {
//        super(name, description, category, visible, "Test UI Module", 0, 0);
//    }
//
//    @EventListener
//    public void onBloom(Bloom2DEvent event) {
//        float fontSize = 12F;
//        super.onUIElementBloom(event);
//        if (event.getState() == EventState.PRE) return;
//        String s = "S P E E D G R I M";
//        float width = 0;
//        width = Southside.fontManager.font.getStringWidth(fontSize, s);
//
//        RenderUtil.drawRect(this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue(), this.xValue.getValue().floatValue() + width, this.yValue.getValue().floatValue() + 2F, new Color(255, 255, 255).getRGB());
//
//        Southside.fontManager.font.drawString(fontSize, s, this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue(), Color.WHITE);
//        Southside.fontManager.font.drawUnformattedString(fontSize, s, this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue() + 10F, Color.WHITE);
//
//        width = Southside.fontManager.font.getCharWidth(fontSize, s);
//
//        RenderUtil.drawRect(this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue(), this.xValue.getValue().floatValue() + width, this.yValue.getValue().floatValue() + 2F, new Color(255, 255, 255).getRGB());
//
//    }
//
//    @Override
//    public float width() {
//        return 70F;
//    }
//}
