package org.kurodev.jpixelgameengine.gfx.decal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kurodev.jpixelgameengine.gfx.OlcReferenceCleaner;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;

@Slf4j
@Getter
public class Decal {
    //TODO Check if the Cleaner is actually getting triggered at some point or if the reference is still too hard.
    private static final Cleaner CLEANER = Cleaner.create();
    private static final NativeFunction<MemorySegment> CREATE_DECAL = new NativeFunction<>("decal_create", ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Void> DESTROY_DECAL = new NativeFunction<>("decal_destroy", ValueLayout.ADDRESS);
    private static final NativeFunction<Vector2D<Float>> UV_SCALE = new NativeFunction<>("decal_vUVScale", FunctionDescriptor.of(ValueLayout.ADDRESS, FloatVector2D.LAYOUT));
    private final MemorySegment ptr;
    private final Sprite sprite;

    public Decal(Sprite sprite) {
        ptr = CREATE_DECAL.invokeExact(m -> m, sprite.getSpritePtr());
        this.sprite = sprite;
        CLEANER.register(this, new OlcReferenceCleaner(() -> {
            log.info("Unloading Decal for {}", sprite.getSpritePath());
            DESTROY_DECAL.invoke(ptr);
        }));
    }


    public Vector2D<Float> vUVScale() {
        return UV_SCALE.invokeObj(FloatVector2D::new, ptr);
    }

}
