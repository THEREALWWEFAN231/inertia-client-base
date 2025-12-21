package com.inertiaclient.base.utils;


import com.inertiaclient.base.InertiaBase;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

public class UIUtils {

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static float getCenterOffset(float componentLength, float maxLength) {//named length because it could be width or height :shrug:
        return (maxLength / 2) - (componentLength / 2);
    }

    public static double getCenterOffset(double componentLength, double maxLength) {//named length because it could be width or height :shrug:
        return (maxLength / 2) - (componentLength / 2);
    }

    public static float getTickDelta() {
        return InertiaBase.mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }

    public static Color colorWithAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color colorWithAlpha(Color color, float alpha) {
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha);
    }

    public static Color transitionColor(Color from, Color to, float progress) {
        if (progress > 1) {
            return to;
        }
        if (progress < 0) {
            return from;
        }
        int redDiff = to.getRed() - from.getRed();
        int greenDiff = to.getGreen() - from.getGreen();
        int blueDiff = to.getBlue() - from.getBlue();
        int alphaDiff = to.getAlpha() - from.getAlpha();

        int newRed = from.getRed() + (int) (redDiff * progress);
        int newGreen = from.getGreen() + (int) (greenDiff * progress);
        int newBlue = from.getBlue() + (int) (blueDiff * progress);
        int newAlpha = from.getAlpha() + (int) (alphaDiff * progress);

        return new Color(newRed, newGreen, newBlue, newAlpha);
    }

    public static float transitionNumber(float from, float to, float progress) {
        float diff = to - from;
        float newNumber = from + (diff * progress);

        return newNumber;
    }

    //http://www.camick.com/java/source/HSLColor.java
    public static float[] colorToHSL(Color color) {
        //  Get RGB values in the range 0 - 1

        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        //	Minimum and Maximum RGB values are used in the HSL calculations

        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));

        //  Calculate the Hue

        float h = 0;

        if (max == min)
            h = 0;
        else if (max == r)
            h = ((60 * (g - b) / (max - min)) + 360) % 360;
        else if (max == g)
            h = (60 * (b - r) / (max - min)) + 120;
        else if (max == b)
            h = (60 * (r - g) / (max - min)) + 240;

        //  Calculate the Luminance

        float l = (max + min) / 2;

        //  Calculate the Saturation

        float s = 0;

        if (max == min)
            s = 0;
        else if (l <= .5f)
            s = (max - min) / (max + min);
        else
            s = (max - min) / (2 - max - min);

        return new float[]{h, s, l};
    }

    public static String orderedTextToString(FormattedCharSequence orderedText) {
        StringBuilder realString = new StringBuilder();
        orderedText.accept((index, style, codePoint) -> {
            realString.appendCodePoint(codePoint);
            return true;
        });
        return realString.toString();
    }

    public static Color rainbow(long timeOffset, float speed) {
        double time = GLFW.glfwGetTime() * speed * 75;
        float hue = (float) (((time + timeOffset) % 360) / 360f);

        return new Color(Color.HSBtoRGB(hue, 1f, 1f));
    }

    public static Color rainbow(long timeOffset) {
        return UIUtils.rainbow(timeOffset, 1);
    }

    public static Color rainbow() {
        return UIUtils.rainbow(0, 1);
    }

    public static void playButtonPressedSound() {
        InertiaBase.mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}
