//package dev.diona.southside.module.modules.render;
//
//import cc.polyfrost.oneconfig.config.options.impl.Switch;
//import dev.diona.southside.Southside;
//import dev.diona.southside.event.EventState;
//import dev.diona.southside.event.events.*;
//import dev.diona.southside.gui.UIElementModule;
//import dev.diona.southside.module.Category;
//import dev.diona.southside.module.values.BooleanValue;
//import dev.diona.southside.util.misc.BezierUtil;
//import dev.diona.southside.util.misc.MathUtil;
//import dev.diona.southside.util.misc.TimerUtil;
//import dev.diona.southside.util.player.ChatUtil;
//import dev.diona.southside.util.render.RenderUtil;
//import me.bush.eventbus.annotation.EventListener;
//import me.bush.eventbus.annotation.ListenerPriority;
//import net.minecraft.item.ItemSword;
//import net.minecraft.network.play.client.*;
//
//import java.awt.*;
//import java.util.LinkedList;
//import java.util.List;
//
//public class BlockRateDisplay extends UIElementModule {
//    private final List<Long> blockRates = new LinkedList<>();
//    private long sum = 0;
//    private boolean blocking = false;
//    public final Switch C09 = new Switch("C09 Unblock", true);
//    public final Switch C0B = new Switch("C0B Unblock", true);
//    public final Switch C0E = new Switch("C0E Unblock", true);
//
//    public BlockRateDisplay(String name, String description, Category category, boolean visible) {
//        super(name, description, category, visible, "Block Rate Display", 50, 180);
//    }
//
//    private final TimerUtil blockTimer = new TimerUtil();
//
//    @EventListener
//    public void onTick(TickEvent event) {
//        if (!OldHitting.isBlocking() && !mc.player.isHandActive()) {
//            blockRates.clear();
//            sum = 0;
//        }
//    }
//
//    @EventListener(priority = ListenerPriority.HIGHEST)
//    public void onHigherPacket(HigherPacketEvent event) {
////        if (!OldHitting.isBlocking()) return;
//        if (event.getPacket() instanceof CPacketPlayerDigging digging && digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
//            this.unblock();
//        } else if (event.getPacket() instanceof CPacketHeldItemChange && C09.getValue()) {
//            this.unblock();
//        } else if (event.getPacket() instanceof CPacketUseEntity && C0B.getValue()) {
//            this.unblock();
//        } else if (event.getPacket() instanceof CPacketClickWindow && C0E.getValue()) {
//            this.unblock();
//        } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
//            blockTimer.reset();
//            blocking = true;
//        }
//    }
//
//    @EventListener(priority = ListenerPriority.LOWEST)
//    public void onMotion(MotionEvent event) {
//        if (event.getState() != EventState.PRE) return;
//        if (blocking) {
//            long current = blockTimer.getCurrentMS();
//            long blockTime = current - blockTimer.lastMS;
//            blockRates.add(blockTime);
//            sum += blockTime;
//            if (blockRates.size() > 20) {
//                sum -= blockRates.remove(0);
//            }
//
//            blockTimer.lastMS = current;
//        }
//    }
//
//    private void unblock() {
//        if (!blocking) return;
//        blocking = false;
//        long blockTime = blockTimer.passed();
//        blockRates.add(blockTime);
//        sum += blockTime;
//        if (blockRates.size() > 20) {
//            sum -= blockRates.remove(0);
//        }
//    }
//
//    @Override
//    public float width() {
//        return 120;
//    }
//
//    private BezierUtil yAnimation = new BezierUtil(4, 0);
//
//    @EventListener
//    public void onBloom2D(Bloom2DEvent event) {
//        super.onUIElementBloom(event);
//        float width = 120, height = 20;
//        RenderUtil.scissorStart(this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue(), width, height);
//        float y = yAnimation.get();
//        RenderUtil.drawRect(this.xValue.getValue().floatValue(), this.yValue.getValue().floatValue() + y, this.xValue.getValue().floatValue() + width, this.yValue.getValue().floatValue() + height + y, new Color(0, 0, 0, 129).getRGB());
//        String message = "Block Rate: ";
//        if (OldHitting.isBlocking() || mc.player.isHandActive()) {
//            double percent;
//            if (blockRates.isEmpty()) {
//                percent = 0;
//            } else {
//               percent = MathUtil.round(100.0 * sum / (blockRates.size() * 50), 1);
//            }
//            if (percent > 100) {
//                percent = 100;
//            }
//            message += percent + "%";
//            yAnimation.update(0);
//        } else {
//            message += "Unblock";
//            yAnimation.update(-30);
//        }
//        Southside.fontManager.font.drawString(12, message, this.xValue.getValue().floatValue() + 5, this.yValue.getValue().floatValue() + 5 + y, Color.WHITE);
//        RenderUtil.scissorEnd();
//    }
//}
