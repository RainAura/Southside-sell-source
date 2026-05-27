package cc.polyfrost.oneconfig.libs.universal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;

public class UMinecraft {

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static WorldClient getWorld() {
        return Minecraft.getMinecraft().world;
    }

    public static void setGuiScaleValue(int value) {
        UMinecraft.getSettings().guiScale = value;
    }

    public static GameSettings getSettings() {
        return UMinecraft.getMinecraft().gameSettings;
    }

    public static final int getGuiScale() {
        return getGuiScaleValue();
    }

    private static final int getGuiScaleValue() {
        return UMinecraft.getSettings().guiScale;
    }


    public static final void setGuiScale(int value) {
        setGuiScaleValue(value);
    }
}
