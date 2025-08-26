package org.kurodev.jpixelgameengine.pos;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

public class LongVector2D extends Vector2D<Long> {
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_LONG.withName("x"),
            ValueLayout.JAVA_LONG.withName("y")
    );
    private final long x;
    private final long y;

    // Constructor from a MemorySegment
    public LongVector2D(MemorySegment segment) {
        this.x = segment.get(ValueLayout.JAVA_LONG, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")));
        this.y = segment.get(ValueLayout.JAVA_LONG, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")));
    }

    public LongVector2D(long x) {
        this.x = x;
        this.y = x;
    }

    public LongVector2D(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Long getX() {
        return x;
    }

    @Override
    public Long getY() {
        return y;
    }

    @Override
    public Long area() {
        return x * y;
    }

    @Override
    public Long lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public Vector2D<Long> add(Vector2D<Long> v) {
        return new LongVector2D(x + v.getX(), y + v.getY());
    }

    @Override
    public Vector2D<Long> subtract(Vector2D<Long> v) {
        return new LongVector2D(x - v.getX(), y - v.getY());
    }

    @Override
    public Vector2D<Long> multiply(Long m) {
        return new LongVector2D(x * m, y * m);
    }

    @Override
    public Long length() {
        return (long) Math.sqrt(x * x + y * y);
    }

    @Override
    public Long distance(Vector2D<Long> v) {
        return this.subtract(v).length();
    }

    @Override
    public Long distanceSquared(Vector2D<Long> v) {
        return this.subtract(v).lengthSquared();
    }

    @Override
    public Long dot(Vector2D<Long> v) {
        return this.x * v.getX() + this.y * v.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LongVector2D that = (LongVector2D) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Vector2D<Float> normalize() {
        long r = 1 / length();
        return new FloatVector2D(x * r, y * r);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    @Override
    protected MemorySegment toPtr(MemorySegment seg) {
        seg.set(ValueLayout.JAVA_LONG,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")),
                x);
        seg.set(ValueLayout.JAVA_LONG,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")),
                y);
        return seg;
    }

    @Override
    public Vector2D<Long> toLongVector() {
        return this;
    }
}
