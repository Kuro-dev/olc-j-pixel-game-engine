package org.kurodev.jpixelgameengine.gfx.sprite;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kurodev.jpixelgameengine.gfx.OlcReferenceCleaner;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;
import java.nio.file.Path;

@Slf4j
public class Sprite {
    //TODO Check if the Cleaner is actually getting triggered at some point or if the reference is still too hard.
    private static final Cleaner CLEANER = Cleaner.create();
    private static final NativeFunction<MemorySegment> CREATE_SPRITE = new NativeFunction<>("sprite_create", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Void> DESTROY_SPRITE = new NativeFunction<>("sprite_destroy", ValueLayout.ADDRESS);

    private static final NativeFunction<Integer> SPRITE_WIDTH = new NativeFunction<>("sprite_width", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> SPRITE_HEIGHT = new NativeFunction<>("sprite_height", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private final Arena arena;
    /**
     * MemoryAddress of this sprite. Should never be needed
     */
    @Getter
    private final MemorySegment spritePtr;
    @Getter
    private final Path spritePath;

    public Sprite(Path spritePath) {
        log.info("Loading sprite {}", spritePath);
        this.spritePath = spritePath;
        arena = Arena.ofAuto();
        spritePtr = CREATE_SPRITE.invokeExact(memorySegment -> memorySegment, arena.allocateFrom(spritePath.toAbsolutePath().toString()));
        CLEANER.register(this, new OlcReferenceCleaner(() -> {
            log.info("Unloading sprite {}", spritePath);
            DESTROY_SPRITE.invoke(spritePtr);
            arena.close();
        }));
    }

    public int getHeight() {
        return SPRITE_HEIGHT.invoke(spritePtr);
    }

    public int getWidth() {
        return SPRITE_WIDTH.invoke(spritePtr);
    }


}
