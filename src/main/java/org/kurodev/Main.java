package org.kurodev;

import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.io.IOException;

/**
 * Just a small class for testing purposes of window creation
 */
public class Main {
    static {
        try {
            NativeLoader.loadLibraries();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }

    public static void main(String[] args) {
        @SuppressWarnings("resource") //will get cleaned up on exit anyway
        PixelGameEngine wrapper = new PixelGameEngine(500, 500) {
            @Override
            public boolean onUserCreate() {
                return false;
            }

            @Override
            public boolean onUserUpdate(float delta) {
                if (!isFocussed()) {
                    return true;
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
                    draw(pos, Pixel.RED);
                }
                drawString(50, 50, "Hello World", Pixel.WHITE, 4);
                drawCircle(150, 150, 50, Pixel.WHITE);
                fillCircle(250, 250, 50, Pixel.WHITE);
                return true;
            }
        };
        wrapper.start();
    }
}
