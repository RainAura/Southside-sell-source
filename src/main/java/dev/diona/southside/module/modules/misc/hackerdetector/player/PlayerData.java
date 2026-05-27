package dev.diona.southside.module.modules.misc.hackerdetector.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.diona.southside.module.modules.misc.HackerDetector;
import dev.diona.southside.util.player.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.concurrent.TimeUnit;

public class PlayerData {
    public boolean shouldEagle;
    public int eagleTicks;

    public float lastTickYaw;
    private EntityPlayer player;
    private float vl;

    private boolean firstDectected = true;

    // CPS cache
    private Cache<Long, Long> cpsCache = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    public PlayerData(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    public float getVl() {
        return vl;
    }

    public void setVl(float vl) {
        this.vl = vl;
    }

    public Cache<Long, Long> getCpsCache() {
        return cpsCache;
    }

    public boolean isFirstDectected() {
        return firstDectected;
    }

    public void setFirstDectected(boolean firstDectected) {
        this.firstDectected = firstDectected;
    }

    public void addVl(float vl) {
        this.vl += vl;
    }

    public void addVl(float vl, String reason) {
        this.vl += vl;
        if (HackerDetector.getINSTANCE().logValue.getValue()) {
            ChatUtil.info(player.getName() + " flagged " + reason);
        }
    }
}
