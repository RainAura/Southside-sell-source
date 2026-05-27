package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Font;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.NewRender2DEvent;
import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.GL11;
import org.lwjglx.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ESP2D extends Module {
    public final Switch players = new Switch("Players", true);
    public final Switch animals = new Switch("Animals", true);
    public final Switch mobs = new Switch("Mobs", false);
    public final Switch invisible = new Switch("Invisible", false);
    public final Switch passives = new Switch("Passives", true);

    public final Switch droppedItems = new Switch("Dropped Items", false);
    public final Switch health = new Switch("Health Bar", true);
    public final Switch tags = new Switch("Tags", true);
    public final Switch filled = new Switch("Filled", true);
    public final Switch box = new Switch("Box", true);
    public final Dropdown boxMode = new Dropdown("BoxMode", "Box", "Box", "Corners");
    public final Slider thickness = new Slider("Thickness", 1.5f, 0.25f, 5.0f, 0.25f);

    private final int black = new Color(0, 0, 0, 150).getRGB();
    public ESP2D() {
        super("ESP2D", "Real 2D", Category.Render, true);
//        boxMode.setDisplay(box::getValue);
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(boxMode.getLabel(), box.getLabel());
    }

    @EventListener
    public final void onRender2DEvent(final NewRender2DEvent event) {
        GL11.glPushMatrix();
        double scaling = event.getScaledResolution().getScaleFactor() / Math.pow(event.getScaledResolution().getScaleFactor(), 2.0);
        GlStateManager.scale(scaling, scaling, scaling);
        for (final var entity : mc.world.loadedEntityList) {
            if (isValid(entity) && RenderUtil.isInViewFrustrum(entity)) {
                double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, event.getPartialTicks());
                double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, event.getPartialTicks());
                double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, event.getPartialTicks());
                double width = entity.width / 1.5;
                double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height + 0.05, z + width);
                List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
                Vector4d position = null;
                for (var vector : vectors) {
                    vector = project(event.getScaledResolution().getScaleFactor(), vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                    if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                        }
                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }
                mc.entityRenderer.setupOverlayRendering();
                if (mc.player.getDistance(entity) < 1) continue;
                if (position == null) continue;
                final Color clr = new Color(getColor());
                GL11.glPushMatrix();
                double posX = position.x;
                double posY = position.y;
                double endPosX = position.z;
                double endPosY = position.w;
                final double thickness2 = thickness.getValue().doubleValue();
                if (health.getValue() && entity instanceof EntityLivingBase livingBase) {
                    double hpPercentage = livingBase.getHealth() / livingBase.getMaxHealth();
                    if (hpPercentage > 1)
                        hpPercentage = 1;
                    else if (hpPercentage < 0)
                        hpPercentage = 0;

                    float health = livingBase.getHealth();

                    double hpHeight = (endPosY - posY) * hpPercentage;

                    double difference = posY - endPosY + 0.5;

                    if (health > 0) {
                        RenderUtil.drawOutline(posX - 4, posY - .5, 2, (endPosY - posY) + .5, .5, black);
                        RenderUtil.drawRect(posX - 4, posY - .5, posX - 2, endPosY + .5, new Color(0, 0, 0, 70).getRGB());
                        RenderUtil.drawRect(posX - 3.5, endPosY - hpHeight, posX - 2.5, endPosY, getHealthColor((EntityLivingBase) entity));
                    }

                    if (-difference > 50.0)
                        for (int i = 1; i < 10; ++i) {
                            double increment = difference / 10.0 * i;
                            RenderUtil.drawRect(posX - 3.5, endPosY - 0.5 + increment, posX - 2.5, endPosY - 0.5 + increment - 1.0, black);
                        }
                }
                final var color2 = new Color(255, 255, 255).getRGB();
                if (tags.getValue()) {
                    NanoVGHelper instance = NanoVGHelper.INSTANCE;
                    Font wqy = Fonts.WQY;
                    final var size = 5;
                    double dif = (endPosX - posX) / 2;
                    String name;
                    if (entity instanceof EntityItem item) {
                        name = item.getItem().getDisplayName();
                    } else {
                        name = entity.getDisplayName().getFormattedText();
                    }
                    instance.setupAndDraw(true, vg -> {
                        final var textX = (float)(posX + dif) -  (instance.getTextWidth(vg, name, size, wqy) / 2f);
                        final var textY = (float)((posY - (9 / 1.5f * 2.0f)) + 2.0f);
                        instance.drawRect(vg, textX, textY, instance.getTextWidth(vg, name, size, wqy), instance.getTextHeight(vg, size, wqy), new Color(0, 0, 0, 100).getRGB());
                        //RenderUtil.drawRect(textX, textY, textX + instance.getTextWidth(vg, name, size, wqy), textY + instance.getTextHeight(vg, size, wqy), new Color(0, 0, 0, 100).getRGB());
                        instance.drawRawTextWithFormatting(vg, name, textX, textY, -1, size, wqy);
                    });

                }
                if (filled.getValue())
                    RenderUtil.drawRect(posX, posY, endPosX, endPosY, new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 120).getRGB());
                if (box.getValue()) {
                    switch (boxMode.getMode()) {
                        case "Box" -> {
                            // Left
                            RenderUtil.drawRect(posX - 1, posY, posX + thickness2, endPosY + .5,
                                    black);
                            // Top
                            RenderUtil.drawRect(posX - 1, posY - .5, endPosX + .5, posY + .5 + thickness2,
                                    black);
                            // Right
                            RenderUtil.drawRect(endPosX - .5 - thickness2, posY, endPosX + .5, endPosY + .5,
                                    black);
                            // Bottom
                            RenderUtil.drawRect(posX - 1, endPosY - thickness2 - .5, endPosX + .5, endPosY + .5,
                                    black);

                            // Left
                            RenderUtil.drawRect(posX - .5, posY, posX + thickness2 - .5, endPosY,
                                    color2);
                            // Bottom
                            RenderUtil.drawRect(posX, endPosY - thickness2, endPosX, endPosY,
                                    color2);
                            // Top
                            RenderUtil.drawRect(posX - .5, posY, endPosX, posY + thickness2,
                                    color2);
                            // Right
                            RenderUtil.drawRect(endPosX - thickness2, posY, endPosX, endPosY,
                                    color2);
                        }
                        case "Corners" -> {
                            RenderUtil.drawRect(posX + .5, posY, posX - 1, posY + (endPosY - posY) / 4 + .5,
                                    black);
                            RenderUtil.drawRect(posX - 1, endPosY, posX + .5, endPosY - (endPosY - posY) / 4 - .5,
                                    black);
                            RenderUtil.drawRect(posX - 1, posY - .5, posX + (endPosX - posX) / 3 + .5, posY + 1,
                                    black);
                            RenderUtil.drawRect(endPosX - (endPosX - posX) / 3 - .5, posY - .5, endPosX, posY + 1,
                                    black);
                            RenderUtil.drawRect(endPosX - 1, posY, endPosX + .5, posY + (endPosY - posY) / 4 + .5,
                                    black);
                            RenderUtil.drawRect(endPosX - 1, endPosY, endPosX + .5, endPosY - (endPosY - posY) / 4 - .5,
                                    black);
                            RenderUtil.drawRect(posX - 1, endPosY - 1, posX + (endPosX - posX) / 3 + .5, endPosY + .5,
                                    black);
                            RenderUtil.drawRect(endPosX - (endPosX - posX) / 3 - .5, endPosY - 1, endPosX + .5, endPosY + .5,
                                    black);
                            RenderUtil.drawRect(posX, posY, posX - .5, posY + (endPosY - posY) / 4,
                                    color2);
                            RenderUtil.drawRect(posX, endPosY, posX - .5, endPosY - (endPosY - posY) / 4,
                                    color2);
                            RenderUtil.drawRect(posX - .5, posY, posX + (endPosX - posX) / 3, posY + .5,
                                    color2);
                            RenderUtil.drawRect(endPosX - (endPosX - posX) / 3, posY, endPosX, posY + .5,
                                    color2);
                            RenderUtil.drawRect(endPosX - .5, posY, endPosX, posY + (endPosY - posY) / 4,
                                    color2);
                            RenderUtil.drawRect(endPosX - .5, endPosY, endPosX, endPosY - (endPosY - posY) / 4,
                                    color2);
                            RenderUtil.drawRect(posX, endPosY - .5, posX + (endPosX - posX) / 3, endPosY,
                                    color2);
                            RenderUtil.drawRect(endPosX - (endPosX - posX) / 3, endPosY - .5, endPosX - .5, endPosY,
                                    color2);
                        }
                    }
                }
                GL11.glPopMatrix();
            }
        }
        GL11.glPopMatrix();
    }

    private int getColor() {
        return new Color(255, 255, 255).getRGB();
    }

    private boolean isValid(Entity entity) {
        if (entity instanceof EntityLivingBase livingBase) {
            return isValidType(livingBase) && entity.isEntityAlive() && (!entity.isInvisible() || invisible.getValue());
        } else return droppedItems.getValue() && entity instanceof EntityItem;
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.getValue() && entity instanceof EntityPlayer) || ((mobs.getValue() && (entity instanceof EntityMob || entity instanceof EntitySlime)) || (passives.getValue() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.getValue() && entity instanceof EntityAnimal));
    }

    private Vector3d project(final float scaleFactor, final double x, final double y, final double z) {
        try (final var stack = MemoryStack.stackPush()) {
            final var vector = stack.mallocFloat(4);
            final var modelView = stack.mallocFloat(16);
            final var projection = stack.mallocFloat(16);
            final var viewport = stack.mallocInt(16);
            org.lwjgl.opengl.GL11.glGetFloatv(org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX, modelView);
            org.lwjgl.opengl.GL11.glGetFloatv(org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX, projection);
            org.lwjgl.opengl.GL11.glGetIntegerv(org.lwjgl.opengl.GL11.GL_VIEWPORT, viewport);
            if (GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, vector)) {
                return new Vector3d(vector.get(0) / scaleFactor, ((float)Display.getHeight() - vector.get(1)) / scaleFactor, vector.get(2));
            }
        }
        return null;
    }


    private int getHealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth()) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }
}
