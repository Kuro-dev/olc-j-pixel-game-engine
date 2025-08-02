package org.kurodev.example;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.gfx.animation.Animation;
import org.kurodev.jpixelgameengine.gfx.animation.SpriteSheetDecal;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.nio.file.Path;

public class PixelGameEngineImpl extends PixelGameEngine {
    private boolean run = true;
    private Sprite sprite;
    private Decal decal;
    private Animation animation;

    public PixelGameEngineImpl(int width, int height) {
        super(width, height, 1, 1);
    }

    @Override
    public boolean onUserCreate() {
        sprite = new Sprite(Path.of("./sprites/Char 1/Character 1.png"));
        decal = new Decal(sprite);
        SpriteSheetDecal ani = new SpriteSheetDecal(decal, Vector2D.ofFloat(64));
        this.animation = ani.animateTimeBased(Vector2D.ofInt(0, 8 * 64), 24, .2, true);
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
        animation.draw(delta, this, FloatVector2D.ZERO, Vector2D.ofFloat(4, 4), Pixel.WHITE);
        return run;
    }

    @Override
    protected void onTextEntryComplete(String text) {
        System.err.println(text);
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        consoleWriteln(">" + command);
        if ("unload".equals(command)) {
            sprite = null;
            decal = null;
            clear(Pixel.BLACK);
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
