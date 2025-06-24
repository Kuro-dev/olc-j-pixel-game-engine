package org.kurodev.jpixelgameengine;

public class PixelGameEngineWrapper {

    private static PixelGameEngineWrapper instance;

    private PixelGameEngineWrapper() {
        System.out.println("Creating PixelGameEngineWrapper");
        boolean success = PixelGameEngineNativeImpl.construct(500,
                500,
                1,
                1,
                false,
                false,
                false,
                true,
                this);
        if (success) {
            System.out.println("PixelGameEngineWrapper successfully constructed");
        } else {
            throw new RuntimeException("PixelGameEngineWrapper failed to construct");
        }
    }

    public static PixelGameEngineWrapper getInstance() {
        if (instance == null) {
            instance = new PixelGameEngineWrapper();
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
        return true;
    }

    @NativeCallCandidate
    public boolean onUserUpdate(float delta) {
        return true;
    }

    @NativeCallCandidate
    public boolean onUserDestroy() {
        return true;
    }
}
