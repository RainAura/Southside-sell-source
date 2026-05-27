//package dev.diona.southside.module.modules.client;
//
//import dev.diona.southside.Southside;
//import dev.diona.southside.gui.click.ClickGuiScreen;
//import dev.diona.southside.gui.powerx.PowerClickGui;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.Module;
//import dev.diona.southside.module.annotations.Binding;
//import net.minecraft.client.gui.GuiScreen;
//import org.lwjglx.input.Keyboard;
//
//import static dev.diona.southside.Southside.MC.mc;
//
//@Binding(Keyboard.KEY_RSHIFT)
//public class ClickGui extends Module {
//
//    public static GuiScreen clickGuiScreen;
//    public static PowerClickGui powerClickGui;
//
//    public ClickGui(String name, String description, Category category, boolean visible) {
//        super(name, description, category, visible);
//    }
//
//    @Override
//    public boolean onEnable() {
//        setEnable(false);
//        if (clickGuiScreen == null) {
//            clickGuiScreen = new ClickGuiScreen();
//        }
//        if (powerClickGui == null) {
//            powerClickGui = new PowerClickGui();
//        }
//        mc.displayGuiScreen(clickGuiScreen);
//        return true;
//    }
//}
