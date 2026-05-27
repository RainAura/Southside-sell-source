package dev.diona.southside.module.modules.misc.hackerdetector.check;

import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;

public interface NormalCheck extends Check{
    void onUpdate(PlayerData playerData);
}
