package dev.diona.southside.module.modules.misc;

import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.module.modules.combat.PreferWeapon;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.player.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSword;
import org.lwjglx.input.Mouse;

import java.util.Arrays;

public class MCF extends Module {
    public MCF(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    int lastClickTick = 0;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        boolean state = Mouse.isButtonDown(2);
        if (state) {
            // 获得鼠标正对的实体
            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer player && (mc.player.ticksExisted -lastClickTick > 5)) {
                if (Target.whiteList.contains(player.getUniqueID())) {
                    Target.whiteList.remove(player.getUniqueID());
                    ChatUtil.info("成功从白名单删除" + player.getName());
                } else {
                    Target.whiteList.add(player.getUniqueID());
                    ChatUtil.info("成功添加" + player.getName() + "到白名单");
                }
            }
            lastClickTick = mc.player.ticksExisted;
        }
    }
}
