package dev.diona.southside.module.modules.nontoggleable;

import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.event.events.ClientTickEvent;
import dev.diona.southside.module.NonToggleableModule;
import dev.diona.southside.util.authentication.AuthenticationStatus;
import dev.diona.southside.util.authentication.WebUtil;
import dev.diona.southside.util.misc.TimerUtil;
import me.bush.eventbus.annotation.EventListener;

import java.util.HashMap;

public class SessionKeepAlive extends NonToggleableModule {
    private final TimerUtil delay = new TimerUtil();
    public SessionKeepAlive() {
        super("SessionKeepAlive", "会话保持");
        delay.reset();
    }

    @EventListener
    public final void onTickEvent(final ClientTickEvent event) {
        if (delay.hasReached(1000 * 60)) {
            delay.reset();
            final var params = new HashMap<String, String>();
            params.put("token", AuthenticationStatus.INSTANCE.token);
            params.put("sessionToken", AuthenticationStatus.INSTANCE.session);
            WebUtil.api("/session/keepalive", params, new WebUtil.Callback<>() {
                @Override
                public void onSuccess(JsonObject e) {
                    final var format = String.format("%s %s %s", e.get("code"), e.get("msg"), e.get("data"));
                    System.out.println(format);
                }

                @Override
                public void onFail(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
