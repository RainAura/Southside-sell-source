package dev.diona.southside.module.modules.misc.hackerdetector.check.impl;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.modules.misc.HackerDetector;
import dev.diona.southside.module.modules.misc.hackerdetector.check.NormalCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.check.PacketCheck;
import dev.diona.southside.module.modules.misc.hackerdetector.player.PlayerData;
import dev.diona.southside.util.player.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.util.math.BlockPos;

public class EagleCheck implements PacketCheck, NormalCheck {
    HackerDetector hackerDetector = (HackerDetector) Southside.moduleManager.getModuleByClass(HackerDetector.class);

    @Override
    public void onPacket(PacketEvent event) {
        if (Minecraft.getMinecraft().world == null) return;
        if (Minecraft.getMinecraft().player == null) return;
        if (event.getPacket() instanceof SPacketEntity packet) {
            Entity entity = packet.getEntity(Minecraft.getMinecraft().world);
            if (entity instanceof EntityPlayer player) {
                PlayerData playerData = hackerDetector.getPlayData(player.getUniqueID());
                if (playerData == null) return;
                if (packet.getOnGround()) {
                    final Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(player.posX, player.posY - 1, player.posZ)).getBlock();

                    if (block instanceof BlockAir) {
                        playerData.shouldEagle = true;
                        playerData.eagleTicks++;
                    } else {
                        playerData.shouldEagle = false;
                        playerData.eagleTicks = 0;
                    }
                } else {
                    playerData.shouldEagle = false;
                    playerData.eagleTicks = 0;
                }
            }
        }
        if (event.getPacket() instanceof SPacketEntityMetadata packet) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(packet.getEntityId());

            if (packet.getDataManagerEntries() != null && entity instanceof EntityPlayer player) {
                PlayerData playerData = hackerDetector.getPlayData(player.getUniqueID());
                if (playerData == null) return;
                for (final EntityDataManager.DataEntry<?> dataEntry : packet.getDataManagerEntries()) {
//                    ChatUtil.info(player.getName() + " " + dataEntry.getKey().getId() + " : " + dataEntry.getValue());
                    if (dataEntry.getKey().getId() == 0 && (Byte) dataEntry.getValue() == 2 && playerData.shouldEagle && playerData.eagleTicks <= 2) {
                        playerData.addVl(1,"Eagle Check");
                    }
                }
            }
        }

    }

    @Override
    public void onUpdate(PlayerData playerData) {
//        EntityPlayer player = playerData.getPlayer();
//        if (player.isSneaking() && playerData.shouldEagle && playerData.eagleTicks <= 2) {
//            playerData.addVl(1);
//        }
    }
}
