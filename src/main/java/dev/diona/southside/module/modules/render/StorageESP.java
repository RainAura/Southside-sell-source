package dev.diona.southside.module.modules.render;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.Render3DEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.player.Stealer;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashSet;

public class StorageESP extends Module {
    public final Slider opacity = new Slider("Opacity", 80, 0, 255, 1);
    public StorageESP(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public final void onRender3DEvent(final Render3DEvent event) {
        try {
            for (final var ent : mc.world.loadedTileEntityList) {
                final var posX = ent.getPos().getX() - mc.getRenderManager().renderPosX;
                final var posY = ent.getPos().getY() - mc.getRenderManager().renderPosY;
                final var posZ = ent.getPos().getZ() - mc.getRenderManager().renderPosZ;
                var base_bb = ent.getBlockType().getCollisionBoundingBox(
                        ent.getBlockType().getStateFromMeta(ent.getBlockMetadata()),
                        mc.world,
                        ent.getPos());
                if (base_bb == null) return;
                base_bb = base_bb.offset(posX, posY, posZ);
                if (ent instanceof TileEntityChest tile) {
                    var tempbb = base_bb;
                    TileEntityChest adjacent = null;
                    if (tile.adjacentChestXNeg != null)
                        adjacent = tile.adjacentChestXNeg;
                    if (tile.adjacentChestXPos != null)
                        adjacent = tile.adjacentChestXPos;
                    if (tile.adjacentChestZNeg != null)
                        adjacent = tile.adjacentChestZNeg;
                    if (tile.adjacentChestZPos != null)
                        adjacent = tile.adjacentChestZPos;
                    if (adjacent != null)
                        tempbb = tempbb.union(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(adjacent.getPos().getX() - mc.getRenderManager().renderPosX, adjacent.getPos().getY() - mc.getRenderManager().renderPosY, adjacent.getPos().getZ() - mc.getRenderManager().renderPosZ));
                    final int opacity = this.opacity.getValue().intValue();
                    Stealer stealer = (Stealer) Southside.moduleManager.getModuleByClass(Stealer.class);
                    final Color color = (tile.getChestType() == BlockChest.Type.TRAP || stealer.stolen.contains(tile.getPos())) ? new Color(255, 91, 86, opacity) : new Color(255, 227, 0, opacity);
                    RenderUtil.drawAxisAlignedBB(tempbb, color);
                } else if (ent instanceof TileEntityFurnace) {
                    RenderUtil.drawAxisAlignedBB(base_bb, new Color(100, 100, 100, this.opacity.getValue().intValue()));
                } else if (ent instanceof TileEntityBrewingStand) {
                    RenderUtil.drawAxisAlignedBB(base_bb, new Color(255,165,0, this.opacity.getValue().intValue()));
                }
            }
        } catch (NullPointerException ignored) {

        }
    }
}
