package org.kurodev.jpixelgameengine.gfx.decal;

import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;

/**
 * Decals Should not be created outside of {@link PixelGameEngine#onUserCreate()} or {@link PixelGameEngine#onUserUpdate(float)}
 */
public class Decal {
    private static final Logger log = LoggerFactory.getLogger(Decal.class);
    private static final Cleaner CLEANER = Cleaner.create();
    private static final NativeFunction<MemorySegment> CREATE_DECAL = new NativeFunction<>("decal_create", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Void> DESTROY_DECAL = new NativeFunction<>("decal_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Vector2D<Float>> UV_SCALE = new NativeFunction<>("decal_vUVScale", FunctionDescriptor.of(ValueLayout.ADDRESS, FloatVector2D.LAYOUT));
    private static final NativeFunction<Void> DECAL_UPDATE = new NativeFunction<>("decal_Update", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Void> DECAL_UPDATE_SPRITE = new NativeFunction<>("decal_UpdateSprite", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    private final MemorySegment ptr;
    private final Sprite sprite;

    public Decal(Sprite sprite) {
        ptr = CREATE_DECAL.invokeExact(m -> m, sprite.getSpritePtr());
        this.sprite = sprite;
        registerCleaner();
    }

    private void registerCleaner() {
        final MemorySegment finalSpritePtr = ptr;
        final Sprite finalSprite = sprite;
        CLEANER.register(this, () -> {
            log.info("Unloading Decal {}", finalSprite.getName());
            try {
                DESTROY_DECAL.invoke(finalSpritePtr);
            } catch (Exception e) {
                log.error("Failed to cleanup sprite {}", finalSprite.getName(), e);
            }
        });
    }

    public MemorySegment getPtr() {
        return ptr;
    }

    /**
     * Update reads the texture from the provided sprite into the GPU.
     * This will update the Decal to match the Sprite
     */
    public void update() {
        DECAL_UPDATE.invoke(ptr);
    }

    /**
     * UpdateSprite Writes the Decal texture into the provided sprite.
     * This will update the sprite to match the decal
     */
    public void updateSprite() {
        DECAL_UPDATE_SPRITE.invoke(ptr);
    }

    public Vector2D<Float> vUVScale() {
        return UV_SCALE.invokeObj(FloatVector2D::new, ptr);
    }

    public Sprite getSprite() {
        return sprite;
    }
}
