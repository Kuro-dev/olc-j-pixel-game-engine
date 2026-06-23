package org.kurodev.jpixelgameengine.gfx.sprite;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.ResourcePack;
import org.kurodev.jpixelgameengine.impl.PixelgameEngineReturnCode;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;
import java.nio.file.Path;

/**
 * Java owner/wrapper for {@code olc::Sprite}, an in-memory 2D pixel image.
 */
public class Sprite {
    private static final Logger log = LoggerFactory.getLogger(Sprite.class);
    private static final Cleaner SPRITE_CLEANER = Cleaner.create();

    private static final NativeFunction<MemorySegment> CREATE_SPRITE = new NativeFunction<>("sprite_create", ValueLayout.ADDRESS);
    private static final NativeFunction<MemorySegment> CREATE_SPRITE_PATH = new NativeFunction<>("sprite_createPath", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<MemorySegment> CREATE_SPRITE_WIDTH_HEIGHT = new NativeFunction<>("sprite_createWidthHeight", ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    private static final NativeFunction<Void> DESTROY_SPRITE = new NativeFunction<>("sprite_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Integer> SPRITE_WIDTH = new NativeFunction<>("sprite_width", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> SPRITE_HEIGHT = new NativeFunction<>("sprite_height", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> LOAD_FROM_FILE = new NativeFunction<>("sprite_loadFromFile", ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Void> SET_SAMPLE_MODE = new NativeFunction<>("sprite_setSampleMode", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
    private static final NativeFunction<Integer> GET_SAMPLE_MODE = new NativeFunction<>("sprite_getSampleMode", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Boolean> SET_PIXEL = new NativeFunction<>("sprite_setPixel", FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, Pixel.LAYOUT));
    private static final NativeFunction<Void> SET_PIXEL_BULK = new NativeFunction<>("sprite_bulk_setPixel", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
    private static final NativeFunction<Pixel> GET_PIXEL = new NativeFunction<>("sprite_getPixel", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, IntVector2D.LAYOUT));
    private static final NativeFunction<Pixel> GET_PIXEL_XY = new NativeFunction<>("sprite_getPixelXY", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
    private static final NativeFunction<Pixel> SAMPLE = new NativeFunction<>("sprite_sample", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT));
    private static final NativeFunction<Pixel> SAMPLE_BL = new NativeFunction<>("sprite_sampleBL", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT));
    private static final NativeFunction<Integer> DATA_LENGTH = new NativeFunction<>("sprite_dataLength", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Pixel> DATA_PIXEL = new NativeFunction<>("sprite_getDataPixel", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
    private static final NativeFunction<MemorySegment> DUPLICATE = new NativeFunction<>("sprite_duplicate", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<MemorySegment> DUPLICATE_REGION = new NativeFunction<>("sprite_duplicateRegion", FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, IntVector2D.LAYOUT, IntVector2D.LAYOUT));
    private static final NativeFunction<Vector2D<Integer>> SIZE = new NativeFunction<>("sprite_size", FunctionDescriptor.of(IntVector2D.LAYOUT, ValueLayout.ADDRESS));
    private static final NativeFunction<Void> SET_SIZE = new NativeFunction<>("sprite_setSize", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
    private static final NativeFunction<SpritePatch> PATCH = new NativeFunction<>("sprite_patch", FunctionDescriptor.of(SpritePatch.LAYOUT, ValueLayout.ADDRESS, IntVector2D.LAYOUT, IntVector2D.LAYOUT));
    private static final NativeFunction<SpritePatch> PATCH_UV = new NativeFunction<>("sprite_patchUv", FunctionDescriptor.of(SpritePatch.LAYOUT, ValueLayout.ADDRESS, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT));

    private final Arena arena;
    private final MemorySegment spritePtr;
    private final String name;
    private final boolean owned;
    private int width;
    private int height;

    /**
     * Creates an empty sprite.
     */
    public Sprite() {
        this(CREATE_SPRITE.invokeExact(segment -> segment), "unnamed", true);
    }

    /**
     * Loads a sprite from an image file.
     *
     * @param spritePath image file path
     */
    public Sprite(Path spritePath) {
        this(CREATE_SPRITE_PATH.invokeExact(Sprite::identity, Arena.ofAuto().allocateFrom(spritePath.toAbsolutePath().toString())),
                spritePath.toString(), true);
        log.info("Loading sprite {}", spritePath);
    }

    /**
     * Creates a sprite and loads it using a resource pack.
     *
     * @param spritePath path inside the pack
     * @param pack       loaded resource pack
     */
    public Sprite(Path spritePath, ResourcePack pack) {
        this();
        loadFromFile(spritePath, pack);
    }

    /**
     * Creates a blank sprite.
     *
     * @param width  sprite width in pixels
     * @param height sprite height in pixels
     * @param name   debugging name used in logs
     */
    public Sprite(int width, int height, String name) {
        this(CREATE_SPRITE_WIDTH_HEIGHT.invokeExact(Sprite::identity, width, height), name, true);
    }

    public Sprite(int width, int height) {
        this(width, height, "unnamed");
    }

    private Sprite(MemorySegment spritePtr, String name, boolean owned) {
        this.arena = Arena.ofAuto();
        this.spritePtr = spritePtr;
        this.name = name;
        this.owned = owned;
        registerCleaner();
        refreshSize();
    }

    public static Sprite wrapNative(MemorySegment spritePtr, String name) {
        return new Sprite(spritePtr, name, false);
    }

    public static Sprite ownNative(MemorySegment spritePtr, String name) {
        return new Sprite(spritePtr, name, true);
    }

    private static MemorySegment identity(MemorySegment seg) {
        return seg;
    }

    private void registerCleaner() {
        if (!owned || MemorySegment.NULL.equals(spritePtr)) {
            return;
        }

        final MemorySegment finalSpritePtr = spritePtr;
        final String finalName = name;
        SPRITE_CLEANER.register(this, () -> {
            log.info("Unloading sprite {}", finalName);
            try {
                DESTROY_SPRITE.invoke(finalSpritePtr);
            } catch (Exception e) {
                log.error("Failed to cleanup sprite {}", finalName, e);
            }
        });
    }

    private void refreshSize() {
        height = SPRITE_HEIGHT.invoke(spritePtr);
        width = SPRITE_WIDTH.invoke(spritePtr);
    }

    /**
     * Loads image data into this sprite.
     *
     * @param path image path
     * @return native load status
     */
    public PixelgameEngineReturnCode loadFromFile(Path path) {
        return loadFromFile(path, null);
    }

    /**
     * Loads image data into this sprite, optionally from a resource pack.
     *
     * @param path image path or resource-pack path
     * @param pack optional resource pack
     * @return native load status
     */
    public PixelgameEngineReturnCode loadFromFile(Path path, ResourcePack pack) {
        MemorySegment packPtr = pack == null ? MemorySegment.NULL : pack.getPtr();
        int code = LOAD_FROM_FILE.invoke(spritePtr, arena.allocateFrom(path.toString()), packPtr);
        refreshSize();
        return PixelgameEngineReturnCode.fromCode(code);
    }

    /**
     * Sets how {@link #sample(float, float)} handles coordinates outside the sprite.
     */
    public void setSampleMode(SpriteSampleMode mode) {
        SET_SAMPLE_MODE.invoke(spritePtr, mode.ordinal());
    }

    public SpriteSampleMode getSampleMode() {
        return SpriteSampleMode.values()[GET_SAMPLE_MODE.invoke(spritePtr)];
    }

    public Pixel getPixel(Vector2D<Integer> pos) {
        return GET_PIXEL.invokeObj(Pixel::new, spritePtr, pos.toPtr());
    }

    public Pixel getPixel(int x, int y) {
        return GET_PIXEL_XY.invokeObj(Pixel::new, spritePtr, x, y);
    }

    public boolean setPixel(Vector2D<Integer> pos, Pixel pixel) {
        return setPixel(pos.getX(), pos.getY(), pixel);
    }

    public boolean setPixel(int x, int y, Pixel pixel) {
        return SET_PIXEL.invoke(spritePtr, x, y, pixel.toPtr());
    }

    /**
     * Replaces all sprite pixels from a two-dimensional array.
     */
    public void setPixels(Pixel[][] pixels) {
        if (pixels.length != height || pixels[0].length != width) {
            throw new IllegalArgumentException("Pixel array must match sprite dimensions");
        }
        try (Arena local = Arena.ofConfined()) {
            MemorySegment buffer = local.allocate(ValueLayout.JAVA_INT, (long) width * height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    buffer.setAtIndex(ValueLayout.JAVA_INT, y * width + x, pixels[y][x].getRGBA());
                }
            }
            SET_PIXEL_BULK.invoke(spritePtr, buffer);
        }
    }

    /**
     * Samples the sprite using normalized coordinates.
     */
    public Pixel sample(float x, float y) {
        return SAMPLE.invokeObj(Pixel::new, spritePtr, x, y);
    }

    /**
     * Samples the sprite using bilinear filtering.
     */
    public Pixel sampleBL(float u, float v) {
        return SAMPLE_BL.invokeObj(Pixel::new, spritePtr, u, v);
    }

    /**
     * Copies native pixel data into a Java array.
     */
    public Pixel[] getData() {
        int length = DATA_LENGTH.invoke(spritePtr);
        Pixel[] pixels = new Pixel[length];
        for (int i = 0; i < length; i++) {
            pixels[i] = DATA_PIXEL.invokeObj(Pixel::new, spritePtr, i);
        }
        return pixels;
    }

    public Sprite duplicate() {
        return ownNative(DUPLICATE.invokeExact(Sprite::identity, spritePtr), name + " copy");
    }

    public Sprite duplicate(Vector2D<Integer> pos, Vector2D<Integer> size) {
        return ownNative(DUPLICATE_REGION.invokeExact(Sprite::identity, spritePtr, pos.toPtr(), size.toPtr()), name + " copy");
    }

    public Vector2D<Integer> size() {
        return SIZE.invokeObj(IntVector2D::new, spritePtr);
    }

    public void setSize(int width, int height) {
        SET_SIZE.invoke(spritePtr, width, height);
        refreshSize();
    }

    public SpritePatch patch(Vector2D<Integer> pos, Vector2D<Integer> size) {
        return PATCH.invokeObj(SpritePatch::new, spritePtr, pos.toPtr(), size.toPtr());
    }

    public SpritePatch patch(Vector2D<Float> bottomLeft, Vector2D<Float> topLeft, Vector2D<Float> topRight, Vector2D<Float> bottomRight) {
        return PATCH_UV.invokeObj(SpritePatch::new, spritePtr, bottomLeft.toPtr(), topLeft.toPtr(), topRight.toPtr(), bottomRight.toPtr());
    }

    public MemorySegment getSpritePtr() {
        return spritePtr;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Sprite{" + name + '}';
    }
}
