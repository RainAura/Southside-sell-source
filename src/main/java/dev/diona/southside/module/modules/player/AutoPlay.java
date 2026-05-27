package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Notification;
import dev.diona.southside.module.modules.misc.ItemDetector;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.InventoryUtil;
import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.EnumHand;

import static dev.diona.southside.Southside.MC.mc;

public class AutoPlay extends Module {
    public final Dropdown modeValue = new Dropdown("Mode", "HytSWSolo", "HytSWSolo");
    public final Slider delayValue = new Slider("Delay", 3, 1, 10, 0.1);
    private static final String AUTOPLAY_TEXT = "AUTOPLAY_TEXT";
    public AutoPlay(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private TimerUtil timer = new TimerUtil();
    private boolean waiting = false;
    public Stage stage = Stage.IDLE;

    @EventListener
    public void onText(TextEvent event) {
        if (event.getText().equals(AUTOPLAY_TEXT)) {
            if (timer.hasReached((long) (delayValue.getValue().doubleValue() * 1000))) {
                event.setText("Sending you to the next game!" );
            } else {
                event.setText("Sending you to the next game in " + (long) (delayValue.getValue().doubleValue() * 10 - timer.passed() / 100f) / 10f + "s");
            }
        }
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        if (stage == Stage.WAITING) {
            stage = Stage.IDLE;
        } else if (stage == Stage.HUB) {
            stage = Stage.CLICKING;
        } else if (stage == Stage.FINISH) {
            stage = Stage.IDLE;
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
//        if (mc.gameSettings.keyBindJump.isPressed()) {
//        }
//        if (timer.hasReached(500) && stage == Stage.CLICKED) {
//            stage = Stage.IDLE;
//            ChatUtil.info("debug2");
//            int id = 0;
//            String sid = "SKYWAR/nskywar";
//
//            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
//                    .writeString("mainmenu")
//                    .writeString("自适应背景$细分分类$游戏0")
//                    .writeInt(0))
//            ));
//
//            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
//                    new PacketBuffer(Unpooled.buffer()
//                            .writeInt(26))
//                            .writeString("GUI$mainmenu@entry/" + id)
//                            .writeString("{\"entry\":" + id + ",\"sid\":\"" + sid + "\"}")
//            ));
//
//            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
//                    .writeString("mainmenu")
//            ));
//        }
        switch (modeValue.getMode()) {
            case "HytSWSolo" -> {
                if (stage == Stage.IDLE) {
                    ItemStack itemStack = mc.player.inventoryContainer.getSlot(44).getStack();
                    if (itemStack.getItem().equals(Items.IRON_DOOR) && !mc.playerController.gameIsSurvivalOrAdventure()) {

                        Notification.addNotificationKeepTime(AUTOPLAY_TEXT, "Auto Play", Notification.NotificationType.INFO, delayValue.getValue().doubleValue());

                        stage = Stage.WAITING;
                        timer.reset();
                    }
                } else if (timer.hasReached(delayValue.getValue().doubleValue() * 1000) && stage == Stage.WAITING) {
                    stage = Stage.HUB;
                    mc.getConnection().sendPacket(new CPacketChatMessage("/hub"));
                } else if (stage == Stage.CLICKING) {
                    mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

                    mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                            .writeString("mainmenu")
                            .writeString("自适应背景$主分类$subject_skywar")
                            .writeInt(0))
                    ));

                    mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                            new PacketBuffer(Unpooled.buffer()
                                    .writeInt(26))
                                    .writeString("GUI$mainmenu@subject/skywar")
                                    .writeString("{\"click\":\"1\"}")));

                    mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                            .writeString("mainmenu")
                    ));

                    stage = Stage.CLICKING_SUB;
                    timer.reset();
                }
            }
        }
    }

    public void receiveMain() {
//        ChatUtil.info("click");
    }

    public void receiveSub() {
//        ChatUtil.info("clicking sub");
        int id = 0;
        String sid = "SKYWAR/nskywar";

        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                .writeString("mainmenu")
                .writeString("自适应背景$细分分类$游戏" + id)
                .writeInt(0))
        ));

        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                new PacketBuffer(Unpooled.buffer()
                        .writeInt(26))
                        .writeString("GUI$mainmenu@entry/" + id)
                        .writeString("{\"entry\":" + id + ",\"sid\":\"" + sid + "\"}")
        ));

        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                .writeString("mainmenu")
        ));

        stage = Stage.FINISH;
    }

    @Override
    public String getSuffix() {
        return this.modeValue.getMode();
    }

    public enum Stage {
        IDLE,
        WAITING,
        HUB,
        CLICKING,
        CLICKING_SUB,
        FINISH
    }
}
