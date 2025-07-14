package org.kurodev.example;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.gfx.sprite.FlipMode;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;

import java.nio.file.Path;

public class PixelGameEngineImpl extends PixelGameEngine {
    private boolean run = true;
    private Sprite sprite;

    public PixelGameEngineImpl(int width, int height) {
        super(width, height);
    }

    @Override
    public boolean onUserCreate() {
        getUIManager().setEnabled(false);
        getUIManager().registerComponent(new OlcButton(50, 50, 200, 50, () -> System.out.println("CLICKED")));
        sprite = new Sprite(Path.of("./sprites/Char 1/Character 1.png"));
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
            drawPartialSprite(50, 50, sprite, 0, 0, 64, 64, 1, FlipMode.NONE);
//            drawSprite(0, 0, sprite, 0, FlipMode.NONE);
        }
        return run;
    }

    @Override
    protected void onTextEntryComplete(String text) {
        System.err.println(text);
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        System.out.println(command);
        if ("unload".equals(command)) {
            sprite = null;
            fill(Pixel.BLACK);
        }
        if ("reload".equals(command)) {
            sprite = new Sprite(Path.of("./sprites/Char 1/Character 1.png"));
        }
        if ("quit".equals(command) || "exit".equals(command)) {
            run = false;
        }
        return true;
    }
}
