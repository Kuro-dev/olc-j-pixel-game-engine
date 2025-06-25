package org.kurodev.jpixelgameengine.impl;

public class PixelGameEngineInitialiser {
    private int screen_w = 500;
    private int screen_h = 500;
    private int pixel_w = 1;
    private int pixel_h = 1;
    private boolean full_screen = false;
    private boolean vsync = false;
    private boolean cohesion = false;
    private boolean realwindow = true;


    public PixelGameEngineInitialiser screen_w(int screen_w) {
        this.screen_w = screen_w;
        return this;
    }


    public PixelGameEngineInitialiser screen_h(int screen_h) {
        this.screen_h = screen_h;
        return this;
    }


    public PixelGameEngineInitialiser pixel_w(int pixel_w) {
        this.pixel_w = pixel_w;
        return this;
    }

    public PixelGameEngineInitialiser pixel_h(int pixel_h) {
        this.pixel_h = pixel_h;
        return this;
    }


    public PixelGameEngineInitialiser full_screen(boolean full_screen) {
        this.full_screen = full_screen;
        return this;
    }

    public PixelGameEngineInitialiser vsync(boolean vsync) {
        this.vsync = vsync;
        return this;
    }


    public PixelGameEngineInitialiser cohesion(boolean cohesion) {
        this.cohesion = cohesion;
        return this;
    }

    public PixelGameEngineInitialiser realWindow(boolean realwindow) {
        this.realwindow = realwindow;
        return this;
    }

    public void start() {
        PixelGameEngineNativeImpl.construct(screen_w, screen_h, pixel_w, pixel_h, full_screen, vsync, cohesion, realwindow, PixelGameEngineWrapper.instance);
        Thread t = new Thread(() -> {
            boolean result = PixelGameEngineNativeImpl.start();
            System.out.print("PixelGameEngineWrapper ");
            if (result) {
                System.out.println("successfully started");
                PixelGameEngineWrapper.instance.setInitialised(true);
            } else {
                System.out.println("failed to start");
            }
        }, "Game thread");
        t.start();
    }
}
