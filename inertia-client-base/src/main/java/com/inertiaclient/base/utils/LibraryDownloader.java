package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.Util;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class LibraryDownloader {

    public static final ClassLoader classLoader = LibraryDownloader.class.getClassLoader();
    private static Method addURLMethod;

    public void main() throws NoSuchMethodException {
        var os = Util.getPlatform();
        boolean isLinux = os != Util.OS.WINDOWS && os != Util.OS.OSX;
        String osName = getLibraryNativeOsName(os);

        this.downloadAndAddToClassLoaderIfNotExisting("assets/icb/fonts/Comfortaa-Regular.ttf", "inertia-client/fonts/02.zip");
        this.downloadAndAddToClassLoaderIfNotExisting("org/lwjgl/util/yoga/Yoga.class", "yoga/lwjgl-yoga-3.3.4.jar", "yoga-natives/lwjgl-yoga-3.3.4-natives-" + osName + ".jar");
        this.downloadAndAddToClassLoaderIfNotExisting("io/github/humbleui/skija/Paint.class", "skia/skija-shared-0.116.4.jar", "skia-natives/skija-" + osName + "-x64-0.116.4.jar");
        this.downloadAndAddToClassLoaderIfNotExisting("io/github/humbleui/types/Rect.class", "skia/types-0.2.0.jar");

        this.downloadAndAddToClassLoaderIfNotExisting("dorkbox/collections/Intset.class", "dorkbox/Collections-2.7.jar");
        this.downloadAndAddToClassLoaderIfNotExisting("dorkbox/objectPool/Pool.class", "dorkbox/ObjectPool-4.4.jar");
        this.downloadAndAddToClassLoaderIfNotExisting("dorkbox/updates/Updates.class", "dorkbox/Updates-1.1.jar");
        this.downloadAndAddToClassLoaderIfNotExisting("dorkbox/tweenEngine/TweenEngine.class", "dorkbox/TweenEngine-9.2.jar");
    }

    public void downloadAndAddToClassLoaderIfNotExisting(String classToCheck, String... pathsToFiles) {

        if (!this.isClassInClassLoader(classToCheck)) {
            String librariesWebsite = InertiaBase.WEBSITE + "/libraries/";
            var librariesDirectory = InertiaBase.instance.getFileManager().getLibrariesDirectory();

            for (String pathToFile : pathsToFiles) {
                try {
                    Path libraryFile = librariesDirectory.resolve(pathToFile);
                    if (Files.notExists(libraryFile)) {
                        this.downloadURLToLibrariesFolder(librariesWebsite, pathToFile, libraryFile);
                    }

                    this.addURL(libraryFile.toUri().toURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void addURL(URL url) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {


        if (addURLMethod == null) {
            addURLMethod = LibraryDownloader.classLoader.getClass().getDeclaredMethod("addUrlFwd", URL.class);
            addURLMethod.setAccessible(true);
        }

        addURLMethod.invoke(LibraryDownloader.classLoader, url);
    }

    public String getLibraryNativeOsName(Util.OS operatingSystem) {
        if (operatingSystem == Util.OS.OSX) {
            return "macos";
        }

        if (operatingSystem == Util.OS.SOLARIS) {
            return Util.OS.LINUX.telemetryName();
        }
        return operatingSystem.telemetryName();
    }

    public boolean isClassInClassLoader(String classToCheck) {
        //cant use Class.forName, or Launch.classLoader.findClass, mojang has some weird stuff...
        try {

            URL findResource = classLoader.getResource(classToCheck);
            if (findResource != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void downloadURLToLibrariesFolder(String librariesUrl, String pathToFile, Path libraryOutputFile) throws Exception {

        URI url = new URI(librariesUrl + pathToFile);
        Files.createDirectories(libraryOutputFile.getParent());

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(7500).build();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            HttpGet getRequest = new HttpGet(url);

            getRequest.setHeader("User-Agent", InertiaBase.getUserAgentForURL(url));

            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (OutputStream outstream = Files.newOutputStream(libraryOutputFile)) {
                        entity.writeTo(outstream);
                    }
                }
            }
        }
    }

}
