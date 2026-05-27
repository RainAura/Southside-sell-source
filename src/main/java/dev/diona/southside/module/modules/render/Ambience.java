package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.MathUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

public class Ambience extends Module {
    public final Dropdown weatherValue = new Dropdown("Weather", "Default", "Default", "Clear", "Rain", "Snow");
    public final Slider strengthValue = new Slider("Strength", 1, 0.3, 2, 0.1);
    public final Switch overrideTimeValue = new Switch("Override Time", false);
    public final Slider timeValue = new Slider("Time", 12500, 0, 24000, 1);
    public final Switch overrideThunderValue = new Switch("Thunder", false);
    public final Slider thunderStrengthValue = new Slider("Thunder Strength", 0.1, 0, 10, 0.1);

    public Ambience(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(strengthValue.getLabel(), this::overrideRain);
        addDependency(thunderStrengthValue.getLabel(), overrideThunderValue::getValue);
    }

    private boolean overrideRain() {
        return weatherValue.getMode().equals("Rain") || weatherValue.getMode().equals("Snow");
    }

    @EventListener
    public void onLivingUpdate(UpdateEvent event) {
        switch (weatherValue.getMode()) {
            case "Clear" -> {
                mc.world.setRainStrength(0);
            }
            case "Rain", "Snow" -> {
                mc.world.setRainStrength(strengthValue.getValue().floatValue());
            }
        }
        if (overrideTimeValue.getValue()) {
            mc.world.setWorldTime(timeValue.getValue().longValue());
        }
        if (overrideThunderValue.getValue()) {
            for (int i = -10; i <= 10; i++) {
                for (int j = -10; j <= 10; j++) {
                    if (MathUtil.getRandomFloat(0, 1) < 0.001 * thunderStrengthValue.getValue().floatValue()) {

                        Chunk chunk = mc.world.getChunk(mc.player.chunkCoordX + i, mc.player.chunkCoordZ + j);
                        if (chunk instanceof EmptyChunk) continue;
                        int x = chunk.x * 16 + MathUtil.getRandomInRange(0, 15);
                        int z = chunk.z * 16 + MathUtil.getRandomInRange(0, 15);
                        BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(x, 0, z));
                        mc.world.addWeatherEffect(new EntityLightningBolt(mc.world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), false));
                        if (pos.getY() != -1) mc.world.playSound(mc.player, pos, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + MathUtil.getRandomFloat(0, 0.2F));
                    }
                }
            }
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketTimeUpdate && overrideTimeValue.getValue()) {
            event.setCancelled(true);
            return;
        }
        if (event.getPacket() instanceof SPacketChangeGameState state) {
            if ((state.getGameState() == 1 || state.getGameState() == 2) && this.overrideRain()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
