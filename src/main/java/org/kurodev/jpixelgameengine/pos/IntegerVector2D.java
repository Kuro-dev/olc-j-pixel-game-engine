package org.kurodev.jpixelgameengine.pos;

import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;

@NativeCallCandidate
public class IntegerVector2D extends Vector2D<Integer> {
    private final int x;
    private final int y;

    public IntegerVector2D(int x) {
        this.x = x;
        this.y = x;
    }

    public IntegerVector2D(int x, int y) {
        this.x = x;
        this.y = y;
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
        return new IntegerVector2D(x + v.getX(), y + v.getY());
    }

    @Override
    public Vector2D<Integer> subtract(Vector2D<Integer> v) {
        return new IntegerVector2D(x - v.getX(), y - v.getY());
    }

    @Override
    public Vector2D<Integer> multiply(Integer m) {
        return new IntegerVector2D(x * m, y * m);
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

}
