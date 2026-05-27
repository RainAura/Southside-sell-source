package dev.diona.southside.module.modules.client;

import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class Radar extends Module {
    public Radar(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void on2DEvent(Render2DEvent event) {
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(Minecraft.getMinecraft().objectMouseOver.entityHit != null ? Minecraft.getMinecraft().objectMouseOver.entityHit.toString() : "null", 10, 10, -1);
        final Minecraft mc = Minecraft.getMinecraft();
        final double xoffest = 150f;
        final double yoffest = 150f;
        final double size = 3f;
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase living) {
                if (living.isEntityAlive()) {
                    final double posx = (living.posX - mc.player.posX) * size;
                    final double posz = (living.posZ - mc.player.posZ) * size;
                    final double angle = mc.player.rotationYaw * (Math.PI / 180.0f);
                    final double cos = Math.cos(angle);
                    final double sin = Math.sin(angle);
                    double rotX = -(posx * cos + posz * sin);
                    double rotY = -(posz * cos - posx * sin);
                    if (living == mc.player) {
                        RenderUtil.drawGradientRect(xoffest + rotX, yoffest + rotY, xoffest + rotX + 2, yoffest + rotY + 2, new Color(255, 0, 0).getRGB(), new Color(255, 0, 0).getRGB());
                    } else {
                        RenderUtil.drawGradientRect(xoffest + rotX, yoffest + rotY, xoffest + rotX + 2, yoffest + rotY + 2, -1, -1);
                    }
                }
            }
        }
    }
}
