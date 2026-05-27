package dev.diona.southside.module.modules.combat;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.render.OldHitting;
import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class SmartBlock extends Module {
    public SmartBlock(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }
    public Slider RangeValue = new Slider("Range", 5, 1, 16, 1);

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player.isHandActive() || !(mc.player.inventory.getCurrentItem().getItem() instanceof ItemSword)) return;

        double detectionRange = RangeValue.getValue().intValue();

        AxisAlignedBB detectionBox = new AxisAlignedBB(
                mc.player.posX - detectionRange, mc.player.posY - detectionRange, mc.player.posZ - detectionRange,
                mc.player.posX + detectionRange, mc.player.posY + detectionRange, mc.player.posZ + detectionRange
        );

        if (!mc.world.getEntitiesWithinAABB(EntityTNTPrimed.class, detectionBox).isEmpty() || !mc.world.getEntitiesWithinAABB(EntityArrow.class, detectionBox).isEmpty()) {
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
//            ChatUtil.info("Block");
            // 怎么显示格挡动画？？？
        }
    }
}
