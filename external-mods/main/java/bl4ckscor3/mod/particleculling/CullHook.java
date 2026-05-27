package bl4ckscor3.mod.particleculling;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class CullHook {
    public static boolean shouldRenderParticle(Particle particle, BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if( Minecraft.getMinecraft().player.isSpectator())
            return true;
        ICamera camera = Minecraft.getMinecraft().renderGlobal.camera;
        if(camera == null)
            return true;
        if(camera.isBoundingBoxInFrustum(particle.getBoundingBox())) {
            RayTraceResult result = entity.world.rayTraceBlocks(new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), new Vec3d(particle.posX, particle.posY, particle.posZ), false, true, true);
            if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState state = entity.world.getBlockState(result.getBlockPos());
                if(state.isFullCube() && state.isOpaqueCube())
                    return false;
            }
            return true;
        }
        return false;
    }
}
