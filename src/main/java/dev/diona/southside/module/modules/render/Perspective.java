package dev.diona.southside.module.modules.render;

import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;

import java.util.Objects;

import static dev.diona.southside.Southside.MC.mc;

public class
Perspective extends Module {
    private static Perspective INSTANCE;
    private static final int KEY_CODE = Keyboard.KEY_LMENU;
    public boolean perspectiveToggled;
    private float cameraYaw;
    private float cameraPitch;
    private int previousPerspective;

    public Perspective(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (!perspectiveToggled) {
            if (Keyboard.isKeyDown(KEY_CODE)) {
                perspectiveToggled = true;
                cameraYaw = mc.player.rotationYaw;
                cameraPitch = mc.player.rotationPitch;
                previousPerspective = mc.gameSettings.thirdPersonView;
                mc.gameSettings.thirdPersonView = 1;
            }
        } else if (!Keyboard.isKeyDown(KEY_CODE)) {
            perspectiveToggled = false;
            mc.gameSettings.thirdPersonView = previousPerspective;
        }
    }

    public static float getCameraYaw() {
        if (INSTANCE.perspectiveToggled) {
            return INSTANCE.cameraYaw;
        } else {
            return Objects.requireNonNull(mc.getRenderViewEntity()).rotationYaw;
        }
    }

    public static float getCameraPitch() {
        if (INSTANCE.perspectiveToggled) {
            return INSTANCE.cameraPitch;
        } else {
            return Objects.requireNonNull(mc.getRenderViewEntity()).rotationPitch;
        }
    }

    public static float getCameraPrevYaw() {
        if (INSTANCE.perspectiveToggled) {
            return INSTANCE.cameraYaw;
        } else {
            return Objects.requireNonNull(mc.getRenderViewEntity()).prevRotationYaw;
        }
    }

    public static float getCameraPrevPitch() {
        if (INSTANCE.perspectiveToggled) {
            return INSTANCE.cameraPitch;
        } else {
            return Objects.requireNonNull(mc.getRenderViewEntity()).prevRotationPitch;
        }
    }

    public static boolean overrideMouse() {
        if (mc.inGameHasFocus && Display.isActive()) {
            if (!INSTANCE.perspectiveToggled) {
                return true;
            }
            mc.mouseHelper.mouseXYChange();
            float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            float f3 = mc.mouseHelper.deltaX * f2;
            float f4 = -mc.mouseHelper.deltaY * f2;
            INSTANCE.cameraYaw += f3 * 0.15f;
            INSTANCE.cameraPitch += f4 * 0.15f;
            if (INSTANCE.cameraPitch > 90.0f) {
                INSTANCE.cameraPitch = 90.0f;
            }
            if (INSTANCE.cameraPitch < -90.0f) {
                INSTANCE.cameraPitch = -90.0f;
            }
        }
        return false;
    }
}
