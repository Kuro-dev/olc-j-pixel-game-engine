package org.kurodev;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLoader {
    public static void loadLibrary(String name) throws IOException {
        String platform = getPlatformFolder();
        String resourcePath = platform + "/org_kurodev_" + System.mapLibraryName(name);

        try (InputStream is = NativeLoader.class.getResourceAsStream("/" + resourcePath)) {
            if (is == null) {
                throw new UnsatisfiedLinkError("Native library not found: " + resourcePath);
            }

            Path tempFile = Files.createTempFile(name, ".dll");
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            System.load(tempFile.toAbsolutePath().toString());
        }
    }

    private static String getPlatformFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("win")) {
            return "win32-" + (arch.contains("64") ? "x86-64" : "x86");
        }
        // Add other platforms if needed
        throw new UnsupportedOperationException("Unsupported platform");
    }

    public static void loadLibraries() throws IOException {
        loadLibrary("Example");
    }
}
