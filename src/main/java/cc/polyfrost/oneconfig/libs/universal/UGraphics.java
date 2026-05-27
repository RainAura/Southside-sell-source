package cc.polyfrost.oneconfig.libs.universal;

import net.minecraft.client.renderer.GlStateManager;

public class UGraphics {
    public class GL {
        public static void pushMatrix() {
            GlStateManager.pushMatrix();
        }

        public static void popMatrix() {
            GlStateManager.popMatrix();
        }

        public static void translate(float x, float y, float z) {
            translate(x, y, (double) z);
        }

        public static void translate(double x, double y, double z) {
            GlStateManager.translate(x, y, z);
        }

        public static void rotate(float angle, float x, float y, float z) {
            GlStateManager.rotate(angle, x, y, z);
        }

        public static void scale(float x, float y, float z) {
            scale(x, y, (double) z);
        }

        public static void scale(double x, double y, double z) {
            GlStateManager.scale(x, y, z);
        }
    }

}
