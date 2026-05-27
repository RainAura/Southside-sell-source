package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.PacketType;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.ColorUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Xray extends Module {

    public final Slider worldOpacity = new Slider("World Opacity", 0.25f, 0.05f, 1.0f, 0.05f);

    public final Switch caveOnly = new Switch("Cave Only", false);

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addListener(worldOpacity.getLabel(), () -> {
            if (shouldLoadRender()) mc.renderGlobal.loadRenderers();
        });
        addListener(caveOnly.getLabel(), () -> {
            if (shouldLoadRender()) mc.renderGlobal.loadRenderers();
        });
        addDependency(range.getLabel(), outline.getLabel());
        addDependency(lineWidth.getLabel(), outline.getLabel());
        addDependency(opacity.getLabel(), outline.getLabel());
        addDependency(radius.getLabel(), packet.getLabel());
        addDependency(delay.getLabel(), packet.getLabel());
    }

    public final Slider range = new Slider("Range", 45, 30, 80, 1);
    public final Switch outline = new Switch("Outline", true);
    public final Slider lineWidth = new Slider("Line Width", 1, 0.2f, 5, 0.1f);

    public final Slider opacity = new Slider("Outline Opacity", 0.25f, 0.05f, 1, 0.05f);
    public final Switch packet = new Switch("Packet", false);

    public final Slider radius = new Slider("Radius", 5, 3, 6, 1);

    public final Slider delay = new Slider("Delay", 3, 3, 5, 0.5f);
    private final List<BlockPos> showing = new CopyOnWriteArrayList<>();

    private final Map<BlockPos, Block> map = new ConcurrentHashMap<>();

    private final TimerUtil packetWatch = new TimerUtil();

    public Xray(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }


    @Override
    public boolean onEnable() {
        super.onEnable();

        showing.clear();
        map.clear();

        mc.renderGlobal.markBlockRangeForRenderUpdate((int) mc.player.posX - 900, (int) mc.player.posY - 900, (int) mc.player.posZ - 900, (int) mc.player.posX + 900, (int) mc.player.posY + 900, (int) mc.player.posZ + 900);
        mc.renderGlobal.loadRenderers();

        if (packet.getValue() && packetWatch.hasReached(Math.round(delay.getValue().doubleValue() * 1000)) && mc.player.posY <= 25)
            sendPacketScanning();
        return true;
    }

    @Override
    public boolean onDisable() {
        super.onDisable();

        showing.clear();
        map.clear();

        mc.renderGlobal.loadRenderers();
        return true;
    }

    @EventListener
    private void onTick(TickEvent event) {
        if (packet.getValue() && packetWatch.hasReached(Math.round(delay.getValue().doubleValue() * 1000)) && mc.player.posY <= 25)
            sendPacketScanning();
    }

    @EventListener
    private void onPacket(PacketEvent event) {

        if (!packet.getValue()) return;

        if (event.getType() == PacketType.RECEIVE) {
            Packet<?> packet = event.getPacket();

            if (packet instanceof SPacketBlockChange blockChange) {

                var position = blockChange.getBlockPosition();
                var block = blockChange.getBlockState().getBlock();

                if ((block instanceof BlockOre || block instanceof BlockRedstoneOre) && !showing.contains(position) && !map.containsKey(position)) {
                    map.put(position, block);
                }
            } else if (packet instanceof SPacketMultiBlockChange blockChange) {

                for (SPacketMultiBlockChange.BlockUpdateData changedBlock : blockChange.getChangedBlocks()) {

                    var position = changedBlock.getPos();
                    var block = changedBlock.getBlockState().getBlock();

                    if ((block instanceof BlockOre || block instanceof BlockRedstoneOre) && !showing.contains(position) && !map.containsKey(position)) {
                        map.put(position, block);
                    }
                }
            }
        }
    }

    @EventListener
    private void onRender3D(Render3DEvent event) {

        if (!outline.getValue() && !packet.getValue()) return;

        for (Map.Entry<BlockPos, Block> entry : map.entrySet()) {

            var position = entry.getKey();

            if (getDistanceXZ(position) > range.getValue().floatValue()) continue;

            var block = entry.getValue();

            var color = new Color(15, 15, 15);

            var string = block.getTranslationKey();

            switch (string) {
                case "tile.oreRedstone":
                case "tile.blockRedstone": {
                    color = new Color(0xFF0000);
                    break;
                }
                case "tile.oreEmerald":
                case "tile.blockEmerald": {
                    color = new Color(0, 255, 0);
                    break;
                }
                case "tile.blockLapis":
                case "tile.oreLapis": {
                    color = new Color(255);
                    break;
                }
                case "tile.blockDiamond":
                case "tile.oreDiamond": {
                    color = new Color(0, 255, 255);
                    break;
                }
                case "tile.netherquartz": {
                    color = new Color(255, 255, 255);
                    break;
                }
                case "tile.blockGold":
                case "tile.oreGold": {
                    color = new Color(0xFFFF00);
                    break;
                }
                case "tile.blockIron":
                case "tile.oreIron": {
                    color = new Color(155, 130, 150);
                    break;
                }
            }

            if (outline.getValue())
                RenderUtil.drawOutlinedBoundingBox(position, lineWidth.getValue().floatValue(), ColorUtil.reAlpha(color, opacity.getValue().floatValue()));
        }
    }

    private void sendPacketScanning() {

        var scanning = radius.getValue().intValue();

        for (int x = -scanning; x < scanning; ++x) {
            for (int y = scanning; y > -scanning; --y) {
                for (int z = -scanning; z < scanning; ++z) {

                    var position = new BlockPos(mc.player).add(x, y, z);
                    var state = mc.world.getBlockState(position);
                    var block = state.getBlock();

                    if (showing.contains(position) || map.containsKey(position) || (block != Blocks.GOLD_ORE && block != Blocks.DIAMOND_ORE) || (state.getBlockHardness(mc.world, BlockPos.ORIGIN) == -1 && !mc.playerController.isInCreativeMode()))
                        continue;
                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, position, EnumFacing.UP));
                    showing.add(position);
                }
            }
        }

        packetWatch.reset();
    }

    private boolean shouldLoadRender() {
        return isEnabled();
    }

    public double getDistanceXZ(double x, double z) {
        return Math.hypot(mc.player.posX - x, mc.player.posZ - z);
    }

    public double getDistanceXZ(BlockPos position) {
        return Math.hypot(mc.player.posX - position.getX(), mc.player.posZ - position.getZ());
    }


    public Slider getWorldOpacity() {
        return worldOpacity;
    }

    public Switch getCaveOnly() {
        return caveOnly;
    }

    public Slider getRange() {
        return range;
    }

    public List<BlockPos> getShowing() {
        return showing;
    }

    public Map<BlockPos, Block> getMap() {
        return map;
    }

    public Switch getOutline() {
        return outline;
    }


}
