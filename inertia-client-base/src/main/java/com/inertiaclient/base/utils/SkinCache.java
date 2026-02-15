package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import io.github.humbleui.skija.Image;
import lombok.AllArgsConstructor;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class SkinCache {

    private Path cacheDirectory;
    private final ConcurrentHashMap<String, CompletableFuture<Image>> cache = new ConcurrentHashMap<>();


    public CompletableFuture<Image> load(String uuidOrName) {
        if (cache.containsKey(uuidOrName)) {
            return cache.get(uuidOrName);
        }
        var future = CompletableFuture.supplyAsync(() -> {
            try {
                Path cachedFile = cacheDirectory.resolve(uuidOrName.toLowerCase() + ".png");

                byte[] bytes;
                if (Files.exists(cachedFile)) {
                    // Load from disk
                    bytes = Files.readAllBytes(cachedFile);
                } else {
                    // Download
                    //8x8, original size
                    bytes = download(String.format("https://mc-heads.net/avatar/%s/8", uuidOrName));
                    if (bytes == null) {
                        throw new IllegalStateException(String.format("Failed to download head %s", uuidOrName));
                    }
                    // Save to cache
                    Files.write(cachedFile, bytes, StandardOpenOption.CREATE);
                }

                return Image.makeDeferredFromEncodedBytes(bytes);

            } catch (Exception e) {
                InertiaBase.LOGGER.error("Failed to load image for {}", uuidOrName, e);
                throw new IllegalStateException(e);
            }
        });
        cache.put(uuidOrName, future);
        return future;
    }

    private byte[] download(String urlString) {
        return InertiaBase.createWebRequest(urlString, closeableHttpResponse -> {
            try {
                return EntityUtils.toByteArray(closeableHttpResponse.getEntity());
            } catch (IOException e) {
                return null;
            }
        });

    }
}
