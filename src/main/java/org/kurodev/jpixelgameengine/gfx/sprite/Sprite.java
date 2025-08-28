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
    private static final NativeFunction<Boolean> SET_PIXEL = new NativeFunction<>("sprite_setPixel", FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, IntVector2D.LAYOUT, Pixel.LAYOUT));
    private static final NativeFunction<Pixel> GET_PIXEL = new NativeFunction<>("sprite_getPixel", FunctionDescriptor.of(Pixel.LAYOUT, ValueLayout.ADDRESS, IntVector2D.LAYOUT));
    private static final Cleaner SPRITE_CLEANER = Cleaner.create();
    private final Arena arena;
    /**
     * MemoryAddress of this sprite. Should never be needed externally;
     */
    private final MemorySegment spritePtr;
    private final String name;

    public Sprite(Path spritePath) {
        this.arena = Arena.ofAuto();
        log.info("Loading sprite {}", spritePath);
        this.name = spritePath.toString();
        spritePtr = CREATE_SPRITE_PATH.invokeExact(Sprite::identity, arena.allocateFrom(spritePath.toAbsolutePath().toString()));
        registerCleaner();
    }

    public Sprite(int width, int height, String name) {
        this.arena = Arena.ofAuto();
        spritePtr = CREATE_SPRITE_WIDTH_HEIGHT.invokeExact(Sprite::identity, width, height);
        this.name = name;
        registerCleaner();
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
        return SET_PIXEL.invoke(spritePtr, pos.toPtr(), pixel.toPtr());
    }

    public MemorySegment getSpritePtr() {
        return spritePtr;
    }

    public int getHeight() {
        return SPRITE_HEIGHT.invoke(spritePtr);
    }

    public int getWidth() {
        return SPRITE_WIDTH.invoke(spritePtr);
    }
}
