package org.kurodev.jpixelgameengine.gfx.decal;

import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;

/**
 * Java owner/wrapper for {@code olc::Decal}, a GPU-resident texture created from a sprite.
 * Decals should normally be created inside {@link PixelGameEngine#onUserCreate()} or
 * {@link PixelGameEngine#onUserUpdate(float)}.
 */
public class Decal {
    private static final Logger log = LoggerFactory.getLogger(Decal.class);
    private static final Cleaner CLEANER = Cleaner.create();

    private static final NativeFunction<MemorySegment> CREATE_DECAL = new NativeFunction<>("decal_create", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<MemorySegment> CREATE_DECAL_OPTIONS = new NativeFunction<>("decal_createOptions", ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN);
    private static final NativeFunction<MemorySegment> CREATE_EXISTING = new NativeFunction<>("decal_createExisting", ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Void> DESTROY_DECAL = new NativeFunction<>("decal_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Integer> ID = new NativeFunction<>("decal_id", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> WIDTH = new NativeFunction<>("decal_width", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> HEIGHT = new NativeFunction<>("decal_height", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final NativeFunction<Vector2D<Float>> UV_SCALE = new NativeFunction<>("decal_vUVScale", FunctionDescriptor.of(FloatVector2D.LAYOUT, ValueLayout.ADDRESS));
    private static final NativeFunction<Void> DECAL_UPDATE = new NativeFunction<>("decal_Update", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Void> DECAL_UPDATE_SPRITE = new NativeFunction<>("decal_UpdateSprite", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<DecalPatch> PATCH = new NativeFunction<>("decal_patch", FunctionDescriptor.of(DecalPatch.LAYOUT, ValueLayout.ADDRESS, IntVector2D.LAYOUT, IntVector2D.LAYOUT));
    private static final NativeFunction<DecalPatch> PATCH_UV = new NativeFunction<>("decal_patchUv", FunctionDescriptor.of(DecalPatch.LAYOUT, ValueLayout.ADDRESS, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT));

    private final MemorySegment ptr;
    private final Sprite sprite;
    private final boolean owned;

    public Decal(Sprite sprite) {
        this(CREATE_DECAL.invokeExact(segment -> segment, sprite.getSpritePtr()), sprite, true);
    }

    public Decal(Sprite sprite, boolean filter, boolean clamp) {
        this(CREATE_DECAL_OPTIONS.invokeExact(segment -> segment, sprite.getSpritePtr(), filter, clamp), sprite, true);
    }

    public Decal(int existingTextureResource, Sprite sprite) {
        this(CREATE_EXISTING.invokeExact(segment -> segment, existingTextureResource, sprite.getSpritePtr()), sprite, true);
    }

    private Decal(MemorySegment ptr, Sprite sprite, boolean owned) {
        this.ptr = ptr;
        this.sprite = sprite;
        this.owned = owned;
        registerCleaner();
    }

    public static Decal wrapNative(MemorySegment ptr, Sprite sprite) {
        return new Decal(ptr, sprite, false);
    }

    public static Decal ownNative(MemorySegment ptr, Sprite sprite) {
        return new Decal(ptr, sprite, true);
    }

    private void registerCleaner() {
        if (!owned || MemorySegment.NULL.equals(ptr)) {
            return;
        }

        final MemorySegment finalDecalPtr = ptr;
        final Sprite finalSprite = sprite;
        CLEANER.register(this, () -> {
            log.info("Unloading Decal {}", finalSprite.getName());
            try {
                DESTROY_DECAL.invoke(finalDecalPtr);
            } catch (Exception e) {
                log.error("Failed to cleanup decal {}", finalSprite.getName(), e);
            }
        });
    }

    public MemorySegment getPtr() {
        return ptr;
    }

    /**
     * Uploads the current sprite pixels to the GPU texture.
     */
    public void update() {
        DECAL_UPDATE.invoke(ptr);
    }

    /**
     * Reads the GPU texture back into the sprite.
     */
    public void updateSprite() {
        DECAL_UPDATE_SPRITE.invoke(ptr);
    }

    public DecalPatch patch(Vector2D<Integer> pos, Vector2D<Integer> size) {
        return PATCH.invokeObj(DecalPatch::new, ptr, pos.toPtr(), size.toPtr());
    }

    public DecalPatch patch(Vector2D<Float> bottomLeft, Vector2D<Float> topLeft, Vector2D<Float> topRight, Vector2D<Float> bottomRight) {
        return PATCH_UV.invokeObj(DecalPatch::new, ptr, bottomLeft.toPtr(), topLeft.toPtr(), topRight.toPtr(), bottomRight.toPtr());
    }

    public int getId() {
        return ID.invoke(ptr);
    }

    public int getWidth() {
        return WIDTH.invoke(ptr);
    }

    public int getHeight() {
        return HEIGHT.invoke(ptr);
    }

    public Vector2D<Float> vUVScale() {
        return UV_SCALE.invokeObj(FloatVector2D::new, ptr);
    }

    public Sprite getSprite() {
        return sprite;
    }
}
