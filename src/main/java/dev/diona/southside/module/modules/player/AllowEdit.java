package dev.diona.southside.module.modules.player;

import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;

public class AllowEdit extends Module {
    public AllowEdit(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public final void onUpdate(final UpdateEvent event) {
        if (mc.player != null) {
            mc.player.capabilities.allowEdit = true;
        }
    }
}
