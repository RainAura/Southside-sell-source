package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.movement.Stuck;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.module.modules.world.Scaffold;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.*;
import dev.diona.southside.util.world.ProjectileUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemEnderPearl;

import java.awt.*;

public class AntiVoid extends Module {
    public final Slider fallDistValue = new Slider("Fall Distance", 3, 1, 10, 1);
    public Switch debugValue = new Switch("Debug", false);
    public final Switch scaffoldValue = new Switch("Scaffold", true);
    public final Slider attemptTime = new Slider("Max Attempt Times", 1, 2, 5);
    private static final double T = 10;
    private static final double T_MIN = 0.001;
    private static final double ALPHA = 0.997;
    private CalculateThread calculateThread;
    private int attempted;
    private boolean scaffoldEnabled;
    //    private int ticksLeft;
    private boolean calculating;

    public AntiVoid(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public void onMoveInput(MoveInputEvent event) {
        if (calculating) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRender(Render2DEvent event) {
        if (!debugValue.getValue()) return;
        Southside.fontManager.font.drawString(12, "assessment: " + new ProjectileUtil.EnderPearlPredictor(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.motionY - 0.01, mc.player.motionY + 0.02).assessRotation(RotationUtil.getPlayerRotation()), 20, 20, Color.WHITE);
        Southside.fontManager.font.drawString(12, "(" + mc.player.rotationYaw + ", " + mc.player.rotationPitch + ")", 20, 30, Color.WHITE);
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (mc.player == null) return;
        if (mc.player.onGround) {
            if (scaffoldEnabled) {
                Southside.moduleManager.getModuleByClass(Scaffold.class).setEnable(false);
                scaffoldEnabled = false;
            }
            attempted = 0;
            calculating = false;
        }

        if (event.getState() == EventState.POST) {
            if (calculating && (calculateThread == null || calculateThread.completed)) {
                calculating = false;
                Stuck.throwPearl(calculateThread.solution);
            }
        }
        if (mc.player.motionY < 0.1 && new FallingPlayer(mc.player).findCollision(60) == null && !PlayerUtil.isBlockUnder(mc.player.posY + mc.player.getEyeHeight()) && mc.player.fallDistance > fallDistValue.getValue().floatValue()) {
            Scaffold scaffold = (Scaffold) Southside.moduleManager.getModuleByClass(Scaffold.class);
            Stuck stuck = (Stuck) Southside.moduleManager.getModuleByClass(Stuck.class);
            if (mc.player.motionY >= -1 && !scaffold.isEnabled() && !stuck.isEnabled()) {
                scaffoldEnabled = true;
                scaffold.setEnable(true);
                scaffold.bigVelocityTick = 10;
            } else if (mc.player.motionY < -1 && attempted <= this.attemptTime.getValue().intValue() && !mc.player.onGround) {
//                ticksLeft = 10;
                attempted += 1;

                int findSlot = -1;
                for (int i = 36; i <= 44; i++) {
                    if (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemEnderPearl) {
                        findSlot = i;
                        break;
                    }
                }

                if (findSlot == -1) {
                    for (int i = 0; i <= 35; i++) {
                        if (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemEnderPearl) {
                            findSlot = i;
                            break;
                        }
                    }
                    if (findSlot == -1) {
                        return;
                    }

                    InventoryUtil.swap(findSlot, 8);
                    findSlot = 44;
                }
                if (scaffold.isEnabled()) {
                    scaffold.setEnable(false);
                    scaffoldEnabled = false;
                }
                mc.player.inventory.currentItem = findSlot - 36;

                calculating = true;
                calculateThread = new CalculateThread(
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ,
                        0,
                        0
                );
                calculateThread.start();

//                Southside.moduleManager.getModuleByName("Stuck").setEnable(true);
                stuck.setEnable(true);
            }
        }
    }

    @Override
    public boolean onDisable() {
        Southside.moduleManager.getModuleByName("Stuck").setEnable(false);
        return super.onDisable();
    }

    private class CalculateThread extends Thread {
        private int iteration;
        private boolean completed;
        private double temperature, energy, solutionE;
        private Rotation solution;
        public boolean stop;
        private final ProjectileUtil.EnderPearlPredictor predictor;

        private CalculateThread(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            predictor = new ProjectileUtil.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = T;
            this.energy = 0;
            stop = false;
            completed = false;
        }

        @Override
        public void run() {
            TimerUtil timer = new TimerUtil();
            timer.reset();
            solution = new Rotation(
                    MathUtil.getRandomInRange(-180, 180),
                    MathUtil.getRandomInRange(-90, 90)
            );
            Rotation current = solution;
            try {
                energy = predictor.assessRotation(solution);
            } catch (Exception ignored) {
                ChatUtil.info("请手动丢珍珠");
                return;
            }
            solutionE = energy;
            while (temperature >= T_MIN && !stop) {
                try {
                    Rotation rotation = new Rotation(
                            (float) (current.yaw + MathUtil.getRandomInRange(-temperature * 18, temperature * 18)),
                            (float) (current.pitch + MathUtil.getRandomInRange(-temperature * 9, temperature * 9))
                    );
                    rotation.fixPitch();
                    double assessment = predictor.assessRotation(rotation);
                    double deltaE = assessment - energy;
                    if (deltaE >= 0 || MathUtil.getRandomInRange(0, 1) < Math.exp(-deltaE / temperature * 100)) {
                        energy = assessment;
                        current = rotation;
                        if (assessment > solutionE) {
                            solutionE = assessment;
                            solution = new Rotation(rotation.yaw, rotation.pitch);
                            ChatUtil.info("Find a better solution: (" + solution.yaw + ", " + solution.pitch + "), value: " + solutionE);
                        }
                    }
                    temperature *= ALPHA;
                    iteration++;
                } catch (Exception ignored) {

                }
            }
            ChatUtil.info("Simulated annealing completed within " + iteration + " iterations");
            ChatUtil.info("Time used: " + timer.passed() + " solution energy: " + solutionE);
            completed = true;
        }
    }
}