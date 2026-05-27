package dev.diona.southside.util.player;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;

import static dev.diona.southside.Southside.MC.mc;

public class TabUtil {
    public static boolean inTab(EntityLivingBase entity) {
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap())
            if (info != null && info.getGameProfile() != null && info.getGameProfile().getName().contains(entity.getName()))
                return true;

        return false;
    }

    public static boolean inTab(String name) {
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap())
            if (info != null && info.getGameProfile() != null && info.getGameProfile().getName().contains(name))
                return true;

        return false;
    }
}
