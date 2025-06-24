package org.kurodev;

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

        Example ex = new Example("Hello World");
        int result = ex.doStuff(5); //calling native method
        System.out.println(result); // prints "1"
    }
}
