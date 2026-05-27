package dev.diona.southside.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.math.Vec3d;
import org.lwjglx.BufferUtils;
import org.lwjglx.opengl.GL11;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import java.nio.FloatBuffer;

public class WorldToScreenUtil {
    public static Matrix4f getMatrix(int matrix) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(matrix, floatBuffer);
        return (Matrix4f) new Matrix4f().load(floatBuffer);
    }
    public static Vector2f worldToScreen(Vector3f pointInWorld, int screenWidth, int screenHeight) {
        return worldToScreen(pointInWorld, getMatrix(GL11.GL_MODELVIEW_MATRIX), getMatrix(GL11.GL_PROJECTION_MATRIX), screenWidth, screenHeight);
    }
//    public static Vector2f worldToScreen(Vec3d pointInWorld, int screenWidth, int screenHeight) {
//        return worldToScreen(new Vector3f((float) pointInWorld.x, (float) pointInWorld.y, (float) pointInWorld.z), screenWidth, screenHeight);
//    }
    public static Vector2f worldToScreen(Vector3f pointInWorld, Matrix4f view, Matrix4f projection, int screenWidth, int screenHeight) {
        Vector4f clipSpacePos = multiply(multiply(new Vector4f(pointInWorld.x, pointInWorld.y, pointInWorld.z, 1.0f), view), projection);
        Vector3f ndcSpacePos = new Vector3f(clipSpacePos.x / clipSpacePos.w, clipSpacePos.y / clipSpacePos.w, clipSpacePos.z / clipSpacePos.w);
        float screenX = ((ndcSpacePos.x + 1.0f) / 2.0f) * screenWidth;
        float screenY = ((1.0f - ndcSpacePos.y) / 2.0f) * screenHeight;
        if (ndcSpacePos.z < -1.0 || ndcSpacePos.z > 1.0) {
            return null;
        }
        return new Vector2f(screenX, screenY);
    }
    public static Vector4f multiply(Vector4f vec, Matrix4f mat) {
        return new Vector4f(
                vec.x * mat.m00 + vec.y * mat.m10 + vec.z * mat.m20 + vec.w * mat.m30,
                vec.x * mat.m01 + vec.y * mat.m11 + vec.z * mat.m21 + vec.w * mat.m31,
                vec.x * mat.m02 + vec.y * mat.m12 + vec.z * mat.m22 + vec.w * mat.m32,
                vec.x * mat.m03 + vec.y * mat.m13 + vec.z * mat.m23 + vec.w * mat.m33
        );
    }

//    public static Vector3f worldToScreen2(Vector3d pos, float partialTicks)
//    {
//        Minecraft mc = Minecraft.getMinecraft();
//        ActiveRenderInfo cam = mc.renderGlobal.camera;
//        Vector3d o = cam.getPosition();
//
//        Vector3f pos1 = new Vector3f((float) (o.x - pos.x), (float) (o.y - pos.y), (float) (o.z - pos.z));
//        Quaternion rot = cam.rotation().copy();
//        rot.conj();
//        pos1.transform(rot);
//
//        // Account for view bobbing
//        if (mc.options.bobView && mc.getCameraEntity() instanceof PlayerEntity)
//        {
//            PlayerEntity player = (PlayerEntity) mc.getCameraEntity();
//            float f = player.walkDist - player.walkDistO;
//            float f1 = -(player.walkDist + f * partialTicks);
//            float f2 = MathHelper.lerp(partialTicks, player.oBob, player.bob);
//
//            Quaternion rot1 = Vector3f.XP.rotationDegrees(Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2f) * f2) * 5f);
//            Quaternion rot2 = Vector3f.ZP.rotationDegrees(MathHelper.sin(f1 * (float) Math.PI) * f2 * 3f);
//            rot1.conj();
//            rot2.conj();
//            pos1.transform(rot1);
//            pos1.transform(rot2);
//            pos1.add(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5f, Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0f);
//        }
//
//        MainWindow w = mc.getWindow();
//        float sc = w.getGuiScaledHeight() / 2f / pos1.z() / (float) Math.tan(Math.toRadians(mc.gameRenderer.getFov(cam, partialTicks, true) / 2f));
//        pos1.mul(-sc, -sc, 1f);
//        pos1.add(w.getGuiScaledWidth() / 2f, w.getGuiScaledHeight() / 2f, 0f);
//
//        return pos1;
//    }
}
