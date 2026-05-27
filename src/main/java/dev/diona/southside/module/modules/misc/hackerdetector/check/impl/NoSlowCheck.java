package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.module.modules.misc.hackerdetector.check.Check;
import dev.diona.southside.module.modules.misc.hackerdetector.check.NormalCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.MovementUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;

public class NoSlowCheck implements NormalCheck {
    @Override
    public void onUpdate(PlayerData playerData) {
        EntityPlayer player = playerData.getPlayer();
        if (player.isPotionActive(Potion.getPotionById(1)) || player.isPotionActive(Potion.getPotionById(8)))
            return;
        if (player.isHandActive() && player.onGround && player.hurtTime == 0 && player.isSprinting() && (player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemFood)  && MovementUtil.getPlayerSpeed(player) >= 0.05F) {
            playerData.addVl(1,"NoSlowCheck");
        }
    }
}
