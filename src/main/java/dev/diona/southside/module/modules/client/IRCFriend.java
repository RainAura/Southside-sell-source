package dev.diona.southside.module.modules.client;

import com.google.gson.JsonObject;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;

public class IRCFriend extends Module {
    public IRCFriend(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    private final TimerUtil attackTimer = new TimerUtil();

    @Override
    public boolean onEnable() {
        updateFriendStatus(true);
        return super.onEnable();
    }

    @Override
    public boolean onDisable() {
        updateFriendStatus(false);
        attackTimer.reset();
        Notification.addNotificationKeepTime("You can attack users again after 10s.", "IRC Friend", Notification.NotificationType.INFO, 10);
        return super.onDisable();
    }

    public boolean canAttack() {
        return !this.isEnabled() && attackTimer.hasReached(10000);
    }

    @EventListener
    public void onWorld(WorldEvent event) {
        if (mc.player != null) {
            updateFriendStatus(true);
        }
    }

    private void updateFriendStatus(final boolean enable) {
        if (Chat.getInstance().isOpen()) {
            JsonObject data = new JsonObject();
            data.addProperty("type", "update");
            String uuid = null, name = null;
            if (mc.player != null) {
                uuid = mc.player.getUniqueID().toString();
                name = mc.player.getName().toString();
            }
            data.addProperty("mcName", name);
            data.addProperty("uuid", uuid);
            data.addProperty("friend", enable);
            Chat.getInstance().sendRaw(data.toString());
        }
    }
}
