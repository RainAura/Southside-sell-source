package dev.diona.southside.util.misc.nvgasset;

import dev.diona.southside.util.misc.FileUtil;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public enum NanoVGAssetHelperImpl {
    INSTANCE;
    public static final int DEFAULT_FLAGS = NanoVG.NVG_IMAGE_REPEATX | NanoVG.NVG_IMAGE_REPEATY | NanoVG.NVG_IMAGE_GENERATE_MIPMAPS;
    private final Map<String, NVGAsset> imageHashMap = new HashMap<>();
    private final Map<String, NVGAsset> svgHashMap = new HashMap<>();

    public boolean loadImage(long vg, String fileName, int flags, Class<?> clazz) {
        if (!imageHashMap.containsKey(fileName)) {
            int[] width = {0};
            int[] height = {0};
            int[] channels = {0};

            ByteBuffer image = FileUtil.resourceToByteBufferNullable(fileName, clazz);
            if (image == null) {
                return false;
            }

            ByteBuffer buffer = STBImage.stbi_load_from_memory(image, width, height, channels, 4);
            if (buffer == null) {
                return false;
            }

            imageHashMap.put(fileName, new NVGAsset(NanoVG.nvgCreateImageRGBA(vg, width[0], height[0], flags, buffer), width[0], height[0]));
            return true;
        }
        return true;
    }

    /**
     * Loads an assets from resources.
     *
     * @param vg       The NanoVG context.
     * @param fileName The name of the file to load.
     * @return Whether the asset was loaded successfully.
     */
    public boolean loadImage(long vg, String fileName, Class<?> clazz) {
        return loadImage(vg, fileName, DEFAULT_FLAGS, clazz);
    }

    public int getImage(String fileName) {
        return imageHashMap.get(fileName).getImage();
    }

}
