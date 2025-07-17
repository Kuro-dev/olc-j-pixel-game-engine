package org.kurodev;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NativeLoader {
    static {
        System.setProperty("jdk.incubator.foreign", "permit");
    }

    private static final String OWNER = "Kuro-dev";
    private static final String REPO = "olc-j-pixel-game-engine";
    private static final Path LIB_DIR = Path.of("lib");

    public static void loadLibrary(String name) throws IOException, InterruptedException {
        String mappedName = System.mapLibraryName("org_kurodev_" + name);
        Path libPath = LIB_DIR.resolve(mappedName);

        if (Files.notExists(libPath)) {
            System.out.println("[NativeLoader] Library not found, attempting to download...");
            downloadLibraryFromGitHubRelease(mappedName, libPath);
        }
        Thread.sleep(500);
        System.load(libPath.toAbsolutePath().toString());
    }

    public static void loadLibraries() throws IOException, InterruptedException {
        loadLibrary("pixelGameEngineFFM");
    }

    private static void downloadLibraryFromGitHubRelease(String fileName, Path targetPath) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()) {
            HttpRequest releaseRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/latest"))
                    .header("Accept", "application/vnd.github+json")
                    .build();

            HttpResponse<String> releaseResponse = client.send(releaseRequest, HttpResponse.BodyHandlers.ofString());
            if (releaseResponse.statusCode() != 200) {
                throw new IOException("Failed to fetch GitHub release: " + releaseResponse.body());
            }

            String body = releaseResponse.body();
            String assetUrl = findAssetDownloadUrl(body, fileName);

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
            try (InputStream in = fileResponse.body()) {
                OutputStream out = Files.newOutputStream(targetPath);
                in.transferTo(out);
                out.close();
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
}
