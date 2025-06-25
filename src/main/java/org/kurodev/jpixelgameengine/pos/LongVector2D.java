package org.kurodev.jpixelgameengine.pos;

public class LongVector2D extends Vector2D<Long> {
    private final long x;
    private final long y;

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
    public Vector2D<Float> normalize() {
        long r = 1 / length();
        return new FloatVector2D(x * r, y * r);
    }
}
