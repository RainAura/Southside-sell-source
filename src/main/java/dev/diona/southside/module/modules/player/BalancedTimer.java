package dev.diona.southside.module.modules.player;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.Minecraft;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.util.HashMap;

public class BalancedTimer extends Module {
    private static BalancedTimer INSTANCE;
    public static final int RELEASE_SPEED = 5;
    private final BezierUtil yAnimation = new BezierUtil(4, 0);
    private final BezierUtil xAnimation = new BezierUtil(4, 0);
    public static int balance = 0;
    public static Stage stage = Stage.IDLE;

    public BalancedTimer(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        balance = 0;
        stage = Stage.IDLE;
    }

    public static void preTick() {
        if (INSTANCE == null || mc.player == null || Alink.isInstanceEnabled() || !INSTANCE.isEnabled()) {
            stage = Stage.IDLE;
            mc.getTimer().tickLength = 50F;
            balance = 0;
            return;
        }
        if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_1)) || Mouse.isButtonDown(3)) {
            mc.playerStuckTicks++;
            stage = Stage.STORE;
            mc.getTimer().tickLength = 50F;
            balance = 0;
        } else if (((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_2)) || Mouse.isButtonDown(4)) && mc.scheduledTasks.size() > 0) {
            stage = Stage.RELEASE;
            mc.getTimer().tickLength = 50F / RELEASE_SPEED;
            balance++;
        } else {
            stage = Stage.IDLE;
            mc.getTimer().tickLength = 50F;
            balance = 0;
        }

        if (mc.scheduledTasks.size() > 0) {
            INSTANCE.yAnimation.update(0);
        } else {
            INSTANCE.yAnimation.update(-30);
            INSTANCE.xAnimation.update(0);
        }
    }

//    @Override
//    public float width() {
//        return 180;
//    }

//    @EventListener
//    public void onBloom2D(Bloom2DEvent event) {
//        super.onUIElementBloom(event);
//        float x = this.xValue.getValue().floatValue();
//        float y = this.yValue.getValue().floatValue() + yAnimation.get();
//        float fontSize = 12F;
//        String text = "Timer Balance: " + Math.max(0, MathUtil.round((mc.scheduledTasks.size() - 1) * 0.05, 1)) + "s";
//        float width = Southside.fontManager.font.getStringWidth(fontSize, text) + 10, height = 20;
//        xAnimation.update(width);
//
//        RenderUtil.scissorStart(this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue(), this.xAnimation.get(), height);
//        RenderUtil.drawRect(x, y, x + this.xAnimation.get(), y + height, new Color(0, 0, 0, 129).getRGB());
//
//        Southside.fontManager.font.drawString(fontSize, text, x + 5, y + 5, Color.WHITE);
//        RenderUtil.scissorEnd();
//    }

    public enum Stage {
        STORE,
        IDLE,
        RELEASE
    }

    public static boolean isInstanceEnabled() {
        return INSTANCE.isEnabled();
    }
}
