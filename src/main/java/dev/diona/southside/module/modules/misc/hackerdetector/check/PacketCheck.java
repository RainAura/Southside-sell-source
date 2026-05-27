package dev.diona.southside.module.modules.misc.hackerdetector.check;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.modules.misc.HackerDetector;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;

public interface PacketCheck extends Check {
    void onPacket(PacketEvent event);
}
