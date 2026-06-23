package org.kurodev.example;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.BatchedPixelGameEngine;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class PerformanceTest {


    public static void main(String[] args) {
        final int width = 500, height = 500;
        PixelGameEngine a = new PixelGameEngineImpl(width, height);
        a.setWindowTitle("Raw");
        a.start();
        PixelGameEngine b = new BatchedPixelGameEngineImpl(width, height);
        b.setWindowTitle("Batched");
        b.start();

    }

    /**
     * Must be named class and cannot be anonymous.
     */
    public static class PixelGameEngineImpl extends PixelGameEngine {

        private Vector2D<Integer> screen;

        public PixelGameEngineImpl(int width, int height) {
            super(width, height, 1, 1);
        }

        public static void main(String[] args) {
            PixelGameEngine wrapper = new org.kurodev.example.PixelGameEngineImpl(500, 500);
            wrapper.start();
        }

        @Override
        public boolean onUserCreate() {
            screen = getWindowSize();

            return true;
        }

        @Override
        public boolean onUserUpdate(float delta) {
            if (!isFocussed()) {
                return true;
            }
            Vector2D<Integer> newScreen = getWindowSize();
            if (!screen.equals(newScreen)) {
                setScreenSize(screen.getX(), screen.getY());
            }
            screen = newScreen;
            for (int y = 0; y < screen.getY(); y++) {
                for (int x = 0; x < screen.getX(); x++) {
                    draw(x, y, new Pixel((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
                }
            }
            return true;
        }
    }

    public static class BatchedPixelGameEngineImpl extends BatchedPixelGameEngine {

        private Vector2D<Integer> screen;

        public BatchedPixelGameEngineImpl(int width, int height) {
            super(width, height, 1, 1);
        }

        public static void main(String[] args) {
            PixelGameEngine wrapper = new org.kurodev.example.PixelGameEngineImpl(500, 500);
            wrapper.start();
        }

        @Override
        public boolean onUserCreate() {
            screen = getWindowSize();
            return true;
        }

        @Override
        public boolean onUserUpdate(float delta) {
            if (!isFocussed()) {
                return true;
            }
            Vector2D<Integer> newScreen = getWindowSize();
            if (!screen.equals(newScreen)) {
                setScreenSize(screen.getX(), screen.getY());
            }
            screen = newScreen;
            for (int y = 0; y < screen.getY(); y++) {
                for (int x = 0; x < screen.getX(); x++) {
                    draw(x, y, new Pixel((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
                }
            }
            return true;
        }
    }
}


