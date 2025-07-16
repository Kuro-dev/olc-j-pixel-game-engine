package org.kurodev.example;

import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;

/**
 * Just a small class for testing purposes of window creation
 */
public class Main {
    public static void main(String[] args) {
        PixelGameEngine wrapper = new PixelGameEngineImpl(800, 800);
        wrapper.start();
    }
}
