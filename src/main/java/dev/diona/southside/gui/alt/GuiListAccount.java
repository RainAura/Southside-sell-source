//package dev.diona.southside.gui.alt;
//
//import dev.diona.southside.Southside;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.gui.GuiSlot;
//
//import java.awt.*;
//
//public class GuiListAccount extends GuiSlot {
//    private GuiScreen prevGui;
//    public int selected = 0;
//    private String status = "Choose an account";
//
//    public GuiListAccount(GuiScreen prevGui) {
//        super(Southside.MC.mc, prevGui.width, prevGui.height, 40, prevGui.height - 40, 30);
//        this.prevGui = prevGui;
//    }
//
//    @Override
//    protected int getSize() {
//        return Southside.quickMacroAltManager.cookies.size();
//    }
//
//    @Override
//    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
//        selected = slotIndex;
//        if (isDoubleClick) {
//            if (selected != -1 && selected < Southside.quickMacroAltManager.cookies.size()) {
//                // login
//                status = "Logging in";
//            } else {
//                selected = -1;
//            }
//        }
//    }
//
//    @Override
//    protected boolean isSelected(int slotIndex) {
//        return selected == slotIndex;
//    }
//
//    @Override
//    protected void drawBackground() {
//
//    }
//
//    @Override
//    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
//
////        val minecraftAccount = Phi.getInstance().getAccountManager().alts[id]
//        mc.fontRenderer.drawCenteredString(Southside.quickMacroAltManager.cookies.get(slotIndex), width / 2f, yPos + 2f, Color.WHITE.getRGB());
////        mc.fontRenderer.drawCenteredString(minecraftAccount.type, width / 2f, y + 15f, Color.LIGHT_GRAY.rgb)
//    }
//}
