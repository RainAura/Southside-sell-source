package dev.diona.southside.module.modules.misc;

import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Notification;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.TimerUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.diona.southside.Southside.MC.mc;

public class ItemDetector extends Module {
    public final Slider keepTimeValue = new Slider("Keep Time", 10, 5, 20, 0.1);
    private final Map<UUID, Boolean> hasSharpAxe = new HashMap<>();
    private final Map<UUID, Boolean> hasBall = new HashMap<>();
    public ItemDetector(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private final TimerUtil timer = new TimerUtil();

    @EventListener
    public void onWorld(WorldEvent event) {
        hasSharpAxe.clear();
        hasBall.clear();
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (!timer.hasReached(1000)) return;
        timer.reset();
        List<AbstractClientPlayer> playerList = mc.world.loadedEntityList
                .stream()
                .filter(entity -> entity instanceof AbstractClientPlayer)
                .filter(Entity::isEntityAlive)
                .map(entity -> (AbstractClientPlayer) entity)
                .toList();
        playerList.forEach(player -> {
            if (player == mc.player) return;

            ItemStack is = player.getHeldItemMainhand();
            if (!hasSharpAxe.containsKey(player.getUniqueID())) {
                hasSharpAxe.put(player.getUniqueID(), false);
            }
            if (!hasBall.containsKey(player.getUniqueID())) {
                hasBall.put(player.getUniqueID(), false);
            }
            if (is.getItem() instanceof ItemAxe) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, is) == 666 && !hasSharpAxe.get(player.getUniqueID())) {
                    hasSharpAxe.put(player.getUniqueID(), true);
                    Notification.addNotificationKeepTime(
                            player.getNameClear() + " has a sharpness 666 axe!",
                            "Item Detector",
                            Notification.NotificationType.WARN,
                            keepTimeValue.getValue().floatValue()
                    );
                }
            }
            if(is.getItem() == Items.SLIME_BALL && !hasBall.get(player.getUniqueID())){
                NBTTagList enchantmentTagList = is.getEnchantmentTagList();

                if (enchantmentTagList != null) {
                    for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
                        NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
                        if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 19) {
                            int level = nbt.getInteger("lvl");

                            if (level >= 2) {
                                hasBall.put(player.getUniqueID(), true);
                                Notification.addNotificationKeepTime(
                                        player.getNameClear() + " has a knock back slime ball!",
                                        "Item Detector",
                                        Notification.NotificationType.WARN,
                                        keepTimeValue.getValue().floatValue()
                                );
                            }
                        }
                    }
                }
            }
        });
    }
}
