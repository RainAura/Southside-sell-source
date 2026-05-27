package dev.diona.southside.module.modules.render;

import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.event.events.NewRender2DEvent;
import dev.diona.southside.event.events.TickEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class FreeCam extends Module {
    public static FreeCam INSTANCE;


    public Slider slowdownFactor = new Slider("Slowdown Factor", 0.01, 1e-9, 0.5, 0.1);
    public Slider acceleration = new Slider("Acceleration", 50, 5, 500, 0.1);
    public Slider maxSpeed = new Slider("Max Speed", 50, 5, 500, 0.1);

    private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private final Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
    private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
    private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
    private int oldCameraType;
    private MovementInput oldInput;
    private double x, y, z;
    private float yRot, xRot;
    private double forwardVelocity;
    private double leftVelocity;
    private double upVelocity;
    private long lastTime;
    public FreeCam(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getXRot() {
        return xRot;
    }

    public float getYRot() {
        return yRot;
    }


    public void onMouseTurn(double yRot, double xRot) {
        this.xRot += (float) xRot * 0.15F;
        this.yRot += (float) yRot * 0.15F;
        this.xRot = MathHelper.clamp(this.xRot, -90, 90);
        calculateVectors();
    }

    @EventListener
    public void onClientTickStart(TickEvent event) {
            while (mc.gameSettings.keyBindTogglePerspective.isPressed()) {
                // consume clicks
            }
            oldInput.updatePlayerMoveState();
    }

    @EventListener
    public void onRenderTickStart(NewRender2DEvent event) {
        if (isEnabled()) {
            long currTime = System.nanoTime();
            float frameTime = (currTime - lastTime) / 1e9f;
            lastTime = currTime;

            MovementInput input = oldInput;
            float forwardImpulse = input.moveForward;
            float leftImpulse = input.moveStrafe;
            float upImpulse = (input.jump ? 1 : 0) + (input.sneak ? -1 : 0);
            double slowdown = Math.pow(slowdownFactor.getValue().doubleValue(), frameTime);
            forwardVelocity = combineMovement(forwardVelocity, forwardImpulse, frameTime, acceleration.getValue().doubleValue(), slowdown);
            leftVelocity = combineMovement(leftVelocity, leftImpulse, frameTime, acceleration.getValue().doubleValue(), slowdown);
            upVelocity = combineMovement(upVelocity, upImpulse, frameTime, acceleration.getValue().doubleValue(), slowdown);

            double dx = (double) this.forwards.x * forwardVelocity + (double) this.left.x * leftVelocity;
            double dy = (double) this.forwards.y * forwardVelocity + upVelocity + (double) this.left.y * leftVelocity;
            double dz = (double) this.forwards.z * forwardVelocity + (double) this.left.z * leftVelocity;
            dx *= frameTime;
            dy *= frameTime;
            dz *= frameTime;
            double speed = Math.sqrt(dx * dx + dy * dy + dz * dz) / frameTime;
            if (speed > maxSpeed.getValue().doubleValue()) {
                double factor = maxSpeed.getValue().doubleValue() / speed;
                forwardVelocity *= factor;
                leftVelocity *= factor;
                upVelocity *= factor;
                dx *= factor;
                dy *= factor;
                dz *= factor;
            }
            x += dx;
            y += dy;
            z += dz;
        }
    }

    public void onGetDebugInfoLeft(List<String> list) {
        if (isEnabled()) {
            list.add("");
            list.add(String.format("FreeCam XYZ: %.3f / %.5f / %.3f", x, y, z));
        }
    }

    private double px, py, pz, lastX, lastY, lastZ, llX, llY, llZ;
    private float eXRot, eYRot, lastXRot, lastYRot;
    private boolean pNoClip;
    private Entity override;
    private boolean entitiesRendering;

    public void onBeforeRenderWorld() {
        override = null;

        if (!isEnabled()) {
            return;
        }

        Entity cameraEntity = mc.getRenderViewEntity();
        if (cameraEntity == null) {
            return;
        }

        override = cameraEntity;
        saveCameraEntityPosition();
        moveCameraEntityToFreeCamPosition();
        pNoClip = override.noClip;
        override.noClip = true;
    }

    public void onAfterRenderWorld() {
        if (override == null) {
            return;
        }

        restoreCameraEntityPosition();
        override.noClip = pNoClip;
        override = null;
    }

    public void onBeforeRenderEntity(Entity entity) {
        if (override == entity) {
            restoreCameraEntityPosition();
        }
    }

    public void onAfterRenderEntity(Entity entity) {
        if (override == entity) {
            moveCameraEntityToFreeCamPosition();
        }
    }

    public void onBeforeRenderEntities() {
        entitiesRendering = true;
        if (override != null) {
            mc.gameSettings.thirdPersonView = 1;
        }
    }

    public void onAfterRenderEntities() {
        entitiesRendering = false;
        if (override != null) {
            mc.gameSettings.thirdPersonView = 0;
        }
    }

    public double getViewFrustumEntityPosX(double viewEntityX) {
        return override != null ? px : viewEntityX;
    }

    public double getViewFrustumEntityPosZ(double viewEntityZ) {
        return override != null ? pz : viewEntityZ;
    }

    public boolean shouldOverrideSpectator(AbstractClientPlayer player) {
        if (override == player && !entitiesRendering) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onEnable() {
        Entity entity = mc.getRenderViewEntity();
        if (entity == null) {
            return super.onEnable();
        }

        oldCameraType = mc.gameSettings.thirdPersonView;
        oldInput = mc.player.movementInput;
        mc.player.movementInput = new MovementInput();
        mc.gameSettings.thirdPersonView = 0;

        Vec3d pos = entity.getPositionEyes(1);
        x = pos.x;
        y = pos.y;
        z = pos.z;
        yRot = entity.rotationYaw;
        xRot = entity.rotationPitch;

        calculateVectors();

        double distance = -2;
        x += (double)this.forwards.x * distance;
        y += (double)this.forwards.y * distance;
        z += (double)this.forwards.z * distance;

        forwardVelocity = 0;
        leftVelocity = 0;
        upVelocity = 0;

        lastTime = System.nanoTime();

        return super.onEnable();
    }

    @Override
    public boolean onDisable() {
        mc.gameSettings.thirdPersonView = oldCameraType;
        mc.player.movementInput = oldInput;
        return super.onDisable();
    }

    private void calculateVectors() {
        rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
        rotation.mul(Vector3f.YP.rotationDegrees(-yRot));
        rotation.mul(Vector3f.XP.rotationDegrees(xRot));
        forwards.set(0.0F, 0.0F, 1.0F);
        forwards.transform(rotation);
        up.set(0.0F, 1.0F, 0.0F);
        up.transform(rotation);
        left.set(1.0F, 0.0F, 0.0F);
        left.transform(rotation);
    }

    private double combineMovement(double velocity, double impulse, double frameTime, double acceleration, double slowdown) {
        if (impulse != 0) {
            if (impulse > 0 && velocity < 0) {
                velocity = 0;
            }
            if (impulse < 0 && velocity > 0) {
                velocity = 0;
            }
            velocity += acceleration * impulse * frameTime;
        } else {
            velocity *= slowdown;
        }
        return velocity;
    }

    private void saveCameraEntityPosition() {
        px = override.posX;
        py = override.posY;
        pz = override.posZ;
        lastX = override.lastTickPosX;
        lastY = override.lastTickPosY;
        lastZ = override.lastTickPosZ;
        llX = override.prevPosX;
        llY = override.prevPosY;
        llZ = override.prevPosZ;
        eXRot = override.rotationPitch;
        eYRot = override.rotationYaw;
        lastXRot = override.prevRotationPitch;
        lastYRot = override.prevRotationYaw;
    }

    private void restoreCameraEntityPosition() {
        override.posX = px;
        override.posY = py;
        override.posZ = pz;
        override.lastTickPosX = lastX;
        override.lastTickPosY = lastY;
        override.lastTickPosZ = lastZ;
        override.prevPosX = llX;
        override.prevPosY = llY;
        override.prevPosZ = llZ;
        override.rotationPitch = eXRot;
        override.rotationYaw = eYRot;
        override.prevRotationPitch = lastXRot;
        override.prevRotationYaw = lastYRot;
    }

    private void moveCameraEntityToFreeCamPosition() {
        override.posX = override.lastTickPosX = override.prevPosX = x;
        override.posY = override.lastTickPosY = override.prevPosY = y;
        override.posZ = override.lastTickPosZ = override.prevPosZ = z;
        override.rotationPitch = override.prevRotationPitch = xRot;
        override.rotationYaw = override.prevRotationYaw = yRot;
    }

    static class Vector3f {

        public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
        public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
        public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
        public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
        public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
        public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);

        public float x;
        public float y;
        public float z;

        public Vector3f(float p_i48098_1_, float p_i48098_2_, float p_i48098_3_) {
            this.x = p_i48098_1_;
            this.y = p_i48098_2_;
            this.z = p_i48098_3_;
        }

        public void set(float p_195905_1_, float p_195905_2_, float p_195905_3_) {
            this.x = p_195905_1_;
            this.y = p_195905_2_;
            this.z = p_195905_3_;
        }

        public Quaternion rotationDegrees(float p_229187_1_) {
            return new Quaternion(this, p_229187_1_, true);
        }

        public void transform(Quaternion p_214905_1_) {
            Quaternion quaternion = new Quaternion(p_214905_1_);
            quaternion.mul(new Quaternion(this.x, this.y, this.z, 0.0F));
            Quaternion quaternion1 = new Quaternion(p_214905_1_);
            quaternion1.conj();
            quaternion.mul(quaternion1);
            this.set(quaternion.i, quaternion.j, quaternion.k);
        }
    }

    static class Quaternion {

        public float i;
        public float j;
        public float k;
        public float r;

        public Quaternion(float p_i48100_1_, float p_i48100_2_, float p_i48100_3_, float p_i48100_4_) {
            this.i = p_i48100_1_;
            this.j = p_i48100_2_;
            this.k = p_i48100_3_;
            this.r = p_i48100_4_;
        }

        public Quaternion(Vector3f p_i48101_1_, float p_i48101_2_, boolean p_i48101_3_) {
            if (p_i48101_3_) {
                p_i48101_2_ *= ((float) Math.PI / 180F);
            }

            float f = sin(p_i48101_2_ / 2.0F);
            this.i = p_i48101_1_.x * f;
            this.j = p_i48101_1_.y * f;
            this.k = p_i48101_1_.z * f;
            this.r = cos(p_i48101_2_ / 2.0F);
        }

        public Quaternion(Quaternion p_i48103_1_) {
            this.i = p_i48103_1_.i;
            this.j = p_i48103_1_.j;
            this.k = p_i48103_1_.k;
            this.r = p_i48103_1_.r;
        }

        private static float cos(float p_214904_0_) {
            return (float) Math.cos(p_214904_0_);
        }

        private static float sin(float p_214903_0_) {
            return (float) Math.sin(p_214903_0_);
        }

        public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
            this.i = p_227066_1_;
            this.j = p_227066_2_;
            this.k = p_227066_3_;
            this.r = p_227066_4_;
        }

        public void mul(Quaternion p_195890_1_) {
            float f = this.i;
            float f1 = this.j;
            float f2 = this.k;
            float f3 = this.r;
            float f4 = p_195890_1_.i;
            float f5 = p_195890_1_.j;
            float f6 = p_195890_1_.k;
            float f7 = p_195890_1_.r;
            this.i = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
            this.j = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
            this.k = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
            this.r = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
        }

        public void conj() {
            this.i = -this.i;
            this.j = -this.j;
            this.k = -this.k;
        }
    }
}
