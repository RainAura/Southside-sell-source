package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.NewRender2DEvent;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.managers.FileManager;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Notification;
import dev.diona.southside.module.modules.misc.hackerdetector.check.Check;
import dev.diona.southside.module.modules.misc.hackerdetector.check.NormalCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.check.PacketCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.check.impl.ChatBypass;
import dev.diona.southside.module.modules.misc.hackerdetector.check.impl.*;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import dev.diona.southside.util.player.TabUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HackerDetector extends Module {

    public HackerDetector(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    private static HackerDetector INSTANCE;

    public final Switch debugValue = new Switch("Debug", false);
    public final Switch logValue = new Switch("log", false);
    public final Switch persistValue = new Switch("Persist", false);
    public final Slider flagVlValue = new Slider("FlagVL", 10.0, 1.0, 100.0, 0.5);

    private Collection<Check> manageChecks = new HashSet<>();

    private final ConcurrentHashMap<UUID, PlayerData> playDataMap = new ConcurrentHashMap<>();

    private Map<UUID, Float> tempHackerList;

    public PlayerData getPlayData(UUID uuid) {
        return playDataMap.get(uuid);
    }

    @Override
    public boolean onEnable() {
        manageChecks.add(new NoSlowCheck());
        manageChecks.add(new ChatBypass());
        manageChecks.add(new AutoClickerCheck());
        manageChecks.add(new EagleCheck());
        manageChecks.add(new NoWebCheck());
        manageChecks.add(new VelocityCheck());
        manageChecks.add(new TellyBridgeCheck());
        if (persistValue.getValue()) {
            tempHackerList = loadCache();
        }
        return super.onEnable();
    }

    @Override
    public boolean onDisable() {
        if (persistValue.getValue()) {
            save(playDataMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().getVl()
                    )));
        }
        manageChecks.clear();
        playDataMap.clear();
        return super.onDisable();
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        if (mc.world == null) return;
        if (persistValue.getValue()) {
            save(playDataMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().getVl()
                    )));
        }
        for (PlayerData playerData : playDataMap.values()) {
            if (playerData.getVl() > flagVlValue.getValue().floatValue() && TabUtil.inTab(playerData.getPlayer())) {
                EntityPlayer player = playerData.getPlayer();
                Notification.addNotificationKeepTime("发现挂壁：" + player.getName(), "HackerDetector", Notification.NotificationType.WARN, 5);
            }
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.ticksExisted <= 10) return;
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayerSP) continue;
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                PlayerData playerData;
                if (playDataMap.containsKey(player.getUniqueID())) {
                    playerData = playDataMap.get(player.getUniqueID());
                } else {
                    playerData = new PlayerData(player);
                    if (persistValue.getValue() && tempHackerList != null && tempHackerList.containsKey(player.getUniqueID())) {
                        playerData.setVl(tempHackerList.get(player.getUniqueID()));
                    }
                    playDataMap.put(player.getUniqueID(), playerData);
                }
                for (Check check : manageChecks) {
                    if (check instanceof NormalCheck) {
                        ((NormalCheck) check).onUpdate(playerData);
                    }
                }
                if (playDataMap.get(player.getUniqueID()).getVl() > flagVlValue.getValue().floatValue() && playerData.isFirstDectected()) {
                    Notification.addNotificationKeepTime("发现挂壁：" + player.getName(), "HackerDetector", Notification.NotificationType.WARN, 5);
                    playerData.setFirstDectected(false);
                }
            }
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        for (Object check : manageChecks) {
            if (check instanceof PacketCheck packetCheck) {
                packetCheck.onPacket(event);
            }
        }
    }

    @EventListener
    public void onRender2D(NewRender2DEvent event) {
        if (playDataMap.isEmpty() || !debugValue.getValue()) return;
        int i = 0;
        for (UUID uuid : playDataMap.keySet()) {
            if (!TabUtil.inTab(playDataMap.get(uuid).getPlayer()) || playDataMap.get(uuid).getPlayer() == mc.player) {
                continue;
            }
            String msg = String.format("Player: %s , flag: %s", playDataMap.get(uuid).getPlayer().getName(), playDataMap.get(uuid).getVl());
            // 获取屏幕分辨率对象
            ScaledResolution scaledResolution = event.getScaledResolution();
            // 获取屏幕的宽度和高度
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            int x = MathHelper.floor((screenWidth - mc.fontRenderer.getStringWidth(msg)) / 2.0) - 100;
            int y = screenHeight / 2 - ++i * 10;
            mc.fontRenderer.drawStringWithShadow(msg, x, y, -1);
        }
    }

    public boolean isHacker(EntityPlayer player) {
        PlayerData playerData = playDataMap.get(player.getUniqueID());
        return playerData != null && playerData.getVl() > 5;
    }


    public static void save(Map<UUID, Float> flagMap) {
        File configFile = new File(FileManager.CLIENT_DIRECTORY, "hackers.dat");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(configFile))) {
            outputStream.writeObject(flagMap);
            System.out.println("Data serialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<UUID, Float> loadCache() {
        File configFile = new File(FileManager.CLIENT_DIRECTORY, "hackers.dat");
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(configFile))) {
            Map<UUID, Float> flagMap = (Map<UUID, Float>) inputStream.readObject();
            return flagMap;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HackerDetector getINSTANCE() {
        return INSTANCE;
    }
}
