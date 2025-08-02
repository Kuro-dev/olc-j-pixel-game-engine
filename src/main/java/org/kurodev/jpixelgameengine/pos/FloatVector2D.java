package org.kurodev.jpixelgameengine.pos;

import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

@NativeCallCandidate
public class FloatVector2D extends Vector2D<Float> {
    public static final FloatVector2D ZERO = new FloatVector2D(0.0f, 0.0f);
    public static final FloatVector2D ONE = new FloatVector2D(1.0f, 1.0f);
    public static final FloatVector2D TWO = new FloatVector2D(2.0f, 2.0f);

    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_FLOAT.withName("x"),
            ValueLayout.JAVA_FLOAT.withName("y")
    );

    private final float x;
    private final float y;

    // Constructor from a MemorySegment
    public FloatVector2D(MemorySegment segment) {
        this.x = segment.get(ValueLayout.JAVA_FLOAT, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")));
        this.y = segment.get(ValueLayout.JAVA_FLOAT, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")));
    }

    public FloatVector2D(float x) {
        this.x = x;
        this.y = x;
    }

    public FloatVector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FloatVector2D that = (FloatVector2D) o;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Float getX() {
        return x;
    }

    @Override
    public Float getY() {
        return y;
    }

    @Override
    public Float area() {
        return x * y;
    }

    @Override
    public Float lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public Vector2D<Float> add(Vector2D<Float> v) {
        return new FloatVector2D(x + v.getX(), y + v.getY());
    }

    @Override
    public Vector2D<Float> subtract(Vector2D<Float> v) {
        return new FloatVector2D(x - v.getX(), y - v.getY());
    }

    @Override
    public Vector2D<Float> multiply(Float m) {
        return new FloatVector2D(x * m, y * m);
    }

    @Override
    public Float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public Float distance(Vector2D<Float> v) {
        return this.subtract(v).length();
    }

    @Override
    public Float distanceSquared(Vector2D<Float> v) {
        return this.subtract(v).lengthSquared();
    }

    @Override
    public Float dot(Vector2D<Float> v) {
        return this.x * v.getX() + this.y * v.getY();
    }

    @Override
    public Vector2D<Float> normalize() {
        float r = 1 / length();
        return new FloatVector2D(x * r, y * r);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    @Override
    protected MemorySegment toPtr(MemorySegment seg) {
        seg.set(ValueLayout.JAVA_FLOAT,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")),
                x);
        seg.set(ValueLayout.JAVA_FLOAT,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")),
                y);
        return seg;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
