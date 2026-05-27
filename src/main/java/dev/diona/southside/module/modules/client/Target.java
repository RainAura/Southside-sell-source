package dev.diona.southside.module.modules.client;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.misc.AntiBot;
import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.chat.Member;
import dev.diona.southside.util.misc.FakePlayer;
import dev.diona.southside.util.player.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static dev.diona.southside.Southside.MC.mc;

public class Target extends Module {
    private static Target INSTANCE;
    public final Switch playerValue = new Switch("Player", true);
    public final Switch teamValue = new Switch("Team", false);
    public final Dropdown teamModeValue = new Dropdown("Team Mode", "Armor", "Armor", "Scoreboard");
    public final Dropdown armorValue = new Dropdown("Armor Slot", "Helmet", "Helmet", "Chestplate", "Leggings", "Boots");
    public final Switch mobValue = new Switch("Mob", false);
    public final Switch animalValue = new Switch("Animal", false);
    public final Switch villagerValue = new Switch("Villager", false);
    public final Switch deadValue = new Switch("Dead", false);

    public static final Set<UUID> whiteList = new HashSet<>();

    public Target(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();

        addDependency(teamValue.getLabel(), playerValue.getLabel());
        addDependency(teamModeValue.getLabel(), () -> playerValue.getValue() && teamValue.getValue());
        addDependency(armorValue.getLabel(), () -> playerValue.getValue() && teamValue.getValue() && teamModeValue.getMode().equals("Armor"));
    }

    public static boolean isTarget(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        boolean result = isTargetIgnoreTeam(entity);
        if (result && entity instanceof EntityPlayer player && (teamCheck(player) || inWhiteList(player))) {
            return false;
        }
        return result;
    }

    private static boolean inWhiteList(EntityPlayer player) {
        return whiteList.contains(player.getUniqueID());
    }

    public static boolean isTargetIgnoreTeam(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        if (entity == mc.player) return false;
        if (entity instanceof FakePlayer) return false;
        boolean deadCheck = (entity.isEntityAlive() && ((EntityLivingBase) entity).getHealth() > 0) || INSTANCE.deadValue.getValue();
        if (!deadCheck) return false;
        if (entity instanceof EntityPlayer player) {
            if (player.getUniqueID().equals(mc.player.getUniqueID())) return false;
            if (AntiBot.isBot(player)) return false;
            return INSTANCE.playerValue.getValue();
        }
        if (entity instanceof EntityMob) {
            return INSTANCE.mobValue.getValue();
        }
        if (entity instanceof EntityAnimal) {
            return INSTANCE.animalValue.getValue();
        }
        if (entity instanceof EntityVillager) {
            return INSTANCE.villagerValue.getValue();
        }
        return false;
    }

    private static boolean teamCheck(EntityPlayer target) {
        IRCFriend ircFriend = (IRCFriend) Southside.moduleManager.getModuleByClass(IRCFriend.class);
        if (!ircFriend.canAttack()) {
            for (Member member : Chat.getInstance().getOnlineMembers().values()) {
                if (target.getUniqueID().toString().equals(member.getUuid()) && member.isFriend()) {
                    return true;
                }
            }
        }
        if (!INSTANCE.teamValue.getValue()) return false;
        if (target == null) return false;
        EntityPlayer player = mc.player;
        switch (INSTANCE.teamModeValue.getMode()) {
            case "Armor" -> {
                int slot = 0;
                switch (INSTANCE.armorValue.getMode()) {
                    case "Helmet" -> {
                        slot = 3;
                    }
                    case "Chestplate" -> {
                        slot = 2;
                    }
                    case "Leggings" -> {
                        slot = 1;
                    }
                    case "Boots" -> {
                        slot = 0;
                    }
                }
                ItemStack playerArmorStack = player.inventory.armorItemInSlot(slot);
                ItemStack targetArmorStack = target.inventory.armorItemInSlot(slot);
                if (playerArmorStack != null && targetArmorStack != null) {
                    if (playerArmorStack.getItem() instanceof ItemArmor playerArmor && targetArmorStack.getItem() instanceof ItemArmor targetArmor) {
                        return playerArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && targetArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && playerArmor.getColor(playerArmorStack) == targetArmor.getColor(targetArmorStack);
                    }
                } else {
                    return false;
                }
            }
            case "Scoreboard" -> {
                return player.isOnSameTeam(target);
            }
        }
        return false;
    }

    @Override
    public boolean onEnable() {
        this.setEnable(false);
        return true;
    }
}
