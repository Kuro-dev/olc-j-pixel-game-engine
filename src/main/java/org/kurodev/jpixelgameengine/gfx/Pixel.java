package org.kurodev.jpixelgameengine.gfx;

public class Pixel {
    public static final Pixel BLACK = new Pixel(0, 0, 0);
    public static final Pixel WHITE = new Pixel(255, 255, 255);
    public static final Pixel RED = new Pixel(255, 0, 0);
    public static final Pixel GREEN = new Pixel(0, 255, 0);
    public static final Pixel BLUE = new Pixel(0, 0, 255);

    private final int r;
    private final int g;
    private final int b;
    private final int a;

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


}
