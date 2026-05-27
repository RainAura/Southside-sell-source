package dev.diona.southside.gui.hud;

import cc.polyfrost.oneconfig.hud.Hud;
import cc.polyfrost.oneconfig.internal.gui.BlurHandler;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.libs.universal.UResolution;
import cc.polyfrost.oneconfig.libs.universal.UScreen;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.renderer.scissor.Scissor;
import cc.polyfrost.oneconfig.renderer.scissor.ScissorHelper;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.module.modules.render.PotionEffects;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.render.GLUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ChatType;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static dev.diona.southside.Southside.MC.mc;

public class PotionHud extends Hud {
    private final HashMap<String, PotionEffectComponent> potionEffects = new HashMap<>();

    public PotionHud(float x, float y, int positionAlignment, float scale) {
        super(x, y, positionAlignment, scale);
    }

    private final BezierUtil height = new BezierUtil(4, 200);

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        scale *= 0.1F;
        final Collection<PotionEffect> effectCollections = mc.player.getActivePotionEffects();
        if ((height.get() < 1 && !example) && effectCollections.isEmpty()) return;
        ScissorHelper scissorHelper = ScissorHelper.INSTANCE;
        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        final float topbar = 73 * scale;
        float finalScale = scale;
        nanoVGHelper.setupAndDraw(true, vg -> {
            float potionY = topbar;

            Scissor scissorTop = scissorHelper.scissor(vg, x, y, 600 * finalScale, height.get());
            nanoVGHelper.drawRect(vg, x, y, 600 * finalScale, height.get() + topbar, new Color(0, 0, 0, 120).getRGB());
            nanoVGHelper.drawDropShadow(vg, x, y, 600 * finalScale, height.get() + topbar, 10, 1, 0);
            nanoVGHelper.drawRect(vg, x, y + 10 * finalScale, 10 * finalScale, 50 * finalScale, new Color(255, 255, 255).getRGB());
            nanoVGHelper.drawText(vg, "Potion Effects", x + 20 * finalScale, y + 39 * finalScale, -1, (40 * finalScale), Fonts.SEMIBOLD);
            scissorHelper.resetScissor(vg, scissorTop);

            Scissor scissor = scissorHelper.scissor(vg, x, y + topbar, 600 * finalScale, height.get() - topbar);

            if (example) {
                height.update(potionY);
            } else {
                for (PotionEffect e : effectCollections) {
                    if (!potionEffects.containsKey(e.getEffectName())) {
                        potionEffects.put(e.getEffectName(), new PotionEffectComponent(e));
                    }
                    PotionEffectComponent component = potionEffects.get(e.getEffectName());
                    component.effect = e;
                    component.y.update(potionY);
                    component.draw(vg, x, y, finalScale);
                    potionY += 150 * finalScale;
                }
                for (PotionEffectComponent component : potionEffects.values()) {
                    if (!effectCollections.contains(component.effect)) {
                        component.y.update(-300 * finalScale);
                        component.draw(vg, x, y, finalScale);
                    }
                }
                if (potionY > topbar) height.update(potionY);
                else height.update(0);
            }

            scissorHelper.resetScissor(vg, scissor);
        });

        if (!example) {
            nanoVGHelper.setupAndDraw(vg -> {
                Scissor scissor = scissorHelper.scissor(vg, x, y + topbar, 600 * finalScale, height.get() - topbar);

                for (PotionEffect e : effectCollections) {
                    if (!potionEffects.containsKey(e.getEffectName())) {
                        potionEffects.put(e.getEffectName(), new PotionEffectComponent(e));
                    }
                    PotionEffectComponent component = potionEffects.get(e.getEffectName());
                    component.effect = e;
//                    component.drawIcon(x, y, scale);
                }
    //            height.update(potionY);

                scissorHelper.resetScissor(vg, scissor);
            });
            RenderUtil.maskScissorStart(x, y + topbar, 600 * scale, UResolution.getScaledHeight());

//            RenderUtil.scissorEnd();
            for (PotionEffectComponent component : potionEffects.values()) {
//                if (!effectCollections.contains(component.effect)) {
                    component.drawIcon(x, y, scale);
//                }
            }

            RenderUtil.maskScissorEnd();
        }
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return 600 * 0.1F * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        if (height == null) return 150 * 0.1F * scale;
        return height.target;
    }

    public static class PotionEffectComponent {
        private final BezierUtil y = new BezierUtil(3, 0);
        private final static float height = 30;
        public PotionEffect effect;

        public PotionEffectComponent(PotionEffect effect) {
            this.effect = effect;
        }

        public void drawIcon(float x, float yOffset, float scale)  {
            if (this.y.get() < -height) return;
            float y = this.y.get() + yOffset;
            Potion potion = effect.getPotion();
            GL11.glPushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(scale * 5F, scale * 5F, scale * 5F);
            mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            if (potion.hasStatusIcon()) {
                int index = potion.getStatusIconIndex();
                mc.ingameGUI.drawTexturedModalRect(4, 6, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
            }
            GlStateManager.disableBlend();
            GL11.glPopMatrix();
        }

        public void draw(long vg, float x, float yOffset, float scale) {
            if (this.y.get() < -height) return;
            float y = this.y.get() + yOffset;

            NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;

            nanoVGHelper.drawRect(vg, x, y, (float) (600 * scale * (1D * effect.getDuration() / effect.getMaxDuration())), 150 * scale, new Color(255, 255, 255, 30).getRGB());

//            this.drawIcon(vg, x, yOffset, scale);

            nanoVGHelper.drawText(vg, InventoryEffectRenderer.getPotionDisplayString(effect), x + 125 * scale, y + 75 * scale, Color.WHITE.getRGB(), 45 * scale, Fonts.REGULAR);
            String duration = Potion.getPotionDurationString(effect, 1.0F);
            nanoVGHelper.drawText(vg, duration, x + 590 * scale - Southside.fontManager.font.getStringWidth(35 * scale, duration), y + 125 * scale, Color.WHITE.getRGB(), 35 * scale, Fonts.REGULAR);
        }
    }
}
