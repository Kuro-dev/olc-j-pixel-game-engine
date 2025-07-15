package org.kurodev.jpixelgameengine.pos;

import lombok.EqualsAndHashCode;
import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;
import org.kurodev.jpixelgameengine.impl.PointerClass;

@NativeCallCandidate
@EqualsAndHashCode(callSuper = false)
public abstract class Vector2D<T extends Number> extends PointerClass {

    public static Vector2D<Integer> ofInt(int n) {
        return ofInt(n, n);
    }

    public static Vector2D<Integer> ofInt(int x, int y) {
        return new IntVector2D(x, y);
    }

    public static Vector2D<Long> ofLong(long n) {
        return ofLong(n, n);
    }

    public static Vector2D<Long> ofLong(long x, long y) {
        return new LongVector2D(x, y);
    }

    public static Vector2D<Float> ofFloat(float n) {
        return ofFloat(n, n);
    }

    public static Vector2D<Float> ofFloat(float x, float y) {
        return new FloatVector2D(x, y);
    }

    public static Vector2D<Double> ofDouble(double n) {
        return ofDouble(n, n);
    }

    public static Vector2D<Double> ofDouble(double x, double y) {
        return new DoubleVector2D(x, y);
    }

    public abstract T getX();

    public abstract T getY();

    public abstract T area();

    public abstract T lengthSquared();

    public abstract Vector2D<T> add(Vector2D<T> v);

    public abstract Vector2D<T> subtract(Vector2D<T> v);

    public abstract Vector2D<T> multiply(T m);

    public abstract T length();

    public abstract T distance(Vector2D<T> v);

    public abstract T distanceSquared(Vector2D<T> v);

    public abstract T dot(Vector2D<T> v);

    public abstract Vector2D<Float> normalize();

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }


}
