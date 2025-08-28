package org.kurodev.example;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class PixelGameEngineImpl extends PixelGameEngine {
    private boolean run = true;

    public PixelGameEngineImpl(int width, int height) {
        super(width, height, 1, 1);
    }

    @Override
    public boolean onUserCreate() {
        setPixelMode(PixelMode.NORMAL);
        return true;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) {
            return true;
        }
        var fKey = getKey(KeyBoardKey.F);
        Vector2D<Integer> pos = getWindowPos();
        if (fKey.isPressed()) {
            resize(pos, Vector2D.ofInt(500, 500));
        } else if (fKey.isReleased()) {
            resize(pos, Vector2D.ofInt(600, 600));
        }

        if (getKey(KeyBoardKey.TAB).isPressed()) {
            consoleShow(KeyBoardKey.TAB, true);
        }
        gradientLineDecal(Vector2D.ofFloat(50, 50), Vector2D.ofFloat(300, 300), Pixel.BLUE, Pixel.RED, 50);
        return run;
    }

    @Override
    protected void onTextEntryComplete(String text) {
        System.err.println(text);
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        consoleWriteln(">" + command);
        if ("quit".equals(command) || "exit".equals(command)) {
            run = false;
            return false;
        }
        return true;
    }
}
