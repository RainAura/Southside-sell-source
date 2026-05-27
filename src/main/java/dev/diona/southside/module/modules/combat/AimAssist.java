package dev.diona.southside.module.modules.combat;

import dev.diona.southside.event.events.StrafeEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.player.Rotation;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;

import java.util.Arrays;

import static dev.diona.southside.Southside.MC.mc;

public class AimAssist extends Module {
    public final Slider rangeValue = new Slider("Range", 4, 1, 8, 0.1);
    public final Slider fovValue = new Slider("FOV", 90, 0, 180, 1);
    public final Slider speedValue = new Slider("Speed", 2, 1, 180, 1);

    public AimAssist(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onStrafe(StrafeEvent event) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) return;
        double range = rangeValue.getValue().doubleValue();
        Entity entity = null;
        double minRotationDifference = Double.MAX_VALUE;

        for (Entity e : mc.world.loadedEntityList) {
            if (e == mc.player) continue;
            if (Target.isTarget(e) && e.isEntityAlive()) {
                if (mc.player.canEntityBeSeen(e)) {
                    if (mc.player.getDistance(e) <= range) {
                        if (RotationUtil.getRotationDifference(e) <= fovValue.getValue().doubleValue()) {
                            double rotationDifference = RotationUtil.getRotationDifference(e);
                            if (rotationDifference < minRotationDifference) {
                                entity = e;
                                minRotationDifference = rotationDifference;
                            }
                        }
                    }
                }
            }
//            if (Target.isTarget(e) && mc.player.canEntityBeSeen(e) &&
//                    mc.player.getDistance(e) <= range && RotationUtil.getRotationDifference(e) <= fovValue.getValue()) {
//                double rotationDifference = RotationUtil.getRotationDifference(e);
//                if (rotationDifference < minRotationDifference) {
//                    entity = e;
//                    minRotationDifference = rotationDifference;
//                }
//            }
        }

        if (entity == null) {
            return;
        }

        Rotation rotation = RotationUtil.limitAngleChange(
                RotationUtil.getPlayerRotation(),
                RotationUtil.toRotation(RotationUtil.getCenter(entity.getEntityBoundingBox()), 1.0F),
                (float) speedValue.getValue().doubleValue()
        );

        rotation.apply();
    }
}
