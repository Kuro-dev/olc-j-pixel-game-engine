package org.kurodev.jpixelgameengine.impl;

import org.kurodev.jpixelgameengine.PixelGameEngine;
import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class PixelGameEngineWrapper implements PixelGameEngine {

    static final PixelGameEngineWrapper instance = new PixelGameEngineWrapper();
    private boolean initialised = false;

    private PixelGameEngineWrapper() {

    }

    public static PixelGameEngineWrapper getInstance() {
        if (!instance.initialised) {
            throw new IllegalStateException("PixelGameEngineWrapper has not been initialised");
        }
        return instance;
    }

    void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    @NativeCallCandidate
    public boolean onUserCreate() {
        System.out.println("JAVA- created");
        return true;
    }

    @NativeCallCandidate
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) {
            return true;
        }
        int size = 30;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                draw(x, y, new Pixel((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            }
        }

        var mouse = getKey(MouseKey.LEFT);
        if (mouse.isHeld()) {
            Vector2D<Integer> pos = getWindowMousePos();
            draw(pos, Pixel.RED);
        }
        return true;
    }

    @NativeCallCandidate
    public boolean onUserDestroy() {
        System.out.println("JAVA- destroyed");
        return true;
    }

    public boolean isFocussed() {
        return PixelGameEngineNativeImpl.isFocused();
    }

    public boolean draw(Vector2D<? extends Number> pos, Pixel p) {
        return draw(pos.getX().intValue(), pos.getY().intValue(), p);
    }

    public boolean draw(int x, int y, Pixel p) {
        return PixelGameEngineNativeImpl.draw(x, y, p.getRGBA());
    }

    public HWButton getKey(KeyBoardKey k) {
        return PixelGameEngineNativeImpl.getKey((byte) k.ordinal());
    }

    public HWButton getKey(MouseKey k) {
        return PixelGameEngineNativeImpl.getMouse((byte) k.ordinal());
    }

    public Vector2D<Integer> getMousePos() {
        return PixelGameEngineNativeImpl.getMousePos();
    }

    public Vector2D<Integer> getWindowMousePos() {
        return PixelGameEngineNativeImpl.getWindowMouse();
    }

    /**
     * @return a value < 0 if scrolling down, a value > 0 if scrolling up, or 0 if not scrolling
     */
    public int getMouseWheel() {
        return PixelGameEngineNativeImpl.getMouseWheel();
    }
}
