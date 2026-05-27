package cc.polyfrost.oneconfig.libs.universal;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.client.renderer.GLAllocation;
import org.apache.commons.lang3.NotImplementedException;
import org.lwjglx.opengl.GL11;
import org.lwjglx.util.vector.Matrix3f;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Quaternion;
import org.lwjglx.util.vector.Vector3f;

public final class UMatrixStack {
//    public static final Companion Companion = new Companion();
    private final Deque<MatrixPair> stack;
    private static final FloatBuffer MATRIX_BUFFER;
    public static final UMatrixStack UNIT;

    private UMatrixStack(Deque stack) {
        this.stack = stack;
    }

    public UMatrixStack() {
        ArrayDeque var1 = new ArrayDeque<MatrixPair>();
        var1.add(new MatrixPair((Matrix4f) new Matrix4f().setIdentity(), (Matrix3f) new Matrix3f().setIdentity()));
        this.stack = var1;
    }

    public final void translate(double x, double y, double z) {
        this.translate((float)x, (float)y, (float)z);
    }

    public final void translate(float x, float y, float z) {
        if(x != 0.0F || y != 0.0F || z != 0.0F) {
            MatrixPair $this$translate_u24lambda_u2d3 = (MatrixPair)this.stack.getLast();
            boolean var5 = false;
            Matrix4f.translate(new Vector3f(x, y, z), $this$translate_u24lambda_u2d3.model(), $this$translate_u24lambda_u2d3.model());
        }
    }

    public final void scale(double x, double y, double z) {
        this.scale((float)x, (float)y, (float)z);
    }

    public final void scale(float x, float y, float z) {
        if(x != 1.0F || y != 1.0F || z != 1.0F) {
            MatrixPair $this$scale_u24lambda_u2d4 = (MatrixPair)this.stack.getLast();
            boolean var5 = false;
            Matrix4f.scale(new Vector3f(x, y, z), $this$scale_u24lambda_u2d4.model(), $this$scale_u24lambda_u2d4.model());
            if(x == y && y == z) {
                if(x < 0.0F) {
                    Matrix3f.negate($this$scale_u24lambda_u2d4.normal(), $this$scale_u24lambda_u2d4.normal());
                }
            } else {
                float ix = 1.0F / x;
                float iy = 1.0F / y;
                float iz = 1.0F / z;
                float rt = (float)Math.cbrt((double)(ix * iy * iz));
                Matrix3f scale = new Matrix3f();
                scale.m00 = rt * ix;
                scale.m11 = rt * iy;
                scale.m22 = rt * iz;
                Matrix3f.mul($this$scale_u24lambda_u2d4.normal(), scale, $this$scale_u24lambda_u2d4.normal());
            }

        }
    }

    public final void rotate(float angle, float x, float y, float z, boolean degrees) {
        if(angle != 0.0F) {
            MatrixPair $this$rotate_u24lambda_u2d6 = (MatrixPair)this.stack.getLast();
            boolean var7 = false;
            float angleRadians = degrees?(float)Math.toRadians((double)angle):angle;
            Vector3f axis = new Vector3f(x, y, z);
            Matrix4f.rotate(angleRadians, axis, $this$rotate_u24lambda_u2d6.model(), $this$rotate_u24lambda_u2d6.model());
            Matrix3f.mul($this$rotate_u24lambda_u2d6.normal(), makeRotationMatrix(angleRadians, axis), $this$rotate_u24lambda_u2d6.normal());
        }
    }

    public static void rotate$default(UMatrixStack var0, float var1, float var2, float var3, float var4, boolean var5, int var6, Object var7) {
        if((var6 & 16) != 0) {
            var5 = true;
        }

        var0.rotate(var1, var2, var3, var4, var5);
    }

    public final void multiply(Quaternion quaternion) throws Throwable {
//        Intrinsics.checkNotNullParameter(quaternion, "quaternion");
        MatrixPair $this$multiply_u24lambda_u2d7 = (MatrixPair)this.stack.getLast();
        boolean var3 = false;
        String var4 = "lwjgl quaternion multiply";
        throw (Throwable)(new NotImplementedException("An operation is not implemented: " + var4));
    }

    public final UMatrixStack fork() {
        ArrayDeque var1 = new ArrayDeque();
        boolean var3 = false;
        var1.add(new MatrixPair(
                new Matrix4f().load(this.stack.getLast().model()),
                new Matrix3f().load(this.stack.getLast().normal())
        ));
        Deque var4 = (Deque)var1;
        return new UMatrixStack(var4);
    }

    public final void push() {
        this.stack.addLast(new MatrixPair(
                new Matrix4f().load(this.stack.getLast().model()),
                new Matrix3f().load(this.stack.getLast().normal())
        ));
    }

    public final void pop() {
        this.stack.removeLast();
    }

    public final MatrixPair peek() {
        Object var1 = this.stack.getLast();
//        Intrinsics.checkNotNullExpressionValue(var1, "stack.last");
        return (MatrixPair)var1;
    }

    public final boolean isEmpty() {
        return this.stack.size() == 1;
    }

    public final void applyToGlobalState() {
        ((MatrixPair)this.stack.getLast()).model().store(MATRIX_BUFFER);
        ((Buffer)MATRIX_BUFFER).rewind();
        GL11.glMultMatrix(MATRIX_BUFFER);
    }

    public final void replaceGlobalState() {
        GL11.glLoadIdentity();
        this.applyToGlobalState();
    }

//    public final void runWithGlobalState(Runnable block) {
////        Intrinsics.checkNotNullParameter(block, "block");
//        this.runWithGlobalState((Function0)(new 1(block)));
//    }

    public final Object runWithGlobalState(Callable<Object> block) {
//        Intrinsics.checkNotNullParameter(block, "block");
        boolean $i$f$withGlobalStackPushed = false;
        UGraphics.GL.pushMatrix();
        boolean var4 = false;
        this.applyToGlobalState();
        Object var5 = null;
        try {
            var5 = block.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean var7 = false;
        UGraphics.GL.popMatrix();
        return var5;
    }

//    public final void runReplacingGlobalState(Runnable block) {
////        Intrinsics.checkNotNullParameter(block, "block");
//        this.runReplacingGlobalState((Function0)(new cc.polyfrost.oneconfig.libs.universal.UMatrixStack.runReplacingGlobalState.1(block)));
//    }

    public final Object runReplacingGlobalState(Callable<Object> block) {
//        Intrinsics.checkNotNullParameter(block, "block");
        boolean $i$f$withGlobalStackPushed = false;
        UGraphics.GL.pushMatrix();
        boolean var4 = false;
        this.replaceGlobalState();
        Object var5 = null;
        try {
            var5 = block.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean var7 = false;
        UGraphics.GL.popMatrix();
        return var5;
    }

    private final Object withGlobalStackPushed(Callable<Object> block) {
        boolean $i$f$withGlobalStackPushed = false;
        UGraphics.GL.pushMatrix();
        Object var3 = null;
        try {
            var3 = block.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean var5 = false;
        UGraphics.GL.popMatrix();
        return var3;
    }

    public final void rotate(float angle, float x, float y, float z) {
        rotate$default(this, angle, x, y, z, false, 16, (Object)null);
    }

    private static final Matrix3f makeRotationMatrix(float angle, Vector3f axis) {
        Matrix3f var2 = new Matrix3f();
        boolean var4 = false;
        float c = (float)Math.cos((double)angle);
        float s = (float)Math.sin((double)angle);
        float oneMinusC = (float)1 - c;
        float xx = axis.x * axis.x;
        float xy = axis.x * axis.y;
        float xz = axis.x * axis.z;
        float yy = axis.y * axis.y;
        float yz = axis.y * axis.z;
        float zz = axis.z * axis.z;
        float xs = axis.x * s;
        float ys = axis.y * s;
        float zs = axis.z * s;
        var2.m00 = xx * oneMinusC + c;
        var2.m01 = xy * oneMinusC + zs;
        var2.m02 = xz * oneMinusC - ys;
        var2.m10 = xy * oneMinusC - zs;
        var2.m11 = yy * oneMinusC + c;
        var2.m12 = yz * oneMinusC + xs;
        var2.m20 = xz * oneMinusC + ys;
        var2.m21 = yz * oneMinusC - xs;
        var2.m22 = zz * oneMinusC + c;
        return var2;
    }

    static {
        FloatBuffer var0 = GLAllocation.createDirectFloatBuffer(16);
//        Intrinsics.checkNotNullExpressionValue(var0, "createDirectFloatBuffer(16)");
        MATRIX_BUFFER = var0;
        UNIT = new UMatrixStack();
    }

    public static final class Compat {
        public static final Compat INSTANCE = new Compat();

        public static final String DEPRECATED = "For 1.17 this method requires you pass a UMatrixStack as the first argument.\n\nIf you are currently extending this method, you should instead extend the method with the added argument.\nNote however for this to be non-breaking, your parent class needs to transition before you do.\n\nIf you are calling this method and you cannot guarantee that your target class has been fully updated (such as when\ncalling an open method on an open class), you should instead call the method with the \"Compat\" suffix, which will\ncall both methods, the new and the deprecated one.\nIf you are sure that your target class has been updated (such as when calling the super method), you should\n(for super calls you must!) instead just call the method with the original name and added argument.";

        private static final List<UMatrixStack> stack = new ArrayList<>();

        public final <R> R runLegacyMethod(UMatrixStack matrixStack, Callable<Object> block) {
//            Intrinsics.checkNotNullParameter(matrixStack, "matrixStack");
//            Intrinsics.checkNotNullParameter(block, "block");
            stack.add(matrixStack);
            R r = null;
            try {
                r = (R)block.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            int $i$a$-also-UMatrixStack$Compat$runLegacyMethod$1 = 0;
            stack.remove(stack.size() - 1);
            return r;
        }

        public final UMatrixStack get() {
            if (stack.isEmpty()) return new UMatrixStack();
            return stack.get(stack.size() - 1);
        }
    }
}
