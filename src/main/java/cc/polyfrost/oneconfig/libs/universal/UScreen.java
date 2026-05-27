package cc.polyfrost.oneconfig.libs.universal;

import dev.diona.southside.util.misc.MathUtil;
import net.minecraft.client.gui.*;
import java.io.*;
import org.lwjglx.input.Mouse;

public abstract class UScreen extends GuiScreen
{
    public static final UScreen.Companion Companion;
    private final boolean restoreCurrentGuiOnClose;
    private int newGuiScale;

    private String unlocalizedName;
    private int guiScaleToRestore;

    private final GuiScreen screenToRestore;

//    public UScreen(final boolean restoreCurrentGuiOnClose, final int newGuiScale, final String unlocalizedName) {
//        this.restoreCurrentGuiOnClose = restoreCurrentGuiOnClose;
//        this.newGuiScale = newGuiScale;
//        this.unlocalizedName = unlocalizedName;
//        this.guiScaleToRestore = -1;
//        this.screenToRestore = (this.restoreCurrentGuiOnClose ? UScreen.Companion.getCurrentScreen() : null);
//    }

    public final boolean getRestoreCurrentGuiOnClose() {
        return this.restoreCurrentGuiOnClose;
    }

    public int getNewGuiScale() {
        return this.newGuiScale;
    }

    public void setNewGuiScale(final int newGuiScale) {
        this.newGuiScale = newGuiScale;
    }

    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    public void setUnlocalizedName(final String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
    }

//    public UScreen(final boolean restoreCurrentGuiOnClose, final int newGuiScale) {
//        this(restoreCurrentGuiOnClose, newGuiScale, null);
//    }

    public final void initGui() {
        this.updateGuiScale();
        this.initScreen(this.width, this.height);
    }

    public final void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.onDrawScreenCompat(new UMatrixStack(), mouseX, mouseY, partialTicks);
    }

    protected final void keyTyped(final char typedChar, final int keyCode) {
        this.onKeyPressed(keyCode, typedChar, UKeyboard.getModifiers());
    }

    protected final void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        this.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected final void mouseReleased(final int mouseX, final int mouseY, final int state) {
        this.onMouseReleased(mouseX, mouseY, state);
    }

    protected final void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        this.onMouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    public final void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int scrollDelta = Mouse.getEventDWheel();
        if (scrollDelta != 0) {
            this.onMouseScrolled(scrollDelta);
        }
    }

    public final void updateScreen() {
        this.onTick();
    }

    public final void onGuiClosed() {
        this.onScreenClose();
//        if (this.guiScaleToRestore != -1) {
//            UMinecraft.setGuiScale(this.guiScaleToRestore);
//        }
    }

    public final void drawWorldBackground(final int tint) {
        this.onDrawBackgroundCompat(new UMatrixStack(), tint);
    }

    public final void restorePreviousScreen() {
        UScreen.Companion.displayScreen(this.screenToRestore);
    }

    public void updateGuiScale() {
//        if (this.getNewGuiScale() != -1) {
//            if (this.guiScaleToRestore == -1) {
//                this.guiScaleToRestore = UMinecraft.getGuiScale();
//            }
//            UMinecraft.setGuiScale(this.getNewGuiScale());
//            this.width = UResolution.getScaledWidth();
//            this.height = UResolution.getScaledHeight();
//        }
    }

    public void initScreen(final int width, final int height) {
        super.initGui();
    }

    public void onDrawScreen(final UMatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
//        Intrinsics.checkNotNullParameter((Object)matrixStack, "matrixStack");
        matrixStack.runWithGlobalState(
//                (Function0)new UScreen$onDrawScreen.UScreen$onDrawScreen$1(this, mouseX, mouseY, partialTicks)
                () -> {
                    super.drawScreen(mouseX, mouseY, partialTicks);
                    return null;
                }
        );
    }

//    @Deprecated(message = "For 1.17 this method requires you pass a UMatrixStack as the first argument.\n\nIf you are currently extending this method, you should instead extend the method with the added argument.\nNote however for this to be non-breaking, your parent class needs to transition before you do.\n\nIf you are calling this method and you cannot guarantee that your target class has been fully updated (such as when\ncalling an open method on an open class), you should instead call the method with the \"Compat\" suffix, which will\ncall both methods, the new and the deprecated one.\nIf you are sure that your target class has been updated (such as when calling the super method), you should\n(for super calls you must!) instead just call the method with the original name and added argument.", replaceWith = @ReplaceWith(expression = "onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)", imports = {}))
    public void onDrawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.onDrawScreen(UMatrixStack.Compat.INSTANCE.get(), mouseX, mouseY, partialTicks);
    }

    private final void onDrawScreenCompat(final UMatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        UMatrixStack.Compat.INSTANCE.runLegacyMethod(matrixStack,
                () -> {
                    onDrawScreen(mouseX, mouseY, partialTicks);
                    return null;
                }
        );
    }

    public void onKeyPressed(final int keyCode, final char typedChar, final UKeyboard.Modifiers modifiers) {
        try {
            super.keyTyped(typedChar, keyCode);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onKeyReleased(final int keyCode, final char typedChar, final UKeyboard.Modifiers modifiers) {
    }

    public void onMouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
        try {
            super.mouseClicked((int)mouseX, (int)mouseY, mouseButton);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMouseReleased(final double mouseX, final double mouseY, final int state) {
        super.mouseReleased((int)mouseX, (int)mouseY, state);
    }

    public void onMouseDragged(final double x, final double y, final int clickedButton, final long timeSinceLastClick) {
        super.mouseClickMove((int)x, (int)y, clickedButton, timeSinceLastClick);
    }

    public void onMouseScrolled(final double delta) {
    }

    public void onTick() {
        super.updateScreen();
    }

    public void onScreenClose() {
        super.onGuiClosed();
    }

    public void onDrawBackground(final UMatrixStack matrixStack, final int tint) {
//        Intrinsics.checkNotNullParameter((Object)matrixStack, "matrixStack");
        matrixStack.runWithGlobalState(
//                (Function0)new UScreen$onDrawBackground.UScreen$onDrawBackground$1(this, tint)
                () -> {
                    super.drawWorldBackground(tint);
                    return null;
                }
        );
    }

//    @Deprecated(message = "For 1.17 this method requires you pass a UMatrixStack as the first argument.\n\nIf you are currently extending this method, you should instead extend the method with the added argument.\nNote however for this to be non-breaking, your parent class needs to transition before you do.\n\nIf you are calling this method and you cannot guarantee that your target class has been fully updated (such as when\ncalling an open method on an open class), you should instead call the method with the \"Compat\" suffix, which will\ncall both methods, the new and the deprecated one.\nIf you are sure that your target class has been updated (such as when calling the super method), you should\n(for super calls you must!) instead just call the method with the original name and added argument.", replaceWith = @ReplaceWith(expression = "onDrawBackground(matrixStack, tint)", imports = {}))
    public void onDrawBackground(final int tint) {
        this.onDrawBackground(UMatrixStack.Compat.INSTANCE.get(), tint);
    }

    public final void onDrawBackgroundCompat(final UMatrixStack matrixStack, final int tint) {
//        Intrinsics.checkNotNullParameter((Object)matrixStack, "matrixStack");
        UMatrixStack.Compat.INSTANCE.runLegacyMethod(matrixStack, () -> {
            onDrawBackground(tint);
            return null;
        });
    }

//    public UScreen(final boolean restoreCurrentGuiOnClose) {
//        this(restoreCurrentGuiOnClose, 2, null); // Hack: gui scale 我乱填的
//    }

//    public UScreen() {
//        this(false, 3, null); // Hack: gui scale 我乱填的
//    }

    public static final GuiScreen getCurrentScreen() {
        return UScreen.Companion.getCurrentScreen();
    }

    public static final void displayScreen(final GuiScreen screen) {
        UScreen.Companion.displayScreen(screen);
    }

    static {
        Companion = new UScreen.Companion();
    }

    public static final class Companion
    {
        private Companion() {
        }

        public final GuiScreen getCurrentScreen() {
            return UMinecraft.getMinecraft().currentScreen;
        }

        public final void displayScreen(final GuiScreen screen) {
            UMinecraft.getMinecraft().displayGuiScreen(screen);
        }
    }

    public enum GuiScale {
        Auto,
        Small,
        Medium,
        Large,
        VeryLarge;

        private static final int guiScaleOverride = Integer.parseInt(System.getProperty("essential.guiScaleOverride", "-1"));

        public static GuiScale fromNumber(int number) {
            return GuiScale.values()[number];
        }

        public static GuiScale scaleForScreenSize() {
            return scaleForScreenSize(650);
        }

        public static GuiScale scaleForScreenSize(int step) {
            if (guiScaleOverride != -1) {
                return fromNumber(MathUtil.clamp(guiScaleOverride, 0, 4));
            }

            int width = UResolution.getViewportWidth();
            int height = UResolution.getScaledHeight();
            int widthScale = MathUtil.clamp(width / step, 1, 4);
            int heightScale = MathUtil.clamp(height / (step / 16 * 9), 1, 4);
            return fromNumber(Math.min(widthScale, heightScale));
        }
    }

    public UScreen(boolean restoreCurrentGuiOnClose, int newGuiScale, String unlocalizedName) {
        this.restoreCurrentGuiOnClose = restoreCurrentGuiOnClose;
        this.newGuiScale = newGuiScale;
        this.unlocalizedName = unlocalizedName;
        this.guiScaleToRestore = -1;
        this.screenToRestore = this.restoreCurrentGuiOnClose ? Companion.getCurrentScreen() : null;
    }

    public UScreen(boolean restoreCurrentGuiOnClose, int newGuiScale) {
        this(restoreCurrentGuiOnClose, newGuiScale, (String)null);
    }

    public UScreen(boolean restoreCurrentGuiOnClose, GuiScale newGuiScale) {
        this(
                restoreCurrentGuiOnClose,
                newGuiScale.ordinal());
    }

    public UScreen(boolean restoreCurrentGuiOnClose) {
        this(restoreCurrentGuiOnClose, 0, null);
    }


    public UScreen() {
        this(false, 0, null);
    }

}
