package dev.diona.southside.module.modules.combat;

import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.AttackEvent;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.movement.Sprint;
import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import static dev.diona.southside.Southside.MC.mc;

public class SuperKnockBack extends Module {
    private int state = 0;
    private static boolean canSend = false;
    private static boolean canSprint = false;

    public SuperKnockBack(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public static boolean canSprint() {
        return SuperKnockBack.canSprint || !Southside.moduleManager.getModuleByClass(SuperKnockBack.class).isEnabled();
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (canSend) {
            if (state <= 2 && mc.player.isSprinting() == mc.player.serverSprintState) {
                if (mc.player.serverSprintState) {
                    canSprint = false;
                } else {
                    canSprint = true;
                }
            } else {
                canSprint = true;
            }
            state++;

        }


    }

    @EventListener
    public void onAttack(AttackEvent event) {
        state = 0;
    }

    @EventListener
    public void onPacketSend(PacketEvent e) {
        if (e.getPacket() instanceof CPacketPlayer) {
            canSend = true;
        }

        if (e.getPacket() instanceof CPacketEntityAction action && (action.getAction() == CPacketEntityAction.Action.START_SPRINTING || action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
//            ChatUtil.info(action.getAction().toString());
            canSend = false;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook packet) {
            canSend = false;
        }

    }
}
