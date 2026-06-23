package org.kurodev.jpixelgameengine.gfx.sprite;

import org.kurodev.jpixelgameengine.impl.PointerClass;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

/**
 * Describes a sprite and four normalized texture coordinates used for patch drawing.
 */
public final class SpritePatch extends PointerClass {
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.ADDRESS.withName("sprite"),
            MemoryLayout.sequenceLayout(4, FloatVector2D.LAYOUT).withName("coords")
    );

    private static final long SPRITE_OFFSET = LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("sprite"));
    private static final long COORDS_OFFSET = LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("coords"));
    private static final long COORD_SIZE = FloatVector2D.LAYOUT.byteSize();

    private final MemorySegment spritePtr;
    private final Vector2D<Float>[] coordinates;

    @SuppressWarnings("unchecked")
    public SpritePatch(MemorySegment segment) {
        this.spritePtr = segment.get(ValueLayout.ADDRESS, SPRITE_OFFSET);
        this.coordinates = new Vector2D[4];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = new FloatVector2D(segment.asSlice(COORDS_OFFSET + i * COORD_SIZE, COORD_SIZE));
        }
    }

    public SpritePatch(Sprite sprite, Vector2D<Float>[] coordinates) {
        if (coordinates.length != 4) {
            throw new IllegalArgumentException("SpritePatch requires exactly four coordinates");
        }
        this.spritePtr = sprite.getSpritePtr();
        this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
    }

    public MemorySegment getSpritePtr() {
        return spritePtr;
    }

    public Vector2D<Float>[] getCoordinates() {
        return Arrays.copyOf(coordinates, coordinates.length);
    }

    @Override
    protected MemorySegment toPtr(MemorySegment ptr) {
        ptr.set(ValueLayout.ADDRESS, SPRITE_OFFSET, spritePtr);
        for (int i = 0; i < coordinates.length; i++) {
            ptr.asSlice(COORDS_OFFSET + i * COORD_SIZE, COORD_SIZE).copyFrom(coordinates[i].toPtr());
        }
        return ptr;
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }
}
