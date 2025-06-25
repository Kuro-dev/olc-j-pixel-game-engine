package org.kurodev.jpixelgameengine;

import org.kurodev.jpixelgameengine.vectorimpl.DoubleVector2D;
import org.kurodev.jpixelgameengine.vectorimpl.FloatVector2D;
import org.kurodev.jpixelgameengine.vectorimpl.IntegerVector2D;
import org.kurodev.jpixelgameengine.vectorimpl.LongVector2D;

import java.util.Objects;

@NativeCallCandidate
public abstract class Vector2D<T extends Number> {

    static Vector2D<Integer> ofInt(int x, int y) {
        return new IntegerVector2D(x, y);
    }

    static Vector2D<Long> ofLong(long x, long y) {
        return new LongVector2D(x, y);
    }

    static Vector2D<Float> ofFloat(float x, float y) {
        return new FloatVector2D(x, y);
    }

    static Vector2D<Double> ofDouble(double x, double y) {
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
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public boolean equals(Object obj) {
       if (obj == this) return true;
       if (obj == null || obj.getClass() != this.getClass()) return false;
       Vector2D other = (Vector2D) obj;
       return Objects.equals(getX(), other.getX()) && Objects.equals(getY(), other.getY());
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}
