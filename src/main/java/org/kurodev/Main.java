package org.kurodev;

import org.kurodev.jpixelgameengine.PixelGameEngineWrapper;

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
        PixelGameEngineWrapper wrapper = PixelGameEngineWrapper.getInstance();
        wrapper.start();
    }
}
