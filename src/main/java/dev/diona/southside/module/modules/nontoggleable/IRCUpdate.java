package dev.diona.southside.module.modules.nontoggleable;

import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.SendMessageEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.NonToggleableModule;
import dev.diona.southside.module.modules.client.IRCFriend;
import dev.diona.southside.util.chat.Chat;
import me.bush.eventbus.annotation.EventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.diona.southside.Southside.MC.mc;

public class IRCUpdate extends NonToggleableModule {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    public IRCUpdate() {
        super("IRCUpdate", "用来更新irc user mcname uuid");
    }

    @EventListener
    public final void onWorldEvent(final WorldEvent event) {
        EXECUTOR_SERVICE.submit(()-> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}

            update();
        });
    }

    @EventListener
    public void onChat(SendMessageEvent event) {
        if (event.getMessage().startsWith("`")) {
            String rest = event.getMessage().substring(1);
            event.setCancelled(true);
            Chat.getInstance().sendMessage(Chat.getInstance().getChannels().get(0), rest);
        }
    }

    public void update() {
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
            data.addProperty("friend", Southside.moduleManager.getModuleByClass(IRCFriend.class).isEnabled());
            Chat.getInstance().sendRaw(data.toString());
        }
    }
}
