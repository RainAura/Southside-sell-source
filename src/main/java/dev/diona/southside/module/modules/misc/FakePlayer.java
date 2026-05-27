package dev.diona.southside.module.modules.misc;

import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.FakePlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.Objects;

public class FakePlayer extends Module {
    public static EntityOtherPlayerMP fakePlayer;
    public FakePlayer(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public boolean onEnable() {
        this.fakePlayer = FakePlayerUtil.spawnFakePlayer();
        return true;
    }

    @Override
    public boolean onDisable() {
        try {
            if (fakePlayer != null)
                mc.world.removeEntity(fakePlayer);
        } catch (Exception ignored) {
        }
        return true;
    }
}
