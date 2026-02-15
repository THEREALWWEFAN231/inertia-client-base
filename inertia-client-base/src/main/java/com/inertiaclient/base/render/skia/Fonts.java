package com.inertiaclient.base.render.skia;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Typeface;
import lombok.Getter;

import java.io.IOException;

public class Fonts {

    public static float DEFAULT_SIZE = 9f;
    @Getter
    private static Typeface OPEN_SANS_FACE;
    @Getter
    private static Font OPEN_SANS;

    @Getter
    private static Typeface COMFORTAA_FACE;
    @Getter
    private static Font COMFORTAA;

    public static void initFonts() {
        COMFORTAA_FACE = createTypeFace("icb/fonts/Comfortaa-Regular.ttf");
        COMFORTAA = new Font(COMFORTAA_FACE, DEFAULT_SIZE);

        OPEN_SANS_FACE = createTypeFace("icb/fonts/OpenSans-Regular.ttf");
        OPEN_SANS = new Font(OPEN_SANS_FACE, DEFAULT_SIZE);
    }

    public static Typeface createTypeFace(String assetLocation) {
        try {
            return SkiaUtils.createFont(assetLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Font getDefault() {
        return COMFORTAA;
    }

}
