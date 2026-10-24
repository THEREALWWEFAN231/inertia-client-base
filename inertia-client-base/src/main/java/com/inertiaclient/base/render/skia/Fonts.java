package com.inertiaclient.base.render.skia;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Typeface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

public class Fonts {

    public static float DEFAULT_SIZE = 9f;
    @Getter
    private static Typeface OPEN_SANS_FACE;
    @Getter
    private static FontWrapper OPEN_SANS;

    @Getter
    private static Typeface COMFORTAA_FACE;
    @Getter
    private static FontWrapper COMFORTAA;

    @Getter
    private static Typeface TRUENO_FACE;
    @Getter
    private static FontWrapper TRUENO;

    public static void initFonts() {
        COMFORTAA_FACE = createTypeFace("icb/fonts/Comfortaa-Regular.ttf");
        COMFORTAA = new FontWrapper(new Font(COMFORTAA_FACE, DEFAULT_SIZE));

        OPEN_SANS_FACE = createTypeFace("icb/fonts/OpenSans-Regular.ttf");
        OPEN_SANS = new FontWrapper(new Font(OPEN_SANS_FACE, DEFAULT_SIZE));

        TRUENO_FACE = createTypeFace("icb/fonts/TruenoRgIt.otf");
        TRUENO = new FontWrapper(new Font(TRUENO_FACE, DEFAULT_SIZE), -1);
    }

    public static Typeface createTypeFace(String assetLocation) {
        try {
            return SkiaUtils.createFont(assetLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FontWrapper getDefault() {
        return COMFORTAA;
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class FontWrapper {

        @Getter
        private final Font skiaFont;
        @Getter
        private float yOffset = 0;
    }

}
