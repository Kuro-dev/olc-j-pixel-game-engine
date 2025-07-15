package org.kurodev.jpixelgameengine.pos;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;

public class DoubleVector2D extends Vector2D<Double> {
    private final double x;
    private final double y;

    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_DOUBLE.withName("x"),
            ValueLayout.JAVA_DOUBLE.withName("y")
    );

    // Constructor from a MemorySegment
    public DoubleVector2D(MemorySegment segment) {
        this.x = segment.get(ValueLayout.JAVA_DOUBLE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")));
        this.y = segment.get(ValueLayout.JAVA_DOUBLE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")));
    }

    public DoubleVector2D(double x) {
        this.x = x;
        this.y = x;
    }

    public DoubleVector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Double getX() {
        return x;
    }

    @Override
    public Double getY() {
        return y;
    }

    @Override
    public Double area() {
        return x * y;
    }

    @Override
    public Double lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public Vector2D<Double> add(Vector2D<Double> v) {
        return new DoubleVector2D(x + v.getX(), y + v.getY());
    }

    @Override
    public Vector2D<Double> subtract(Vector2D<Double> v) {
        return new DoubleVector2D(x - v.getX(), y - v.getY());
    }

    @Override
    public Vector2D<Double> multiply(Double m) {
        return new DoubleVector2D(x * m, y * m);
    }

    @Override
    public Double length() {
        return (double) Math.sqrt(x * x + y * y);
    }

    @Override
    public Double distance(Vector2D<Double> v) {
        return this.subtract(v).length();
    }

    @Override
    public Double distanceSquared(Vector2D<Double> v) {
        return this.subtract(v).lengthSquared();
    }

    @Override
    public Double dot(Vector2D<Double> v) {
        return this.x * v.getX() + this.y * v.getY();
    }

    @Override
    public Vector2D<Float> normalize() {
        double r = 1 / length();
        return new FloatVector2D((float) (x * r), (float) (y * r));
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    @Override
    protected MemorySegment toPtr(MemorySegment seg) {
        seg.set(ValueLayout.JAVA_DOUBLE,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("x")),
                x);
        seg.set(ValueLayout.JAVA_DOUBLE,
                LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("y")),
                y);
        return seg;
    }
}
