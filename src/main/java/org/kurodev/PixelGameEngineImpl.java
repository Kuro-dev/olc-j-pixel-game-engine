package org.kurodev;

import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class PixelGameEngineImpl extends PixelGameEngine {
    private boolean run = true;

    public PixelGameEngineImpl(int width, int height) {
        super(width, height);
    }

    @Override
    public boolean onUserCreate() {
        return true;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) {
            return true;
        }
        if (getKey(KeyBoardKey.TAB).isPressed()) {
            consoleShow(KeyBoardKey.TAB, true);
        }
        int size = 50;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                draw(x, y, new Pixel((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            }
        }

        var mouse = getKey(MouseKey.LEFT);
        if (mouse.isHeld()) {
            Vector2D<Integer> pos = getWindowMousePos();
            draw(pos, Pixel.WHITE);
        }
        drawString(50, 50, "Hello World", Pixel.WHITE, 4);
        drawCircle(150, 150, 50, Pixel.WHITE);
        fillCircle(250, 250, 50, Pixel.WHITE);
        return run;
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        if ("quit".equals(command) || "exit".equals(command)) {
            run = false;
        }
        return true;
    }
}
