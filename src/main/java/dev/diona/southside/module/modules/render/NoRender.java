package dev.diona.southside.module.modules.render;

import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.item.EntityItem;

public class NoRender extends Module {
    public NoRender(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onUpdate(final UpdateEvent event) {
        for (final Object o : mc.world.loadedEntityList) {
            if (o instanceof EntityItem) {
                final EntityItem i = (EntityItem) o;
                mc.world.removeEntity(i);
            }
        }
    }
}
