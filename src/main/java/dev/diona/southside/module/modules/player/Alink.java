package dev.diona.southside.module.modules.player;

import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Notification;
import me.bush.eventbus.annotation.EventListener;

public class Alink extends Module {
    private static Alink INSTANCE;
    public Alink(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public boolean onEnable() {
        if (mc.scheduledTasks.size() > 1 || BalancedTimer.stage != BalancedTimer.Stage.IDLE) {
            Notification.addNotification("Alink 不能和 Balanced Timer 同时工作！", "Alink", Notification.NotificationType.ERROR);
            return false;
        }
        return super.onEnable();
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        this.setEnable(false);
    }

    public static boolean isInstanceEnabled() {
        if (INSTANCE == null) return false;
        return INSTANCE.isEnabled();
    }
}
