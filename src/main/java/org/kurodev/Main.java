package org.kurodev;

import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;

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
        PixelGameEngine wrapper = new PixelGameEngineImpl(500, 500);
        wrapper.start();
    }
}
