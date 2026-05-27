package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static dev.diona.southside.Southside.MC.mc;

public class AntiBot extends Module {
    private static AntiBot INSTANCE;
    private Set<UUID> grounds = new HashSet<>();
    public Switch groundValue = new Switch("No Ground", false);
    public Switch armorValue = new Switch("No Armor", false);
    public Switch livingTimeSwitchValue = new Switch("Living Time", false);
    public Slider livingTimeValue = new Slider("Living Time Ticks", 0, 0, 200, 1);
    public Switch sleepValue = new Switch("No Sleeping", false);

    public AntiBot(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();

        addDependency(this.livingTimeValue.getLabel(), this.livingTimeSwitchValue.getLabel());
    }

    @Override
    public boolean onEnable() {
        this.clear();
        return true;
    }

    @Override
    public boolean onDisable() {
        this.clear();
        return true;
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        this.clear();
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof SPacketEntity packet) {
            if (packet.getEntity(mc.world) instanceof EntityPlayer player) {
                if (packet.getOnGround() && !grounds.contains(player.getUniqueID())) {
                    grounds.add(player.getUniqueID());
                }
            }
        }
    }

    private boolean checkEntity(EntityPlayer entity) {
        if (groundValue.getValue() && !grounds.contains(entity.getUniqueID())) return true;
        if (livingTimeSwitchValue.getValue() && entity.ticksExisted <= livingTimeValue.getValue().intValue()) return true;
        if (armorValue.getValue() && !this.hasArmor(entity)) return true;
        if (sleepValue.getValue() && entity.isPlayerSleeping()) return true;
        return false;
    }

    private boolean hasArmor(EntityPlayer entity) {
        if (!entity.inventory.armorItemInSlot(0).isEmpty()) return true;
        if (!entity.inventory.armorItemInSlot(1).isEmpty()) return true;
        if (!entity.inventory.armorItemInSlot(2).isEmpty()) return true;
        if (!entity.inventory.armorItemInSlot(3).isEmpty()) return true;
        return false;
    }

    public static boolean isBot(EntityPlayer entity) {
        if (!INSTANCE.isEnabled()) return false;
        return INSTANCE.checkEntity(entity);
    }

    private void clear() {
        grounds.clear();
    }
}
