package dev.diona.southside.util.player;

import dev.diona.southside.util.misc.FakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Objects;

import static dev.diona.southside.Southside.MC.mc;

public class FakePlayerUtil {
    public static FakePlayer spawnFakePlayer() {
        if (mc.world == null) return null;
        return new FakePlayer(mc.player);
    }
}
