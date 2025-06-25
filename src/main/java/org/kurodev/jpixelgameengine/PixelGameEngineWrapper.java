package org.kurodev.jpixelgameengine;

import java.util.Random;

public class PixelGameEngineWrapper {

    private static final PixelGameEngineWrapper instance = new PixelGameEngineWrapper();
    Random rand = new Random();
    private boolean initialised = false;

    private PixelGameEngineWrapper() {

    }

    public static PixelGameEngineWrapper getInstance() {
        if (!instance.initialised) {
            PixelGameEngineNativeImpl.construct(
                    1200,
                    1200,
                    1,
                    1,
                    false,
                    false,
                    false,
                    true,
                    instance);
            instance.initialised = true;
        }

        return instance;
    }

    public void start() {
        Thread t = new Thread(() -> {
            boolean result = PixelGameEngineNativeImpl.start();
            System.out.print("PixelGameEngineWrapper ");
            if (result) {
                System.out.println("successfully started");
            } else {
                System.out.println("failed to start");
            }
        }, "Game thread");
        t.start();
    }

    @NativeCallCandidate
    public boolean onUserCreate() {
        System.out.println("JAVA- created");
        return true;
    }

    @NativeCallCandidate
    public boolean onUserUpdate(float delta) {
        int size = 1200;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                draw(x, y, new Pixel(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            }
        }
        return true;
    }

    @NativeCallCandidate
    public boolean onUserDestroy() {
        System.out.println("JAVA- destroyed");
        return true;
    }

    public boolean draw(Vector2D<? extends Number> pos, Pixel p) {
        return draw(pos.getX().intValue(), pos.getY().intValue(), p);
    }

    public boolean draw(int x, int y, Pixel p) {
        return PixelGameEngineNativeImpl.draw(x, y, p.getRGBA());
    }

}
