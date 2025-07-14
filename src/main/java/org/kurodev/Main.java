package org.kurodev;

import org.kurodev.example.PixelGameEngineImpl;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;

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
        PixelGameEngine wrapper = new PixelGameEngineImpl(500, 500);
        wrapper.start();
    }
}
