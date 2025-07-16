package org.kurodev.example;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.nio.file.Path;

public class PixelGameEngineImpl extends PixelGameEngine {
    private static final Vector2D<Float> SPRITE_SIZE = Vector2D.ofFloat(64);
    private boolean run = true;
    private Sprite sprite;
    private Decal decal;

    public PixelGameEngineImpl(int width, int height) {
        super(width, height, 1, 1);
    }

    @Override
    public boolean onUserCreate() {
        sprite = new Sprite(Path.of("./sprites/Char 1/Character 1.png"));
        decal = new Decal(sprite);
        setPixelMode(PixelMode.NORMAL);
        return true;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) {
            return true;
        }
        if (getKey(KeyBoardKey.TAB).isPressed()) {
            consoleShow(KeyBoardKey.TAB, true);
//            textEntryEnable(true, "Example");
        }
        if (sprite != null) {
            drawRect(50, 50, 64, 64, Pixel.RED);
            drawPartialDecal(Vector2D.ofFloat(50, 50), decal, FloatVector2D.ZERO, SPRITE_SIZE, FloatVector2D.ONE, Pixel.WHITE);
        }
        return run;
    }

    @Override
    protected void onTextEntryComplete(String text) {
        System.err.println(text);
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        consoleWriteln(">" + command);
        if ("gc".equals(command)) {
            System.gc();
            System.gc();
            System.gc();
            System.gc();
            System.gc();
        }
        if ("unload".equals(command)) {
            sprite = null;
            decal = null;
            clear(Pixel.BLACK);
            System.gc();
            System.gc();
            return false;
        }
        if ("reload".equals(command)) {
            sprite = new Sprite(Path.of("./sprites/Char 1/Character 1.png"));
            decal = new Decal(sprite);
            return false;
        }
        if ("quit".equals(command) || "exit".equals(command)) {
            run = false;
            return false;
        }
        return true;
    }
}
