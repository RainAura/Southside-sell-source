package cc.polyfrost.oneconfig.libs.universal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class UResolution {
    public static float getScaleFactor() {
        final var scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledResolution.getScaleFactor();
    }


    public static final int getScaledWidth() {
        final var scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledResolution.getScaledWidth();
    }

    public static final int getScaledHeight() {
        final var scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledResolution.getScaledHeight();
    }

    public static final int getWindowWidth() {
        return UMinecraft.getMinecraft().displayWidth;
    }
    public static final int getWindowHeight() {
        return UMinecraft.getMinecraft().displayHeight;
    }
    public static final int getViewportWidth() {
        return UMinecraft.getMinecraft().displayWidth;
    }
    public static final int getViewportHeight() {
        return UMinecraft.getMinecraft().displayHeight;
    }
}
