//package dev.diona.southside.gui.alt;
//
//import dev.diona.southside.Southside;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.GuiResourcePackList;
//import net.minecraft.client.gui.GuiScreen;
//
//import java.io.IOException;
//
//public class GuiQuickMacro extends GuiScreen {
//    private GuiScreen prevScreen;
//    private GuiListAccount guiListAccount;
//
//    public GuiQuickMacro(GuiScreen prevScreen) {
//        this.prevScreen = prevScreen;
//    }
//
//    public void initGui() {
//        this.guiListAccount = new GuiListAccount(this);
//        this.guiListAccount.registerScrollButtons(7, 8);
//        this.guiListAccount.elementClicked(-1, false, 0, 0);
//        this.guiListAccount.scrollBy(-1 * this.guiListAccount.getSlotHeight());
//        var j = 22;
//        this.buttonList.clear();
//        buttonList.add(new GuiButton(1, 10, j + 24, 140, 20, "Import From Clipboard"));
//        buttonList.add(new GuiButton(2, 10, j + 24 * 2, 140, 20, "Remove"));
//        buttonList.add(new GuiButton(2, 10, j + 24 * 3, 140, 20, "Login"));
//    }
//
//    @Override
//    protected void actionPerformed(GuiButton button) throws IOException {
//        if (button.id == 1) {
//
//        } else if (button.id == 2) {
//            if (guiListAccount.selected != -1 && guiListAccount.selected < guiListAccount.getSize()) {
//                guiListAccount.selected = -1;
//                Southside.quickMacroAltManager.cookies.remove(guiListAccount.selected);
//                Southside.quickMacroAltManager.save();
//            }
//        } else if (button.id == 3) {
//
//        }
//        super.actionPerformed(button);
//    }
//
//    @Override
//    public void handleMouseInput() throws IOException {
//        this.guiListAccount.handleMouseInput();
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        drawBackground(0);
//        this.guiListAccount.drawScreen(mouseX, mouseY, partialTicks);
//        super.drawScreen(mouseX, mouseY, partialTicks);
//    }
//}
