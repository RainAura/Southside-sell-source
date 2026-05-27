package dev.diona.southside.module.modules.misc;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.events.NewRender2DEvent;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSetSlot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class AntiTigerMachine extends Module {
    private static AntiTigerMachine INSTANCE;
    public Slider rollingTime = new Slider("Allow rolling time", 500, 0, 1000, 1);
    public Switch disallowOpenOtherTiger = new Switch("Disallow open other Tiger", true);
    public Switch drawTiger = new Switch("This draws tigers on your screen", false);
    private final HashMap<Integer, TigerSlot> tigerSlots = new HashMap<>();
    private boolean tigerAngry = false; // TODO:检测老虎是否生气，生气就停止InvManager的工作

    public AntiTigerMachine(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @EventListener
    public void onUpdate(PacketEvent event) {

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock onBlock && disallowOpenOtherTiger.getValue() && isTigerMachineWorking()) {
            Block block = mc.world.getBlockState(onBlock.getPos()).getBlock();
            if (block != Blocks.AIR &&
                    !mc.player.isSneaking() &&
                    (block == Blocks.CHEST ||
                            block == Blocks.FURNACE ||
                            block == Blocks.BREWING_STAND)) {
                event.cancel();
                ChatUtil.info("老虎机工作中，再开老虎就生气了");
            }
        }

        if (event.getPacket() instanceof SPacketSetSlot setSlot && setSlot.getWindowId() == 0) {
            TigerSlot slot;
            if ((slot = tigerSlots.get(setSlot.getSlot())) != null) {
                if (slot.canRemoveTime < System.currentTimeMillis()) {
                    slot.canRemoveTime = System.currentTimeMillis() + rollingTime.getValue().intValue();
                }
            }
        }

        if (event.getPacket() instanceof CPacketClickWindow clickWindow && clickWindow.getWindowId() == 0) {
            if (!(clickWindow.getSlotId() == -999)) {
                tigerSlots.put(clickWindow.getSlotId(), new TigerSlot(clickWindow.getSlotId()));
            }
        }


    }

    @EventListener
    public void onRender2D(NewRender2DEvent event) {
        if (!drawTiger.getValue()) return;
        if (!tigerSlots.isEmpty()) {
            int y = 30;
            int x = 100;
            mc.fontRenderer.drawStringWithShadow("§oTiger Slots", x, y += 15, -1);

            mc.fontRenderer.drawStringWithShadow("§nSlot", x, y += 15, -1);
            mc.fontRenderer.drawStringWithShadow("§nTime", x + 30, y, -1);


            for (Map.Entry<Integer, TigerSlot> entry : tigerSlots.entrySet()) {
                mc.fontRenderer.drawStringWithShadow(String.valueOf(entry.getKey()), x, y += 15, -1);
                mc.fontRenderer.drawStringWithShadow(String.valueOf((entry.getValue().canRemoveTime - System.currentTimeMillis())), x + 30, y, -1);

            }
        }
    }

    @EventListener
    public void onWorldLoad(WorldEvent event) {
        tigerSlots.clear();
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.player != null && !tigerSlots.isEmpty()) {
            Iterator<Map.Entry<Integer, TigerSlot>> iterator = tigerSlots.entrySet().iterator();
            long currentTime = System.currentTimeMillis();
            while (iterator.hasNext()) {
                Map.Entry<Integer, TigerSlot> entry = iterator.next();
                if (entry.getValue().canRemoveTime < currentTime) {
                    iterator.remove();
                }
            }
        }
    }

    public static boolean isTigerMachineWorking() {
        return !INSTANCE.tigerSlots.isEmpty();
    }

    class TigerSlot {
        public int slotID;
        public long firstClickTime;
        public long canRemoveTime;

        public TigerSlot(int slotID, int firstClickTime, int canRemoveTime) {
            this.slotID = slotID;
            this.firstClickTime = firstClickTime;
            this.canRemoveTime = canRemoveTime;
        }

        public TigerSlot(int slotID) {
            this.slotID = slotID;
            this.firstClickTime = System.currentTimeMillis();
            this.canRemoveTime = this.firstClickTime + rollingTime.getValue().intValue();
        }
    }
}
