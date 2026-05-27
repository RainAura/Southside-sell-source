package dev.diona.southside.util.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Margele
 */
public class MaskUtil {

    public static void defineMask() {
        GlStateManager.depthMask(true);
        GlStateManager.clearDepth(1);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_ALWAYS);
        GlStateManager.colorMask(false, false, false, false);
    }

    /**
     * Finish defining the screen mask
     */
    public static void finishDefineMask() {
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
    }

    /**
     * Start drawing only on the masked area
     */
    public static void drawOnMask() {
        glDepthFunc(GL_EQUAL);

    }

    /**
     * Start drawing only off the masked area.
     */
    public static void drawOffMask() {
        glDepthFunc(GL_NOTEQUAL);
    }

    /**
     * Start drawing only minecraft masked area.
     */
    public static void drawMCMask() {
        glDepthFunc(GL_LEQUAL);
    }

    /**
     * Reset the masked area - should be done after you've finished rendering
     */
    public static void resetMask() {
        GlStateManager.clearDepth(1.0);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(false);
    }
}
