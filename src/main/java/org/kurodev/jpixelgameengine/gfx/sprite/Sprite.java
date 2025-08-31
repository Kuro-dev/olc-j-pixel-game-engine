package org.kurodev.jpixelgameengine.gfx.sprite;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;
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

public class Sprite {
    private static final Logger log = LoggerFactory.getLogger(Sprite.class);
    private static final NativeFunction<MemorySegment> CREATE_SPRITE_PATH = new NativeFunction<>("sprite_createPath", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<MemorySegment> CREATE_SPRITE_WIDTH_HEIGHT = new NativeFunction<>("sprite_createWidthHeight", ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    private static final NativeFunction<Void> DESTROY_SPRITE = new NativeFunction<>("sprite_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Integer> SPRITE_WIDTH = new NativeFunction<>("sprite_width", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> SPRITE_HEIGHT = new NativeFunction<>("sprite_height", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Boolean> SET_PIXEL = new NativeFunction<>("sprite_setPixel", FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, Pixel.LAYOUT));
    private static final NativeFunction<Boolean> SET_PIXEL_BULK = new NativeFunction<>("sprite_bulk_setPixel", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
    private static final NativeFunction<Pixel> GET_PIXEL = new NativeFunction<>("sprite_getPixel", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, IntVector2D.LAYOUT));
    private static final Cleaner SPRITE_CLEANER = Cleaner.create();
    private final Arena arena;
    /**
     * MemoryAddress of this sprite. Should never be needed externally;
     */
    private final MemorySegment spritePtr;
    private final String name;
    private final int width;
    private final int height;

    public Sprite(Path spritePath) {
        this.arena = Arena.ofAuto();
        log.info("Loading sprite {}", spritePath);
        this.name = spritePath.toString();
        spritePtr = CREATE_SPRITE_PATH.invokeExact(Sprite::identity, arena.allocateFrom(spritePath.toAbsolutePath().toString()));
        registerCleaner();
        height = SPRITE_HEIGHT.invoke(spritePtr);
        width = SPRITE_WIDTH.invoke(spritePtr);

    }

    public Sprite(int width, int height, String name) {
        this.arena = Arena.ofAuto();
        spritePtr = CREATE_SPRITE_WIDTH_HEIGHT.invokeExact(Sprite::identity, width, height);
        this.name = name;
        registerCleaner();
        this.width = width;
        this.height = height;
    }

    public Sprite(int width, int height) {
        this(width, height, "unnamed");
    }

    private static MemorySegment identity(MemorySegment seg) {
        return seg;
    }

    private void registerCleaner() {
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

    @Override
    public String toString() {
        return "Sprite{" + name + '}';
    }

    public String getName() {
        return name;
    }

    public Pixel getPixel(Vector2D<Integer> pos) {
        return GET_PIXEL.invokeObj(Pixel::new, spritePtr, pos.toPtr());
    }

    public boolean setPixel(Vector2D<Integer> pos, Pixel pixel) {
        return SET_PIXEL.invoke(spritePtr, pos.getX(), pos.getY(), pixel.toPtr());
    }

    public boolean setPixel(int x, int y, Pixel pixel) {
        return SET_PIXEL.invoke(spritePtr, x, y, pixel.toPtr());
    }

    public void setPixels(Pixel[][] pixels) {
        if (pixels.length != height || pixels[0].length != width) {
            throw new IllegalArgumentException("Pixel array must match sprite dimensions");
        }
        Arena arena = Arena.ofConfined();
        MemorySegment buffer = arena.allocate(ValueLayout.JAVA_INT, (long) width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                buffer.setAtIndex(ValueLayout.JAVA_INT, index, pixels[y][x].getRGBA());
            }
        }
        SET_PIXEL_BULK.invoke(spritePtr, buffer);
        arena.close();
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
}
