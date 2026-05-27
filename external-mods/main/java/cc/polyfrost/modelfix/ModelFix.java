package cc.polyfrost.modelfix;

import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjglx.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModelFix {

    public static List<BlockPart> pixel(int layer, String key, TextureAtlasSprite sprite) {
        List<BlockPart> parts = new ArrayList<>();
        int width = sprite.getIconWidth();
        int height = sprite.getIconHeight();
        float xFactor = width / 16.0F;
        float yFactor = height / 16.0F;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (!isPixelAlwaysTransparent(sprite, x, y)) {
                    Map<EnumFacing, BlockPartFace> faces = new EnumMap<>(EnumFacing.class);
                    BlockPartFace face = new BlockPartFace(null, layer, key, new BlockFaceUV(new float[] { x / xFactor, y / yFactor, (x + 1) / xFactor, (y + 1) / yFactor }, 0));
                    BlockPartFace flippedFace = new BlockPartFace(null, layer, key, new BlockFaceUV(new float[] { (x + 1) / xFactor, y / yFactor, x / xFactor, (y + 1) / yFactor }, 0));

                    faces.put(EnumFacing.SOUTH, face);
                    faces.put(EnumFacing.NORTH, flippedFace);
                    for (PixelDirection pixelDirection : PixelDirection.VALUES) {
                        if (doesPixelHaveEdge(sprite, x, y, pixelDirection)) {
                            faces.put(pixelDirection.getFacing(), pixelDirection.isVertical() ? face : flippedFace);
                        }
                    }

                    parts.add(new BlockPart(new Vector3f(x / xFactor, (height - (y + 1)) / yFactor, 7.5F), new Vector3f((x + 1) / xFactor, (height - y) / yFactor, 8.5F), faces, null, true));
                }
            }
        }
        return parts;
    }

    public static boolean isPixelOutsideSprite(TextureAtlasSprite sprite, int x, int y) {
        return x < 0 || y < 0 || x >= sprite.getIconWidth() || y >= sprite.getIconHeight();
    }

    public static boolean isPixelTransparent(TextureAtlasSprite sprite, int frame, int x, int y) {
        return isPixelOutsideSprite(sprite, x, y) || (sprite.getFrameTextureData(frame)[0][y * sprite.getIconWidth() + x] >> 24 & 0xFF) == 0;
    }

    public static boolean isPixelAlwaysTransparent(TextureAtlasSprite sprite, int x, int y) {
        for (int frame = 0; frame < sprite.getFrameCount(); ++frame) {
            if (!isPixelTransparent(sprite, frame, x, y)) {
                return false;
            }
        }
        return true;
    }

    public static boolean doesPixelHaveEdge(TextureAtlasSprite sprite, int x, int y, PixelDirection direction) {
        int x1 = x + direction.getOffsetX();
        int y1 = y + direction.getOffsetY();
        if (isPixelOutsideSprite(sprite, x1, y1)) {
            return true;
        }
        for (int frame = 0; frame < sprite.getFrameCount(); ++frame) {
            if (!isPixelTransparent(sprite, frame, x, y) && isPixelTransparent(sprite, frame, x1, y1)) {
                return true;
            }
        }
        return false;
    }

    public enum PixelDirection {
        LEFT(EnumFacing.WEST, -1, 0),
        RIGHT(EnumFacing.EAST, 1, 0),
        UP(EnumFacing.UP, 0, -1),
        DOWN(EnumFacing.DOWN, 0, 1);

        public static final PixelDirection[] VALUES = values();

        private final EnumFacing facing;
        private final int offsetX;
        private final int offsetY;

        PixelDirection(EnumFacing facing, int offsetX, int offsetY) {
            this.facing = facing;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public boolean isVertical() {
            return this == DOWN || this == UP;
        }
    }
}