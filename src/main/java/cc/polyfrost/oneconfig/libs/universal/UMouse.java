package cc.polyfrost.oneconfig.libs.universal;

import org.lwjglx.input.Mouse;

public class UMouse {
    public static final class Raw {
        public static double getX() {
            return Mouse.getX();
        }
        public static double getY() {
            return (double)UResolution.getWindowHeight() - (double)Mouse.getY() - 1.0;
        }
    }
}
