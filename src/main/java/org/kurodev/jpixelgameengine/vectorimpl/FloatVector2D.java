package org.kurodev.jpixelgameengine.vectorimpl;

import org.kurodev.jpixelgameengine.Vector2D;

public class FloatVector2D extends Vector2D<Float> {
    private final float x;
    private final float y;

    public FloatVector2D(float x) {
        this.x = x;
        this.y = x;
    }

    public FloatVector2D(float x, float y) {
        this.x = x;
        this.y = y;
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
}
