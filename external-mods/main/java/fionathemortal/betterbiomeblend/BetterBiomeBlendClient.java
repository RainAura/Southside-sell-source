package fionathemortal.betterbiomeblend;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;


public final class BetterBiomeBlendClient
{
    public static final int BIOME_BLEND_RADIUS_MAX = 14;
    public static final int BIOME_BLEND_RADIUS_MIN = 0;

    public static void onChunkLoadedEvent(final Chunk chunk, final World world)
    {

        ColorChunkCache cache = BiomeColor.getColorChunkCacheForWorld(world);

        if (cache != null)
        {
            cache.invalidateNeighbourhood(chunk.x, chunk.z);
        }
    }
}
