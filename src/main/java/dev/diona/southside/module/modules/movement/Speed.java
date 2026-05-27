package dev.diona.southside.module.modules.movement;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.combat.KillAura;
import dev.diona.southside.module.modules.player.Blink;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.misc.FakePlayer;
import dev.diona.southside.util.player.MovementUtil;
import dev.diona.southside.util.player.PlayerUtil;
import dev.diona.southside.util.player.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.math.AxisAlignedBB;

public class Speed extends Module {
    public static Speed INSTANCE;
    public Slider speedOption = new Slider("Speed", 0.05, 0.01, 0.08, 0.01);
    public Switch followTargetOption = new Switch("Follow Target", true);
    public Switch onlyJumpOption = new Switch("Only follow when jump", true);
    public Switch hurttimeCheck = new Switch("HurtTime Check", false);
    public Switch adaptiveOption = new Switch("Adaptive", true);


    public Speed(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public boolean onDisable() {
        mc.gameSettings.keyBindLeft.setPressed(GameSettings.isKeyDown(mc.gameSettings.keyBindLeft));
        return super.onDisable();
    }

    @EventListener
    public void onPre(MotionEvent event) {
        if (Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled() || mc.currentScreen instanceof GuiChat || mc.player.hurtTime > 6 && hurttimeCheck.getValue())
            return;
        if (event.getState() == EventState.PRE) {
            AxisAlignedBB playerBox = mc.player.getEntityBoundingBox().expand(1.0, 1.0, 1.0);
            int c = 0;

            for (Entity entity : mc.world.loadedEntityList) {
                if ((entity instanceof EntityLivingBase || entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityFishHook) && !(entity instanceof EntityArmorStand) && !(entity instanceof FakePlayer) && entity.getEntityId() != mc.player.getEntityId() && playerBox.intersects(entity.getEntityBoundingBox()) && entity.getEntityId() != -8 && entity.getEntityId() != -1337 && !Blink.isInstanceEnabled()) {
                    c++;
                }
            }
            if (c > 0 && MovementUtil.isMoving() && mc.player.isSprinting()) {
                double strafeOffset = (Math.min(c, 3)) * speedOption.getValue().doubleValue();

                float yaw = getMoveYaw();

                double mx = -Math.sin(Math.toRadians(yaw));
                double mz = Math.cos(Math.toRadians(yaw));

                if (mc.player.movementInput.moveForward == 0 && mc.player.movementInput.moveStrafe == 0) {

                    if (mc.player.motionX > strafeOffset) {
                        mc.player.motionX -= strafeOffset;
                    } else if (mc.player.motionX < -strafeOffset) {
                        mc.player.motionX += strafeOffset;
                    } else {
                        mc.player.motionX = 0.0;
                    }
                    if (mc.player.motionZ > strafeOffset) {
                        mc.player.motionZ -= strafeOffset;
                    } else if (mc.player.motionZ < -strafeOffset) {
                        mc.player.motionZ += strafeOffset;
                    } else {
                        mc.player.motionZ = 0.0;
                    }

                }
                if (mx < 0.0) {
                    if (mc.player.motionX > strafeOffset) {
                        mc.player.motionX -= strafeOffset;
                    } else
                        mc.player.motionX += mx * strafeOffset;

                } else if (mx > 0.0) {
                    if (mc.player.motionX < -strafeOffset) {
                        mc.player.motionX += strafeOffset;
                    } else
                        mc.player.motionX += mx * strafeOffset;

                }

                if (mz < 0.0) {
                    if (mc.player.motionZ > strafeOffset) {
                        mc.player.motionZ -= strafeOffset;
                    } else
                        mc.player.motionZ += mz * strafeOffset;

                } else if (mz > 0.0) {
                    if (mc.player.motionZ < -strafeOffset) {
                        mc.player.motionZ += strafeOffset;
                    } else
                        mc.player.motionZ += mz * strafeOffset;
                }
                if (c < 4 && shouldFollow()) {
                    mc.gameSettings.keyBindLeft.setPressed(true);
                } else {
                    mc.gameSettings.keyBindLeft.setPressed(GameSettings.isKeyDown(mc.gameSettings.keyBindLeft));
                }
            } else {
                mc.gameSettings.keyBindLeft.setPressed(GameSettings.isKeyDown(mc.gameSettings.keyBindLeft));
            }
        }
    }

    public boolean shouldFollow() {
        return this.isEnabled() && !Southside.moduleManager.getModuleByClass(Scaffold.class).isEnabled() && KillAura.getTarget() != null && (!onlyJumpOption.getValue() || mc.gameSettings.keyBindJump.isKeyDown()) && (PlayerUtil.isBlockUnder(KillAura.getTarget().getPosition().getY(), KillAura.getTarget()) || !adaptiveOption.getValue()) && followTargetOption.getValue();
    }

    private float getMoveYaw() {
        float moveYaw = mc.player.rotationYaw;

        if (mc.player.moveForward != 0 && mc.player.moveStrafing == 0) {
            moveYaw += (mc.player.moveForward > 0) ? 0 : 180;
        } else if (mc.player.moveForward != 0 && mc.player.moveStrafing != 0) {
            if (mc.player.moveForward > 0) {
                moveYaw += (mc.player.moveStrafing > 0) ? -45 : 45;
            } else {
                moveYaw -= (mc.player.moveStrafing > 0) ? -45 : 45;
            }
            moveYaw += (mc.player.moveForward > 0) ? 0 : 180;
        } else if (mc.player.moveStrafing != 0 && mc.player.moveForward == 0) {
            moveYaw += (mc.player.moveStrafing > 0) ? -90 : 90;
        }
        if (RotationUtil.targetRotation != null && KillAura.getTarget() != null && followTargetOption.getValue() && (!onlyJumpOption.getValue() || mc.gameSettings.keyBindJump.isKeyDown())) {
            moveYaw = RotationUtil.targetRotation.yaw;
        }
        return moveYaw;
    }

    @Override
    public String getSuffix() {
        return "Grim";
    }
}
