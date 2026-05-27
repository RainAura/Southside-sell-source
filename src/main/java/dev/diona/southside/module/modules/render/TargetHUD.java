package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.HUD;
import cc.polyfrost.oneconfig.hud.Position;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.AttackEvent;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.gui.hud.TargetHud;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.render.GLUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemSword;
import org.lwjglx.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

import static dev.diona.southside.Southside.MC.mc;

public class TargetHUD extends Module {
    public final HUD hud = new HUD(
            "Target HUD",
            new TargetHud(
                100,
                -30,
                Position.AnchorPosition.MIDDLE_CENTER.ordinal(),
                1,
                this
            )
    );
    public TargetHUD(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public final HashMap<UUID, TargetHud.TargetHUDComponent> targets = new HashMap<>();

    private void updateTarget(AbstractClientPlayer player) {
        if (!targets.containsKey(player.getUniqueID())) {
            targets.put(player.getUniqueID(), new TargetHud.TargetHUDComponent(player));
        } else {
            targets.get(player.getUniqueID()).entity = player;
        }
        targets.get(player.getUniqueID()).updateTimer.reset();
    }

    @EventListener
    public void onAttack(AttackEvent event) {
        if (event.getState() != EventState.PRE) return;
        if (event.getTargetEntity() instanceof AbstractClientPlayer player) {
            this.updateTarget(player);
        }
    }

    @EventListener(priority = ListenerPriority.LOW)
    public void onUpdate(UpdateEvent event) {
        if (KillAura.getTargets() == null) return;
        KillAura.getTargets().forEach(entityLivingBase -> {
            if (entityLivingBase instanceof AbstractClientPlayer player) {
                this.updateTarget(player);
            }
        });
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        targets.clear();
    }
}
