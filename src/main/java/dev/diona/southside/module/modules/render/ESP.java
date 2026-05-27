package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.awt.*;

import static dev.diona.southside.Southside.MC.mc;

public class ESP extends Module {
    public final Dropdown modeValue = new Dropdown("Mode", "Box", "Box", "Glow");
    public ESP(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (Target.isTargetIgnoreTeam(entity)) {
                this.drawESP((EntityLivingBase) entity, event.getPartialTicks());
            }
        }
    }

    @Override
    public boolean onDisable() {
        if (this.modeValue.getMode().equals("Glow")) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityLivingBase entityLivingBase) {
                    entityLivingBase.setFlag(6, false);
                }
            }
        }
        return true;
    }

    private void drawESP(EntityLivingBase entity, float partialTicks) {
        if (entity == null || entity == mc.player) return;
        switch (this.modeValue.getMode()) {
            case "Box" -> {
                if (entity.getEntityBoundingBox() == null) return;
                RenderUtil.drawEntity(entity, new Color(255, 255, 255, 30));
            }
            case "Glow" -> {
                entity.setFlag(6, true);
            }
        }
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }
}
