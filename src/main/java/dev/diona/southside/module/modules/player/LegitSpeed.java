package dev.diona.southside.module.modules.player;

import dev.diona.southside.event.events.StrafeEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;

public class LegitSpeed extends Module {

    public LegitSpeed(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

//    public Slider speedOption = new Slider("Speed", 1.002F, 1, 1.010F, 0.001);
//    public Slider TimerOption = new Slider("Timer", 1.004f, 1, 1.010F, 0.001);


    @EventListener
    public void onTick(TickEvent event) {
        mc.getTimer().tickLength = 50F / 1.004f;
    }

    @EventListener
    public void onStrafe(StrafeEvent event) {
//        if (mc.player.isPotionActive(Potion.getPotionById(1))) {
//            if (mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier() == 1) {
//                event.setFriction(event.getFriction() * 1.003F);
//
//            } else if (mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier() == 2) {
//                event.setFriction(event.getFriction() * 1.002F);
//
//            } else if (mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier() == 3) {
//                event.setFriction(event.getFriction() * 1.002F);
//            }
//        } else {
//        }
        float friction = 1.002F;
        event.setFriction(event.getFriction() * friction);
    }

    @Override
    public boolean onEnable() {
        return super.onEnable();
    }

    @Override
    public boolean onDisable() {
        mc.getTimer().tickLength = 50F;
        return super.onDisable();
    }
}
