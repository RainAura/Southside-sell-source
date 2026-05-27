package dev.diona.southside.managers;

import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.blur.KawaseBloom;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.GL11;

import static dev.diona.southside.Southside.MC.mc;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class RenderManager {
    public static long vg;
    public RenderManager() {
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        Southside.eventBus.subscribe(this);
    }

    public static ScaledResolution sr = new ScaledResolution(mc);

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    @EventListener(priority = ListenerPriority.LOWEST)
    public void onRender2D(Render2DEvent event) {
        sr = event.getSr();

        int iconified = GLFW.glfwGetWindowAttrib(Display.getWindow(), GLFW.GLFW_ICONIFIED);
        if (iconified == GLFW.GLFW_TRUE) {
            return;
        }

        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        Bloom2DEvent preBloom2DEvent = new Bloom2DEvent(EventState.PRE, event.getSr(), event.getPartialTicks());
        Southside.eventBus.post(preBloom2DEvent);

        stencilFramebuffer.unbindFramebuffer();
        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, 2, 3);
        Bloom2DEvent postBloom2DEvent = new Bloom2DEvent(EventState.POST, event.getSr(), event.getPartialTicks());
        Southside.eventBus.post(postBloom2DEvent);
    }

    public static void beginNvgFrame() {
        NanoVG.nvgSave(vg);
        NanoVG.nvgBeginFrame(vg, Display.getWidth(), Display.getHeight(), 1);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    }

    public static void endNvgFrame() {
        NanoVG.nvgRestore(vg);
        NanoVG.nvgEndFrame(vg);
        GL11.glPopAttrib();
    }
}
