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
}
