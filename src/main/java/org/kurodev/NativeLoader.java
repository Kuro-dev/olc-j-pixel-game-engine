package org.kurodev;

import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;
import org.kurodev.jpixelgameengine.impl.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.foreign.ValueLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NativeLoader {

    private static final NativeFunction<String> GET_LIBRARY_VERSION = new NativeFunction<>("get_library_version", ValueLayout.ADDRESS);
    private static final String OWNER = "Kuro-dev";
    private static final String REPO = "olc-j-pixel-game-engine";
    private static final Path LIB_DIR = Path.of("lib");
    private static final Logger log = LoggerFactory.getLogger(NativeLoader.class);

    static {
        System.setProperty("jdk.incubator.foreign", "permit");
    }

    public static void loadLibrary(String name) throws IOException, InterruptedException {
        String mappedName = System.mapLibraryName("org_kurodev_" + name);
        Path libPath = LIB_DIR.resolve(mappedName);

        if (Files.notExists(libPath)) {
            System.out.println("[NativeLoader] Library not found, attempting to download...");
            downloadLibraryFromGitHubRelease(mappedName, libPath);
        }
        Thread.sleep(500);
        System.load(libPath.toAbsolutePath().toString());

        String javaVersion = getJavaVersion();
        String nativeVersion = getLibraryVersion();
        log.info("java version: {}", javaVersion);
        log.info("native version: {}", nativeVersion);
        if (!javaVersion.equals(nativeVersion)) {
            log.warn("[NativeLoader] Detected outdated Library files. The program might potentially break or behave in unexpected ways.\n" +
                    "Please Close the program, delete the \"/lib\" folder and restart the program to update.");
        }
    }

    public static void loadLibraries() throws IOException, InterruptedException {
        loadLibrary("pixelGameEngineFFM");
    }

    private static void downloadLibraryFromGitHubRelease(String fileName, Path targetPath) throws IOException, InterruptedException {
        String version = getJavaVersion();
        try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()) {
            String releaseBody;
            HttpRequest versionRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/tags/" + version))
                    .header("Accept", "application/vnd.github+json")
                    .build();

            HttpResponse<String> versionResponse = client.send(versionRequest, HttpResponse.BodyHandlers.ofString());
            if (versionResponse.statusCode() == 200) {
                releaseBody = versionResponse.body();
                System.out.println("[NativeLoader] Found release for version: " + version);
            } else if (versionResponse.statusCode() == 404) {
                System.out.println("[NativeLoader] Release for version " + version + " not found, falling back to latest release...");
                HttpRequest latestRequest = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/latest"))
                        .header("Accept", "application/vnd.github+json")
                        .build();
                HttpResponse<String> latestResponse = client.send(latestRequest, HttpResponse.BodyHandlers.ofString());
                if (latestResponse.statusCode() != 200) {
                    throw new IOException("Failed to fetch latest GitHub release: " + latestResponse.body());
                }
                releaseBody = latestResponse.body();
            } else {
                throw new IOException("Failed to fetch release for version " + version + ": HTTP " + versionResponse.statusCode());
            }
            String assetUrl = findAssetDownloadUrl(releaseBody, fileName);
            HttpRequest fileRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(assetUrl))
                    .header("Accept", "application/octet-stream")
                    .build();
            HttpResponse<InputStream> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofInputStream());
            if (fileResponse.statusCode() != 200) {
                throw new IOException("Failed to download file: HTTP " + fileResponse.statusCode());
            }
            Files.createDirectories(LIB_DIR);
            try (InputStream in = fileResponse.body(); OutputStream out = Files.newOutputStream(targetPath)) {
                in.transferTo(out);
            }
            System.out.println("[NativeLoader] Downloaded: " + fileName);
        }
    }


    @SuppressWarnings("RegExpRedundantEscape")
    private static String findAssetDownloadUrl(String json, String fileName) {
        Pattern p = Pattern.compile("browser_download_url\":\\s?\"(https:/[ \\w\\/\\.-]+)\"");
        Matcher m = p.matcher(json);

        while (m.find()) {
            String url = m.group(1);
            if (url.contains(fileName)) {
                return url;
            }
        }

        throw new RuntimeException("Failed to fetch file: " + fileName + ".\n Please download it from https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/latest");
    }

    /**
     * Retrieves the version of this release.
     */
    public static String getJavaVersion() {
        try (InputStream input = NativeLoader.class.getResourceAsStream("/version.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                return prop.getProperty("version");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "unknown";
    }

    /**
     * Retrieves the version of this release for the native library.
     */
    public static String getLibraryVersion() {
        return GET_LIBRARY_VERSION.invokeExact(Util::cString);
    }
}
