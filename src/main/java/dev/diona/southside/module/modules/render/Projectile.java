package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.movement.Stuck;
import dev.diona.southside.module.modules.player.invmanager.subcomponents.TNTComponent;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.world.ProjectileUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.util.math.*;
import org.lwjglx.opengl.GL11;
import org.lwjglx.util.glu.Cylinder;
import org.lwjglx.util.glu.Disk;

import java.awt.*;

public class Projectile extends Module {
    public Projectile(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private BezierUtil x = new BezierUtil(10, 0);
    private BezierUtil y = new BezierUtil(10, 0);
    private BezierUtil z = new BezierUtil(10, 0);
    private BezierUtil rotX = new BezierUtil(4, 0);
    private BezierUtil rotZ = new BezierUtil(4, 0);
    public Switch TNTValue = new Switch("TNT", true);

    @EventListener
    public void onRender3D(Render3DEvent e) {
        boolean isBow = false;
        float pitchDifference = 0.0F;
        float motionFactor = 1.5F;
        float motionSlowdown = 0.99F;
        if (mc.player.getHeldItemMainhand() != null) {
            float gravity, size;
            Item heldItem = mc.player.getHeldItemMainhand().getItem();


            if (heldItem instanceof net.minecraft.item.ItemBow) {
                isBow = true;
                gravity = 0.05F;
                size = 0.3F;
                float power = mc.player.getItemInUseMaxCount() / 20.0F;
                power = (power * power + power * 2.0F) / 3.0F;

                if (power < 0.1D) {
                    return;
                }

                if (power > 1.0F) {
                    power = 1.0F;
                }

                motionFactor = power * 3.0F;

//                ChatUtil.info(mc.player.getItemInUseMaxCount() + "");
            } else if (heldItem instanceof net.minecraft.item.ItemFishingRod) {
                gravity = 0.04F;
                size = 0.25F;
                motionSlowdown = 0.92F;
            } else if (heldItem instanceof ItemSplashPotion) {
                gravity = 0.03F;
                size = 0.25F;
                pitchDifference = -20.0F;
                motionFactor = 0.5F;
            } else if (heldItem.equals(TNTComponent.TNTItem) && this.TNTValue.getValue()) {
                gravity = 0.05F;
                size = 0.25F;
            } else {
                if (!(heldItem instanceof net.minecraft.item.ItemSnowball) && !(heldItem instanceof net.minecraft.item.ItemEnderPearl) && !(heldItem instanceof net.minecraft.item.ItemEgg)) {
                    return;
                }

                gravity = 0.03F;
                size = 0.25F;
            }


            double posX = (mc.getRenderManager()).getRenderPosX() - (MathHelper.cos(mc.player.rotationYaw / 180.0F * 3.1415927F) * 0.16F);
            double posY = mc.getRenderManager().getRenderPosY() + mc.player.getEyeHeight() - 0.10000000149011612D;


            double posZ = mc.getRenderManager().getRenderPosZ() - (MathHelper.sin(mc.player.rotationYaw / 180.0F * 3.1415927F) * 0.16F);

            double motionX = (-MathHelper.sin(mc.player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(mc.player.rotationPitch / 180.0F * 3.1415927F)) * (isBow ? 1.0D : 0.4D);

            double motionY = -MathHelper.sin((mc.player.rotationPitch + pitchDifference) / 180.0F * 3.1415927F) * (isBow ? 1.0D : 0.4D);


            double motionZ = (MathHelper.cos(mc.player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(mc.player.rotationPitch / 180.0F * 3.1415927F)) * (isBow ? 1.0D : 0.4D);
            float distance = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;

            if (!Stuck.isStuck()) {
                motionY += mc.player.motionY;
            }

            ProjectileUtil.ProjectileHit projectileHit = ProjectileUtil.predict(posX, posY, posZ, motionX, motionY, motionZ, motionSlowdown, size, gravity, true);

            GL11.glEnd();
            GL11.glPushMatrix();

            this.x.update((float) (projectileHit.posX() - mc.getRenderManager().getRenderPosX()));
            this.y.update((float) (projectileHit.posY() - mc.getRenderManager().getRenderPosY()));
            this.z.update((float) (projectileHit.posZ() - mc.getRenderManager().getRenderPosZ()));

            GL11.glTranslated(x.get(), y.get(), z.get());

            if (projectileHit.landingPosition() != null) {

                int side = projectileHit.landingPosition().sideHit.getIndex();

                if (side == 1 && heldItem instanceof net.minecraft.item.ItemEnderPearl) {
                    RenderUtil.color((new Color(105, 140, 255)).getRGB());
                } else if (side == 2) {
                    rotX.update(1);
                    rotZ.update(0);
                    GlStateManager.rotate(90.0F, rotX.get(), 0, rotZ.get());
                } else if (side == 3) {
                    rotX.update(1);
                    rotZ.update(0);
                    GlStateManager.rotate(90.0F, rotX.get(), 0, rotZ.get());
                } else if (side == 4) {
                    rotX.update(0);
                    rotZ.update(1);
                    GlStateManager.rotate(90.0F, rotX.get(), 0, rotZ.get());
                } else if (side == 5) {
                    rotX.update(0);
                    rotZ.update(1);
                    GlStateManager.rotate(90.0F, rotX.get(), 0, rotZ.get());
                }

                if (projectileHit.hitEntity()) {
                    RenderUtil.color((new Color(105, 140, 255)).getRGB());
                }
            }


            renderPoint();
            GL11.glPopMatrix();
            RenderUtil.disableRender3D(true);
        }
    }

    private void renderPoint() {
//        GL11.glBegin(1);
//        GL11.glVertex3d(-0.5D, 0.0D, 0.0D);
//        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
//        GL11.glVertex3d(0.0D, 0.0D, -0.5D);
//        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
//        GL11.glVertex3d(0.5D, 0.0D, 0.0D);
//        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
//        GL11.glVertex3d(0.0D, 0.0D, 0.5D);
//        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
//        GL11.glEnd();
//        Cylinder c = new Cylinder();
//        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
//
//        c.setDrawStyle(100011);
//        c.draw(0.5F, 0.5F, 0.0F, 256, 27);
        Disk d1 = new Disk();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);

        d1.setDrawStyle(100011);
        d1.draw(0.5F, 0.5F, 6, 256);

        Disk d2 = new Disk();

        d2.setDrawStyle(100011);
        d2.draw(0.1F, 0.1F, 6, 256);
    }
}
