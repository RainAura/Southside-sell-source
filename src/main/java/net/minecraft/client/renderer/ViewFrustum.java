package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import dev.diona.southside.module.modules.render.FreeCam;
import net.jafama.FastMath;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.render.VboRegion;

public class ViewFrustum
{
    protected final RenderGlobal renderGlobal;
    protected final World world;
    protected int countChunksY;
    protected int countChunksX;
    protected int countChunksZ;
    public RenderChunk[] renderChunks;
    private final Map<ChunkPos, VboRegion[]> mapVboRegions = new HashMap<ChunkPos, VboRegion[]>();

    public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal renderGlobalIn, IRenderChunkFactory renderChunkFactory)
    {
        this.renderGlobal = renderGlobalIn;
        this.world = worldIn;
        this.setCountChunksXYZ(renderDistanceChunks);
        this.createRenderChunks(renderChunkFactory);
    }

    /**
     * @reason Improving the performance of this method by using a single loop instead of multiple nested ones and avoiding allocating in loop.
     *         Improving the performance of this method is beneficial as it reduces lag when loading renderer.
     *         For example, when loading in a world, changing the render distance, changing graphics quality, etc.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    protected void createRenderChunks(IRenderChunkFactory renderChunkFactory)
    {
        final int totalRenderChunks = countChunksX * countChunksY * countChunksZ;

        int xChunkIndex = 0;
        int yChunkIndex = 0;
        int zChunkIndex = 0;

        renderChunks = new RenderChunk[totalRenderChunks];

        for (int i = 0; i < totalRenderChunks; ++i) {
            if (xChunkIndex == countChunksX) {
                xChunkIndex = 0;
                ++yChunkIndex;
                if (yChunkIndex == countChunksY) {
                    yChunkIndex = 0;
                    ++zChunkIndex;
                }
            }

            renderChunks[i] = new RenderChunk(world, renderGlobal, i);
            renderChunks[i].setPosition(xChunkIndex * 16, yChunkIndex * 16, zChunkIndex * 16);

            ++xChunkIndex;
        }
    }

    public void deleteGlResources()
    {
        for (RenderChunk renderchunk : this.renderChunks)
        {
            renderchunk.deleteGlResources();
        }

        this.deleteVboRegions();
    }

    protected void setCountChunksXYZ(int renderDistanceChunks)
    {
        int i = renderDistanceChunks * 2 + 1;
        this.countChunksX = i;
        this.countChunksY = 16;
        this.countChunksZ = i;
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Improving the performance of this method is beneficial as it reduces lag when loading renderer.
     *         For example, when loading in a world, changing the render distance, changing graphics quality, etc.
     *         Not only that but it is also used when updating the frustum which is done each time the viewEntity changes position.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    public void updateChunkPositions(double viewEntityX, double viewEntityZ)
    {
        viewEntityX = FreeCam.INSTANCE.getViewFrustumEntityPosX(viewEntityX);
        viewEntityZ = FreeCam.INSTANCE.getViewFrustumEntityPosZ(viewEntityZ);

        final int baseX = (int) (FastMath.floor(viewEntityX) - 8);
        final int baseZ = (int) (FastMath.floor(viewEntityZ) - 8);
        final int renderDistanceX = countChunksX * 16;

        for (int chunkX = 0; chunkX < countChunksX; ++chunkX) {
            final int adjustedX = getBaseCoordinate(baseX, renderDistanceX, chunkX);

            for (int chunkZ = 0; chunkZ < countChunksZ; ++chunkZ) {
                final int adjustedZ = getBaseCoordinate(baseZ, renderDistanceX, chunkZ);

                for (int chunkY = 0; chunkY < countChunksY; ++chunkY) {
                    final int adjustedY = chunkY << 4;

                    renderChunks[(chunkZ * countChunksY + chunkY) * countChunksX + chunkX].setPosition(adjustedX, adjustedY, adjustedZ);
                }
            }
        }
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators, since render chunk size is always the same.
     *         Improving the performance of this method is beneficial as it reduces lag when loading renderer.
     *         For example, when loading in a world, changing the render distance, changing graphics quality, etc.
     *         Not only that but it is also used when updating the frustum which is done each time the viewEntity changes position.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    private int getBaseCoordinate(int base, int renderDistance, int chunkIndex)
    {
        final int coordinate = chunkIndex << 4;
        int offset = coordinate - base + (renderDistance >> 1);

        if (offset < 0)
            offset -= renderDistance - 1;

        return coordinate - offset / renderDistance * renderDistance;
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Improving the performance of this method is beneficial for FPS stability, as it reduces FPS drops when a lot of blocks are marked for update.
     *         For example, when loading in a world, teleporting, moving really fast or when using mods that changes a lot of blocks all at once.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    public void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately)
    {
        final int chunkMinX = minX >> 4;
        final int chunkMinY = minY >> 4;
        final int chunkMinZ = minZ >> 4;
        final int chunkMaxX = maxX >> 4;
        final int chunkMaxY = maxY >> 4;
        final int chunkMaxZ = maxZ >> 4;

        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            final int normalizedX = (x % countChunksX + countChunksX) % countChunksX;

            for (int y = chunkMinY; y <= chunkMaxY; ++y) {
                final int normalizedY = (y % countChunksY + countChunksY) % countChunksY;

                for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                    final int normalizedZ = (z % countChunksZ + countChunksZ) % countChunksZ;

                    renderChunks[(normalizedZ * countChunksY + normalizedY) * countChunksX + normalizedX].setNeedsUpdate(updateImmediately);
                }
            }
        }
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Making this faster improves the speed of loading render chunks and thus makes it less likely to have so far away chunks never load.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Nullable
    public RenderChunk getRenderChunk(BlockPos blockPos)
    {
        int x = blockPos.getX() >> 4;
        final int y = blockPos.getY() >> 4;
        int z = blockPos.getZ() >> 4;

        if (y >= 0 && y < countChunksY) {
            x = (x % countChunksX + countChunksX) % countChunksX;
            z = (z % countChunksZ + countChunksZ) % countChunksZ;

            return renderChunks[(z * countChunksY + y) * countChunksX + x];
        } else
            return null;
    }

    private void updateVboRegion(RenderChunk p_updateVboRegion_1_)
    {
        BlockPos blockpos = p_updateVboRegion_1_.getPosition();
        int i = blockpos.getX() >> 8 << 8;
        int j = blockpos.getZ() >> 8 << 8;
        ChunkPos chunkpos = new ChunkPos(i, j);
        BlockRenderLayer[] ablockrenderlayer = BlockRenderLayer.values();
        VboRegion[] avboregion = this.mapVboRegions.get(chunkpos);

        if (avboregion == null)
        {
            avboregion = new VboRegion[ablockrenderlayer.length];

            for (int k = 0; k < ablockrenderlayer.length; ++k)
            {
                avboregion[k] = new VboRegion(ablockrenderlayer[k]);
            }

            this.mapVboRegions.put(chunkpos, avboregion);
        }

        for (int l = 0; l < ablockrenderlayer.length; ++l)
        {
            VboRegion vboregion = avboregion[l];

            if (vboregion != null)
            {
                p_updateVboRegion_1_.getVertexBufferByLayer(l).setVboRegion(vboregion);
            }
        }
    }

    public void deleteVboRegions()
    {
        for (ChunkPos chunkpos : this.mapVboRegions.keySet())
        {
            VboRegion[] avboregion = this.mapVboRegions.get(chunkpos);

            for (int i = 0; i < avboregion.length; ++i)
            {
                VboRegion vboregion = avboregion[i];

                if (vboregion != null)
                {
                    vboregion.deleteGlBuffers();
                }

                avboregion[i] = null;
            }
        }

        this.mapVboRegions.clear();
    }
}
