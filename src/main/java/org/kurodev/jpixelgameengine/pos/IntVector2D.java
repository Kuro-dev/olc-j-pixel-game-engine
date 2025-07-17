package org.kurodev.jpixelgameengine.pos;

import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

@NativeCallCandidate
public class IntVector2D extends Vector2D<Integer> {
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("x"),
            ValueLayout.JAVA_INT.withName("y")
    );
    private final int x;
    private final int y;

    public IntVector2D(MemorySegment segment) {
        this.x = segment.get(ValueLayout.JAVA_INT, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")));
        this.y = segment.get(ValueLayout.JAVA_INT, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")));
    }

    public IntVector2D(int x) {
        this.x = x;
        this.y = x;
    }

    public IntVector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntVector2D that = (IntVector2D) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Integer getX() {
        return x;
    }

    @Override
    public Integer getY() {
        return y;
    }

    @Override
    public Integer area() {
        return x * y;
    }

    @Override
    public Integer lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public Vector2D<Integer> add(Vector2D<Integer> v) {
        return new IntVector2D(x + v.getX(), y + v.getY());
    }

    @Override
    public Vector2D<Integer> subtract(Vector2D<Integer> v) {
        return new IntVector2D(x - v.getX(), y - v.getY());
    }

    @Override
    public Vector2D<Integer> multiply(Integer m) {
        return new IntVector2D(x * m, y * m);
    }

    @Override
    public Integer length() {
        return (int) Math.sqrt(x * x + y * y);
    }

    @Override
    public Integer distance(Vector2D<Integer> v) {
        return this.subtract(v).length();
    }

    @Override
    public Integer distanceSquared(Vector2D<Integer> v) {
        return this.subtract(v).lengthSquared();
    }

    @Override
    public Integer dot(Vector2D<Integer> v) {
        return this.x * v.getX() + this.y * v.getY();
    }

    @Override
    public Vector2D<Float> normalize() {
        float r = (float) 1 / length();
        return new FloatVector2D(x * r, y * r);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    @Override
    protected MemorySegment toPtr(MemorySegment seg) {
        seg.set(ValueLayout.JAVA_INT,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")),
                x);
        seg.set(ValueLayout.JAVA_INT,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")),
                y);
        return seg;
    }

}
