package org.kurodev;


import java.io.IOException;
import java.nio.file.Path;


public class NativeLoader {
    static {
        System.setProperty("jdk.incubator.foreign", "permit");
    }

    public static void loadLibrary(String name) throws IOException {
        String resourcePath = "/" + System.mapLibraryName("org_kurodev_" + name);
        System.load(Path.of("./lib", resourcePath).toAbsolutePath().toString());
    }

    public static void loadLibraries() throws IOException {
        loadLibrary("pixelGameEngineFFM");
    }
}
