package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.util.Util;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LibraryDownloader {

    public static final ClassLoader classLoader = LibraryDownloader.class.getClassLoader();
    private static Method addURLMethod;

    private static final String INERTIA_LIBRARIES = InertiaBase.WEBSITE + "/libraries/";
    private static final String MAVEN_LIBRARIES = "https://repo1.maven.org/maven2/";

    public static final String YOGA_VERSION = "3.4.3";
    public static final String SKIA_VERSION = "0.143.17";
    public static final String SKIA_TYPES_VERSION = "0.2.0";

    public void main() throws NoSuchMethodException {
        var os = Util.getPlatform();
        var arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String osName = os == Util.OS.WINDOWS ? "windows" : os == Util.OS.OSX ? "macos" : "linux";//assume linux
        boolean isArm = arch.equals("aarch64") || arch.equals("arm64");

        this.downloadAndAddToClassLoaderIfNotExisting(INERTIA_LIBRARIES, "assets/icb/fonts/Comfortaa-Regular.ttf", "inertia-client/fonts/02.zip");
        this.downloadYoga(osName, isArm);
        this.downloadSkia(osName, isArm);

        this.downloadAndAddToClassLoaderIfNotExisting(INERTIA_LIBRARIES, "dorkbox/collections/Intset.class", "dorkbox/Collections-2.7.jar");
        this.downloadAndAddToClassLoaderIfNotExisting(INERTIA_LIBRARIES, "dorkbox/objectPool/Pool.class", "dorkbox/ObjectPool-4.4.jar");
        this.downloadAndAddToClassLoaderIfNotExisting(INERTIA_LIBRARIES, "dorkbox/updates/Updates.class", "dorkbox/Updates-1.1.jar");
        this.downloadAndAddToClassLoaderIfNotExisting(INERTIA_LIBRARIES, "dorkbox/tweenEngine/TweenEngine.class", "dorkbox/TweenEngine-9.2.jar");
    }

    private void downloadYoga(String osName, boolean isArm) {
        String yogaJar = String.format("org/lwjgl/lwjgl-yoga/%s/lwjgl-yoga-%s.jar", YOGA_VERSION, YOGA_VERSION);
        String nativesEXT = osName;
        if (isArm) {
            nativesEXT += "-arm64";
        }
        String nativesJar = String.format("org/lwjgl/lwjgl-yoga/%s/lwjgl-yoga-%s-natives-%s.jar", YOGA_VERSION, YOGA_VERSION, nativesEXT);

        this.downloadAndAddToClassLoaderIfNotExisting(MAVEN_LIBRARIES, "org/lwjgl/util/yoga/Yoga.class", yogaJar, nativesJar);
    }

    private void downloadSkia(String osName, boolean isArm) {
        String skiaJar = String.format("io/github/humbleui/skija-shared/%s/skija-shared-%s.jar", SKIA_VERSION, SKIA_VERSION);
        String nativesEXT = isArm ? "arm64" : "x64";
        String nativesJar = "io/github/humbleui/" + String.format("skija-%s-%s/%s/skija-%s-%s-%s.jar", osName, nativesEXT, SKIA_VERSION, osName, nativesEXT, SKIA_VERSION);
        String typesJar = String.format("io/github/humbleui/types/%s/types-%s.jar", SKIA_TYPES_VERSION, SKIA_TYPES_VERSION);

        this.downloadAndAddToClassLoaderIfNotExisting(MAVEN_LIBRARIES, "io/github/humbleui/skija/Paint.class", skiaJar, nativesJar);
        this.downloadAndAddToClassLoaderIfNotExisting(MAVEN_LIBRARIES, "io/github/humbleui/types/Rect.class", typesJar);

    }

    public void downloadAndAddToClassLoaderIfNotExisting(String librariesWebsite, String classToCheck, String... pathsToFiles) {
        if (!this.isClassInClassLoader(classToCheck)) {
            var librariesDirectory = InertiaBase.instance.getFileManager().getLibrariesDirectory();

            for (String pathToFile : pathsToFiles) {
                try {
                    Path libraryFile = librariesDirectory.resolve(pathToFile);
                    if (Files.notExists(libraryFile)) {
                        this.downloadURLToLibrariesFolder(librariesWebsite, pathToFile, libraryFile);
                    } else {
                        InertiaBase.LOGGER.info("Loading cached library {}", pathToFile);
                    }

                    this.addURL(libraryFile.toUri().toURL());
                } catch (Exception e) {
                    InertiaBase.LOGGER.error("Failed to load library {}", pathToFile, e);
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
        InertiaBase.LOGGER.info("Downloading library {}", librariesUrl + pathToFile);

        URI url = new URI(librariesUrl + pathToFile);
        Files.createDirectories(libraryOutputFile.getParent());

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(7500, TimeUnit.MILLISECONDS).build();
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
