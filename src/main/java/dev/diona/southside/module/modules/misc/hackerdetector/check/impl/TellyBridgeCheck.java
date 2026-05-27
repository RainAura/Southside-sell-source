package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.module.modules.misc.hackerdetector.check.NormalCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;

public class TellyBridgeCheck implements NormalCheck {
    @Override
    public void onUpdate(PlayerData playerData) {
        EntityPlayer player = playerData.getPlayer();
        if (Math.abs(player.rotationYaw - playerData.lastTickYaw) > 45 && Math.abs(player.rotationYaw - playerData.lastTickYaw) < 60
                && player.isSprinting() && !player.onGround && player.isSwingInProgress && player.inventory.getCurrentItem().getItem() instanceof ItemBlock) {
            playerData.addVl(0.5F, "TellyBridgeCheck");
        }
        playerData.lastTickYaw = player.rotationYaw;
    }
}
