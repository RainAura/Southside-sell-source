package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.module.modules.misc.hackerdetector.check.NormalCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import dev.diona.southside.util.player.MovementUtil;
import net.minecraft.entity.player.EntityPlayer;

public class NoWebCheck implements NormalCheck {

    @Override
    public void onUpdate(PlayerData playerData) {
        EntityPlayer player = playerData.getPlayer();
        if (player != null) {
            if (player.collided && (player.isInWeb || player.isInWater() || player.isInLava()) && !player.isInWeb &&MovementUtil.getPlayerSpeed(player) > 0.10){
                playerData.addVl(1,"NoWebCheck");
            }
        }
    }
}
