package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import static dev.diona.southside.Southside.MC.mc;

public class SpeedMine extends Module {
    public Dropdown modeValue = new Dropdown("Mode", "Simple", "Simple", "Grim");
    public Slider speedValue = new Slider("Speed", 1, 1, 5, 0.1);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost = false;
    private float damage = 0.0F;
    public SpeedMine(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayerDigging packet) {
            switch (packet.getAction()) {
                case START_DESTROY_BLOCK -> {
                    this.boost = true;
                    this.pos = packet.getPosition();
                    this.facing = packet.getFacing();
                    this.damage = 0.0F;
                }
                case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> {
                    this.boost = false;
                    this.pos = null;
                    this.facing = null;
                }
            }
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
//        if (Blink.isInstanceEnabled()) return;
        if (mc.playerController.extendedReach()) {
            mc.playerController.blockHitDelay = 0;
        } else if (this.pos != null && this.boost) {
            IBlockState blockState = mc.world.getBlockState(this.pos);
            this.damage = (float)(this.damage + blockState.getBlock().getPlayerRelativeBlockHardness(blockState, mc.player, mc.world, this.pos) * (this.speedValue.getValue().doubleValue()));
            if (this.damage >= 1.0F) {
                mc.world.setBlockState(this.pos, Blocks.AIR.getDefaultState(), 11);
                if (this.modeValue.getMode().equals("Grim")) {
                    mc.getConnection().sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
                }
                mc.getConnection().sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, this.facing));
                this.damage = 0.0F;
                this.boost = false;
            }
        }
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }
}
