package org.kurodev;

import org.kurodev.jpixelgameengine.PixelGameEngine;

import java.io.IOException;

public class Main {
    static {
        try {
            NativeLoader.loadLibraries();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }

    public static void main(String[] args) {
        PixelGameEngine.init()
                .screen_w(800)
                .screen_h(500)
                .start();
    }
}
