package com.inertiaclient.base.render.skia;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.svg.SVGDOM;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;


public class SkiaUtils {

    public static Typeface createFont(String assetLocation) throws IOException {
        try (var inputStream = SkiaUtils.class.getResourceAsStream("/assets/" + assetLocation)) {
            return Typeface.makeFromData(Data.makeFromBytes(inputStream.readAllBytes()));
        }
    }

    public static SVGDOM createSvgDom(String assetLocation) throws IOException {
        try (var inputStream = SkiaUtils.class.getResourceAsStream("/assets/" + assetLocation)) {
            return new SVGDOM(Data.makeFromBytes(inputStream.readAllBytes()));
        }
    }

    /**
     * @param shaderName just the name of the file, extension is expected to be sksl and is not required
     * @return
     * @throws IOException
     */
    public static RuntimeEffect makeShader(String modName, String shaderName) throws IOException {
        try (var inputStream = SkiaUtils.class.getResourceAsStream("/assets/" + modName + "/shaders/skia/" + shaderName + ".sksl")) {
            String sksl = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return RuntimeEffect.makeForShader(sksl);
        }
    }

    public static Data createUniformsDataForShader(int sizeInBytes, Consumer<ByteBuffer> addData) {
        var uniformsBuffer = ByteBuffer.allocate(sizeInBytes).order(ByteOrder.nativeOrder());
        addData.accept(uniformsBuffer);

        return Data.makeFromBytes(uniformsBuffer.array());
    }

    public static Paint createPaintForRuntimeEffect(RuntimeEffect runtimeEffect, Data uniforms) {
        try (var shader = runtimeEffect.makeShader(uniforms, null, null)) {
            return new Paint().setShader(shader);
        }
    }

    public static Paint createPaintForColor(java.awt.Color color) {
        return new Paint().setARGB(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Paint createStrokePaint(java.awt.Color color, float strokeWidth) {
        var paint = SkiaUtils.createPaintForColor(color);
        paint.setMode(PaintMode.STROKE);
        paint.setStrokeWidth(strokeWidth);
        return paint;
    }

    //for some reason skia renders text from bottom alignment, not top, so if y = 0, the text will be off screen
    private static float getAdjustedTextYPosition(float y, Font font) {
        return y - font.getMetrics().getAscent();
    }

    public static void drawString(Canvas canvas, String text, float x, float y, Font font, Paint paint) {
        y = getAdjustedTextYPosition(y, font);
        canvas.drawString(text, x, y, font, paint);
    }

    public static void drawStringForMinecraft(CanvasWrapper canvas, String text, float x, float y, Paint paint) {
        SkiaUtils.drawStringForMinecraft(canvas, text, x, y, Fonts.getDefault(), paint, true);
    }

    public static void drawStringForMinecraft(CanvasWrapper canvas, String text, float x, float y, Font font, Paint paint, boolean shadow) {
        if (shadow) {
            float shadowMultiplier = .25f;
            float shadowDrop = .75f;
            float shadowBlur = 0;
            try (Paint shadowPaint = new Paint()) {
                if (paint.getShader() != null) {
                    var whitescaleFilter = ColorFilter.makeLighting(new java.awt.Color((int) (255 * shadowMultiplier), (int) (255 * shadowMultiplier), (int) (255 * shadowMultiplier), paint.getAlpha()).getRGB(), 0);
                    var news = paint.getShader().makeWithColorFilter(whitescaleFilter);
                    shadowPaint.setShader(news);
                    canvas.drawString(text, x + shadowDrop, y + shadowDrop, font, shadowPaint);
                } else {
                    int color = paint.getColor();
                    int red = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;
                    int alpha = (color >> 24) & 0xFF;
                    red *= shadowMultiplier;
                    green *= shadowMultiplier;
                    blue *= shadowMultiplier;
                    int shadowColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    shadowPaint.setImageFilter(ImageFilter.makeDropShadowOnly(shadowDrop, shadowDrop, shadowBlur, shadowBlur, shadowColor));

                    //this does work, :shrug:
                    //shadowPaint.setImageFilter(ImageFilter.makeColorFilter(whitescaleFilter, ImageFilter.makeDropShadowOnly(shadowDrop, shadowDrop, shadowBlur, shadowBlur, color), null));
                    canvas.drawString(text, x, y, font, shadowPaint);
                }
            }
        }
        canvas.drawString(text, x, y, font, paint);
    }

}
