package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.module.modules.misc.hackerdetector.check.NormalCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.MovementUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class VelocityCheck implements NormalCheck {
    @Override
    public void onUpdate(PlayerData playerData) {
        EntityPlayer player = playerData.getPlayer();
        if (player != null) {
            if (player.hurtTime > 6 && player.isSprinting()) {
                if (player.posY - Math.floor(player.posY) == 0.42F) {
                    playerData.addVl(1,"VelocityCheck");
                }
            }
        }
    }
}
