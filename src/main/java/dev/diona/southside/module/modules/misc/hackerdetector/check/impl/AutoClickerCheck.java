package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.modules.misc.HackerDetector;
import dev.diona.southside.module.modules.misc.hackerdetector.check.PacketCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketAnimation;


public class AutoClickerCheck implements PacketCheck {
    HackerDetector hackerDetector = (HackerDetector) Southside.moduleManager.getModuleByClass(HackerDetector.class);
    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketAnimation animation && animation.getAnimationType() == 0) {
            Entity entity =  Minecraft.getMinecraft().world.getEntityByID(animation.getEntityID());
            if (entity instanceof EntityPlayer) {
                PlayerData playerData = hackerDetector.getPlayData(entity.getUniqueID());
                if (playerData != null) {
                    playerData.getCpsCache().put(System.currentTimeMillis(), 0L);
//                    ChatUtil.info(String.valueOf(playerData.getCpsCache().asMap().keySet().stream().filter(timestamp -> System.currentTimeMillis() - timestamp <= 1000).count()));
                    if (playerData.getCpsCache().asMap().keySet().stream().filter(timestamp -> System.currentTimeMillis() - timestamp <= 1000).count() > 10) {
                        playerData.addVl(0.1F,"AutoClicker");
                    }
                }
            }
        }
    }
}
