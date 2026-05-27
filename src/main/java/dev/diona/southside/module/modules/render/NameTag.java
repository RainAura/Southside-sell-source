package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.event.events.NewRender2DEvent;
import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.module.modules.misc.HackerDetector;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

import static dev.diona.southside.Southside.MC.mc;

public class NameTag extends Module {
    private static NameTag INSTANCE;

    public NameTag(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public final void onRender2DEvent(final NewRender2DEvent event) {
        for (final var entity : mc.world.loadedEntityList) {
            if (Target.isTargetIgnoreTeam(entity)) {
                this.drawNameTag((EntityLivingBase) entity, false, event.getPartialTicks());
            }
        }
    }

    private void drawNameTag(EntityLivingBase entity, boolean bloom, float partialTicks) {
        if (entity == mc.player) return;
        float fontSize = 14f;

        boolean team;
        String name = entity.getDisplayName().getFormattedText();
        if (entity instanceof EntityPlayer) {
            if (!Target.isTarget(entity) && Target.isTargetIgnoreTeam(entity)) {
                team = true;
            } else {
                team = false;
            }
        } else {
            team = false;
        }

        Vec2f pos = RenderUtil.entityScreenPos(entity, partialTicks);
        if (pos == null) return;

        if (team) {
            name = TextFormatting.GREEN + "[Team] " + TextFormatting.RESET + name;
        }
        HackerDetector hackerDetector = (HackerDetector) Southside.moduleManager.getModuleByClass(HackerDetector.class);
        boolean hacker = hackerDetector.isEnabled() && entity instanceof EntityPlayer player && hackerDetector.isHacker(player);
        if (hacker) {
            name = TextFormatting.RED + "[Hacker] " + TextFormatting.RESET + name;
        }

        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        String finalName = name;
        nanoVGHelper.setupAndDraw(true, vg -> {

            float width = nanoVGHelper.getTextWidth(vg, finalName, fontSize, Fonts.WQY);
            float extraWidth = 60f;
            float fontHeight = nanoVGHelper.getTextHeight(vg, fontSize, Fonts.WQY);

            float right = pos.x + (width + extraWidth) / 2F;
            float left = pos.x - (width + extraWidth) / 2F;
            float top = pos.y + fontHeight - 60f;
            float bottom = pos.y - 10f;
            float rate = entity.getHealth() / entity.getMaxHealth();
            if (bloom) {
//                RenderUtil.drawRect(left, top, right, bottom, Color.BLACK.getRGB());
                nanoVGHelper.drawRect(vg, left, top, right - left, bottom - top, Color.BLACK.getRGB());
            } else {
                nanoVGHelper.drawRect(vg, left, top, right - left, bottom - top, new Color(20, 20, 20, 130).getRGB());
                nanoVGHelper.drawRect(vg, left, pos.y - 10f, (right - left) * rate, 3f, team ? new Color(115, 201, 145).getRGB() : new Color(200, 200, 200, 200).getRGB());
            }
            nanoVGHelper.drawTextWithFormatting(vg, finalName, left + 5F, top + 13f, -1, fontSize, Fonts.WQY);
            nanoVGHelper.drawTextWithFormatting(vg, "Health: " + String.format("%.1f", entity.getHealth()), left + 5F, top + 28f, -1, 8f, Fonts.WQY);
//            Southside.fontManager.wqy_microhei.drawString(fontSize, finalName, left + 8F, top + fontHeight * 0.5F, Color.WHITE);
//            Southside.fontManager.wqy_microhei.drawString(8, "Health: " + String.format("%.1f", entity.getHealth()), left + 8F, top + fontHeight * 1.7F, Color.WHITE);

        });
    }

    public static boolean renderVanillaNameTags() {
        return !INSTANCE.isEnabled();
    }
}
