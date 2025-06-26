package org.kurodev;

import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;

public class PixelGameEngineImpl extends PixelGameEngine {
    public PixelGameEngineImpl(int w, int h) {
        super(w, h);
    }

    @Override
    public boolean onUserCreate() {
        return true;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) return true;
        var mousePos = getWindowMousePos();
        draw(mousePos.getX(), mousePos.getY(), Pixel.WHITE);
        return true;
    }
}
