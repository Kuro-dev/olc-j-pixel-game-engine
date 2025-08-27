package org.kurodev.jpixelgameengine.gfx;

import org.kurodev.jpixelgameengine.impl.PointerClass;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

public class Pixel extends PointerClass {
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_BYTE.withName("r"), ValueLayout.JAVA_BYTE.withName("g"), ValueLayout.JAVA_BYTE.withName("b"), ValueLayout.JAVA_BYTE.withName("a"));

    public static final Pixel BLACK = new Pixel(0, 0, 0);
    public static final Pixel WHITE = new Pixel(255, 255, 255);
    public static final Pixel RED = new Pixel(255, 0, 0);
    public static final Pixel GREEN = new Pixel(0, 255, 0);
    public static final Pixel BLUE = new Pixel(0, 0, 255);
    public static final Pixel TRANSPARENT = new Pixel(0, 0, 0, 0);
    public static final Pixel LIGHT_GRAY = new Pixel(192, 192, 192);
    public static final Pixel GRAY = new Pixel(128, 128, 128);
    public static final Pixel DARK_GRAY = new Pixel(64, 64, 64);
    public static final Pixel PINK = new Pixel(255, 175, 175);
    public static final Pixel ORANGE = new Pixel(255, 200, 0);
    public static final Pixel YELLOW = new Pixel(255, 255, 0);
    public static final Pixel MAGENTA = new Pixel(255, 0, 255);
    public static final Pixel CYAN = new Pixel(0, 255, 255);

    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public Pixel(MemorySegment segment) {
        this.r = Byte.toUnsignedInt(segment.get(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("r"))));
        this.g = Byte.toUnsignedInt(segment.get(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("g"))));
        this.b = Byte.toUnsignedInt(segment.get(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("b"))));
        this.a = Byte.toUnsignedInt(segment.get(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("a"))));
    }

    public Pixel(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 0xFF;
    }

    public Pixel(int rgba) {
        r = (rgba & 0xFF_00_00_00);
        g = (rgba & 0x00_FF_00_00);
        b = (rgba & 0x00_00_FF_00);
        a = (rgba & 0x00_00_00_FF);
    }

    public int getRGBA() {
        return r | (g << 8) | (b << 16) | (a << 24);
    }

    public byte getR() {
        return (byte) (r & 0xFF);
    }

    public int getG() {
        return (int) (g & 0xFF);
    }

    public int getB() {
        return (int) (b & 0xFF);
    }

    public int getA() {
        return (int) (a & 0xFF);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    @Override
    protected MemorySegment toPtr(MemorySegment seg) {
        seg.set(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("r")), (byte) r);
        seg.set(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("g")), (byte) g);
        seg.set(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("b")), (byte) b);
        seg.set(ValueLayout.JAVA_BYTE, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("a")), (byte) a);
        return seg;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pixel pixel = (Pixel) o;
        return r == pixel.r && g == pixel.g && b == pixel.b && a == pixel.a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}';
    }

    /**
     * Additive blending (brightens the result)
     */
    public Pixel add(Pixel other) {
        int newR = Math.min(255, this.r + other.r);
        int newG = Math.min(255, this.g + other.g);
        int newB = Math.min(255, this.b + other.b);
        int newA = Math.min(255, this.a + other.a);
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Multiplicative blending (darkens the result)
     */
    public Pixel multiply(Pixel other) {
        int newR = (this.r * other.r) / 255;
        int newG = (this.g * other.g) / 255;
        int newB = (this.b * other.b) / 255;
        int newA = (this.a * other.a) / 255;
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Screen blending (opposite of multiply, lightens)
     */
    public Pixel screen(Pixel other) {
        int newR = 255 - ((255 - this.r) * (255 - other.r)) / 255;
        int newG = 255 - ((255 - this.g) * (255 - other.g)) / 255;
        int newB = 255 - ((255 - this.b) * (255 - other.b)) / 255;
        int newA = 255 - ((255 - this.a) * (255 - other.a)) / 255;
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Overlay blending (combination of multiply and screen)
     */
    public Pixel overlay(Pixel other) {
        int newR = (this.r < 128) ?
                (2 * this.r * other.r) / 255 :
                255 - (2 * (255 - this.r) * (255 - other.r)) / 255;

        int newG = (this.g < 128) ?
                (2 * this.g * other.g) / 255 :
                255 - (2 * (255 - this.g) * (255 - other.g)) / 255;

        int newB = (this.b < 128) ?
                (2 * this.b * other.b) / 255 :
                255 - (2 * (255 - this.b) * (255 - other.b)) / 255;

        int newA = this.a; // Keep original alpha or blend as needed
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Linear interpolation between two colors
     */
    public Pixel lerp(Pixel other, float t) {
        float invT = 1.0f - t;
        int newR = (int) (this.r * invT + other.r * t);
        int newG = (int) (this.g * invT + other.g * t);
        int newB = (int) (this.b * invT + other.b * t);
        int newA = (int) (this.a * invT + other.a * t);
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Average of two colors
     */
    public Pixel average(Pixel other) {
        int newR = (this.r + other.r) / 2;
        int newG = (this.g + other.g) / 2;
        int newB = (this.b + other.b) / 2;
        int newA = (this.a + other.a) / 2;
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Subtract colors
     */
    public Pixel subtract(Pixel other) {
        int newR = Math.max(0, this.r - other.r);
        int newG = Math.max(0, this.g - other.g);
        int newB = Math.max(0, this.b - other.b);
        int newA = Math.max(0, this.a - other.a);
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Lighten (take the maximum of each component)
     */
    public Pixel lighten(Pixel other) {
        int newR = Math.max(this.r, other.r);
        int newG = Math.max(this.g, other.g);
        int newB = Math.max(this.b, other.b);
        int newA = Math.max(this.a, other.a);
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Darken (take the minimum of each component)
     */
    public Pixel darken(Pixel other) {
        int newR = Math.min(this.r, other.r);
        int newG = Math.min(this.g, other.g);
        int newB = Math.min(this.b, other.b);
        int newA = Math.min(this.a, other.a);
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Color dodge (brightening blend)
     */
    public Pixel colorDodge(Pixel other) {
        int newR = (other.r == 255) ? 255 : Math.min(255, (this.r << 8) / (255 - other.r));
        int newG = (other.g == 255) ? 255 : Math.min(255, (this.g << 8) / (255 - other.g));
        int newB = (other.b == 255) ? 255 : Math.min(255, (this.b << 8) / (255 - other.b));
        int newA = this.a; // Keep original alpha
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Color burn (darkening blend)
     */
    public Pixel colorBurn(Pixel other) {
        int newR = (other.r == 0) ? 0 : Math.max(0, 255 - ((255 - this.r) << 8) / other.r);
        int newG = (other.g == 0) ? 0 : Math.max(0, 255 - ((255 - this.g) << 8) / other.g);
        int newB = (other.b == 0) ? 0 : Math.max(0, 255 - ((255 - this.b) << 8) / other.b);
        int newA = this.a; // Keep original alpha
        return new Pixel(newR, newG, newB, newA);
    }

    /**
     * Soft light (softer version of overlay)
     */
    public Pixel softLight(Pixel other) {
        int newR = (int) ((other.r < 128) ?
                this.r - ((255 - 2 * other.r) * this.r * (255 - this.r)) / (255 * 255) :
                this.r + ((2 * other.r - 255) * (sqrt(this.r) - this.r)) / 255);

        int newG = (int) ((other.g < 128) ?
                this.g - ((255 - 2 * other.g) * this.g * (255 - this.g)) / (255 * 255) :
                this.g + ((2 * other.g - 255) * (sqrt(this.g) - this.g)) / 255);

        int newB = (int) ((other.b < 128) ?
                this.b - ((255 - 2 * other.b) * this.b * (255 - this.b)) / (255 * 255) :
                this.b + ((2 * other.b - 255) * (sqrt(this.b) - this.b)) / 255);

        return new Pixel(newR, newG, newB, this.a);
    }

    /**
     * Helper method for softLight
     */
    private int sqrt(int value) {
        return (int) Math.sqrt(value);
    }

    /**
     * Adjust brightness
     */
    public Pixel brighten(float factor) {
        int newR = Math.min(255, (int) (this.r * factor));
        int newG = Math.min(255, (int) (this.g * factor));
        int newB = Math.min(255, (int) (this.b * factor));
        return new Pixel(newR, newG, newB, this.a);
    }

    /**
     * Adjust darkness
     */
    public Pixel darken(float factor) {
        int newR = Math.max(0, (int) (this.r * factor));
        int newG = Math.max(0, (int) (this.g * factor));
        int newB = Math.max(0, (int) (this.b * factor));
        return new Pixel(newR, newG, newB, this.a);
    }

    /**
     * Invert color
     */
    public Pixel invert() {
        return new Pixel(255 - this.r, 255 - this.g, 255 - this.b, this.a);
    }

    /**
     * Grayscale conversion
     */
    public Pixel grayscale() {
        int gray = (int) (0.299 * this.r + 0.587 * this.g + 0.114 * this.b);
        return new Pixel(gray, gray, gray, this.a);
    }
}
