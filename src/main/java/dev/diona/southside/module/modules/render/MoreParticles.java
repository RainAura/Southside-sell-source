package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.AttackEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

import static dev.diona.southside.Southside.MC.mc;

public class MoreParticles extends Module {
    private static MoreParticles INSTANCE;
    public final Switch criticalValue = new Switch("Critical Particles", true);
    public final Switch sharpnessValue = new Switch("Sharpness Particles", true);
    public MoreParticles(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public void onAttack(AttackEvent event) {
        if (event.getState() != EventState.PRE) return;
        spawnParticles(event.getTargetEntity());
    }

    public static void spawnParticles(Entity entity) {
        if (!INSTANCE.isEnabled()) return;
        if (INSTANCE.criticalValue.getValue()) {
            mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
        }
        if (INSTANCE.sharpnessValue.getValue()) {
            mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
        }
    }
}
