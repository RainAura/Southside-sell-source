package dev.diona.southside.gui.hud;

import cc.polyfrost.oneconfig.hud.Hud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.libs.universal.UResolution;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.renderer.scissor.Scissor;
import cc.polyfrost.oneconfig.renderer.scissor.ScissorHelper;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.module.modules.render.TargetHUD;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.render.ColorUtil;
import dev.diona.southside.util.render.GLUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemSword;
import org.lwjglx.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static dev.diona.southside.Southside.MC.mc;

public class TargetHud extends Hud {
    private TargetHUD parent;

    public TargetHud(float x, float y, int positionAlignment, float scale, TargetHUD parent) {
        super(x, y, positionAlignment, scale);
        this.parent = parent;
    }

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {

//        GL11.glPushMatrix();

//        RenderUtil.scissorStart(x - 5F, y, this.getWidth(scale, example) + 200F, UResolution.getScaledHeight() - y);
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.disableLighting();

        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        ScissorHelper scissorHelper = ScissorHelper.INSTANCE;
        nanoVGHelper.setupAndDraw(true, vg -> {
            Scissor scissor = scissorHelper.scissor(vg, x - 5f, y, UResolution.getScaledWidth(), UResolution.getScaledHeight());
            float targetY = 0;
            ArrayList<TargetHUDComponent> components = new ArrayList<>(parent.targets.values());
            for (int i = 0; i < components.size(); i++) {
                TargetHUDComponent component = components.get(i);
                if (component.updateTimer.hasReached(10000)) {
                    components.remove(i);
                    i--;
                    continue;
                }
                if (component.updateTimer.hasReached(1000)) {
                    component.y.update(-50 * scale);
                } else {
                    component.y.update(targetY);
                    targetY += 36 * scale;
                }
                component.draw(vg, scale, x, y);
            }
            scissorHelper.resetScissor(vg, scissor);
        });

        RenderUtil.maskScissorStart(x - 5F * scale, y, UResolution.getScaledWidth(), UResolution.getScaledHeight());
//        RenderUtil.drawRect(0, 0, UResolution.getScaledWidth(), UResolution.getScaledHeight(), -1);

        ArrayList<TargetHUDComponent> components = new ArrayList<>(parent.targets.values());
        for (int i = 0; i < components.size(); i++) {
            TargetHUDComponent component = components.get(i);
            component.drawBigHead(scale, x, y);
        }

        RenderUtil.maskScissorEnd();
//
//        GL11.glPopMatrix();
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return 110 * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return 35 * scale;
    }

    public static class TargetHUDComponent {
        public final TimerUtil updateTimer = new TimerUtil();
        private BezierUtil y = new BezierUtil(3, 0);
        private BezierUtil health = new BezierUtil(3, 1);
        private final static float height = 30;
        //        private List<Boolean> blockStatus = new LinkedList<>();
        private int blockTicks = 0;
        private long blockMask = 0;

        public AbstractClientPlayer entity;
        public BezierUtil color = new BezierUtil(10, 0);
        public TargetHUDComponent(AbstractClientPlayer entity) {
            this.entity = entity;
        }

        public void draw(long vg, float scale, float x, float yOffset) {
            int blockStatus = (entity.isHandActive() && entity.getHeldItemMainhand().getItem() instanceof ItemSword) ? 1 : 0;
            blockMask = (blockMask << 1) | blockStatus;
            blockTicks += blockStatus;
            if ((blockMask & (1L << 40)) != 0) {
                blockTicks--;
                blockMask ^= (1L << 40);
            }

            if (this.y.get() < -height * scale) return;
            float y = this.y.get() + yOffset;

            int alpha = 120;

            String extraMessage = "Health: " + MathUtil.round(entity.getHealth(), 1) + " Block Rate: " + (blockTicks * 2.5) + "%";

            NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
            ScissorHelper scissorHelper = ScissorHelper.INSTANCE;
            nanoVGHelper.drawRect(vg, x, y, 110 * scale, 30 * scale, new Color(0, 0, 0, alpha).getRGB());
            if (this.entity == KillAura.getTarget()) {
                color.update(1);
                color.set(1);
                nanoVGHelper.drawRect(vg, x, (float) (y + 2.5 * scale), 1 * scale, 25 * scale, Color.WHITE.getRGB());
                nanoVGHelper.drawDropShadow(vg, x, (float) (y + 2.5 * scale), 1 * scale, 25 * scale, 8, 0.01F, 0, new Color(255, 255, 255, 150));
            } else {
                color.update(0);
                nanoVGHelper.drawDropShadow(vg, x, (float) (y + 2.5 * scale), 1 * scale, 25 * scale, 8, 0.01F, 0, new Color(255, 255, 255, (int) MathUtil.interpolateFloat(0, 150, color.get())));
                nanoVGHelper.drawRect(vg, x, (float) (y + 2.5 * scale), 1 * scale, 25 * scale, new Color(255, 255, 255, (int) MathUtil.interpolateFloat(alpha, 255, color.get())).getRGB());
            }

            nanoVGHelper.drawTextWithFormatting(vg, entity.getDisplayName().getFormattedText(), x + 30 * scale, y + 11 * scale, -1, 10 * scale, Fonts.WQY);
            nanoVGHelper.drawTextWithFormatting(vg, extraMessage, x + 30.5F * scale, y + 22 * scale, -1, 4 * scale, Fonts.WQY);

            float rate = entity.getHealth() / entity.getMaxHealth();
            health.update(rate);
            nanoVGHelper.drawRect(vg, x, y, 110 * scale * health.get(), 30 * scale, new Color(255, 255, 255, 30).getRGB());
        }

        public void drawBigHead(float scale, float x, float y) {
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y + this.y.get(), 0f);
            GLUtil.startBlend();
            GL11.glScalef(scale * 0.7f, scale * 0.7f, scale * 0.7f);
            mc.getTextureManager().bindTexture(entity.getLocationSkin());
            RenderUtil.drawScaledCustomSizeModalCircle(7f, 6f, 8, 8, 8, 8, 30, 30, 64f, 64f);
            GLUtil.endBlend();
            GL11.glPopMatrix();
        }
    }
}
