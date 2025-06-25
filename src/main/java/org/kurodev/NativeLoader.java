package org.kurodev;


import java.io.IOException;
import java.nio.file.Path;


public class NativeLoader {
    public static void loadLibrary(String name) throws IOException {
        String platform = getPlatformFolder();
        String resourcePath = platform + "/" + System.mapLibraryName("org_kurodev_" + name);
        System.load(Path.of("./lib", resourcePath).toAbsolutePath().toString());
    }

    private static String getPlatformFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("win")) {
            return "win32-" + (arch.contains("64") ? "x86-64" : "x86");
        } else if (os.contains("linux")) {
            return "linux-" + (arch.contains("64") ? "x86-64" : "x86");
        }
        // Add other platforms if needed
        throw new UnsupportedOperationException("Unsupported platform" + os + arch);
    }

    public static void loadLibraries() throws IOException {
        loadLibrary("pixelgameEngineWrapper");
    }
}
