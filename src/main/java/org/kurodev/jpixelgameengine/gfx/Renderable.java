package org.kurodev.jpixelgameengine.gfx;

import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.PixelgameEngineReturnCode;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;
import java.nio.file.Path;

/**
 * Convenience wrapper for {@code olc::Renderable}, which owns a sprite and matching decal.
 */
public final class Renderable implements AutoCloseable {
    private static final Cleaner CLEANER = Cleaner.create();
    private static final NativeFunction<MemorySegment> CREATE = new NativeFunction<>("renderable_create", ValueLayout.ADDRESS);
    private static final NativeFunction<Void> DESTROY = new NativeFunction<>("renderable_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Integer> LOAD = new NativeFunction<>("renderable_load", ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN);
    private static final NativeFunction<Void> CREATE_TARGET = new NativeFunction<>("renderable_createTarget", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN));
    private static final NativeFunction<MemorySegment> DECAL = new NativeFunction<>("renderable_decal", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<MemorySegment> SPRITE = new NativeFunction<>("renderable_sprite", ValueLayout.ADDRESS, ValueLayout.ADDRESS);

    private final Arena arena = Arena.ofAuto();
    private final MemorySegment ptr;
    private final Cleaner.Cleanable cleanable;
    private Sprite sprite;
    private Decal decal;

    public Renderable() {
        ptr = CREATE.invokeExact(segment -> segment);
        cleanable = CLEANER.register(this, () -> DESTROY.invoke(ptr));
    }

    /**
     * Loads an image into a sprite and creates its decal.
     */
    public PixelgameEngineReturnCode load(Path file) {
        return load(file, null, false, true);
    }

    /**
     * Loads an image into a sprite and creates its decal.
     *
     * @param file   image path
     * @param pack   optional resource pack
     * @param filter true to enable texture filtering
     * @param clamp  true to clamp texture coordinates
     * @return native load status
     */
    public PixelgameEngineReturnCode load(Path file, ResourcePack pack, boolean filter, boolean clamp) {
        MemorySegment packPtr = pack == null ? MemorySegment.NULL : pack.getPtr();
        int code = LOAD.invoke(ptr, arena.allocateFrom(file.toString()), packPtr, filter, clamp);
        refreshViews();
        return PixelgameEngineReturnCode.fromCode(code);
    }

    /**
     * Creates a blank render target.
     */
    public void create(int width, int height) {
        create(width, height, false, true);
    }

    /**
     * Creates a blank render target.
     */
    public void create(int width, int height, boolean filter, boolean clamp) {
        CREATE_TARGET.invoke(ptr, width, height, filter, clamp);
        refreshViews();
    }

    public Decal decal() {
        if (decal == null) {
            refreshViews();
        }
        return decal;
    }

    public Sprite sprite() {
        if (sprite == null) {
            refreshViews();
        }
        return sprite;
    }

    private void refreshViews() {
        MemorySegment spritePtr = SPRITE.invokeExact(segment -> segment, ptr);
        MemorySegment decalPtr = DECAL.invokeExact(segment -> segment, ptr);
        sprite = MemorySegment.NULL.equals(spritePtr) ? null : Sprite.wrapNative(spritePtr, "renderable sprite");
        decal = MemorySegment.NULL.equals(decalPtr) || sprite == null ? null : Decal.wrapNative(decalPtr, sprite);
    }

    public MemorySegment getPtr() {
        return ptr;
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}
