package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.RuntimeEffect;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.io.IOException;

public class SquareColorPicker extends YogaNode {

    private boolean isBeingDragged;

    private static RuntimeEffect colorPickerShader;

    public SquareColorPicker(ColorContainer colorContainer) {
        this.styleSetHeight(60);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (colorPickerShader == null) {
                try {
                    colorPickerShader = SkiaUtils.makeShader("icb", "square_color_picker");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (isBeingDragged) {
                float newSaturation = (globalMouseX - this.getGlobalX()) / this.getWidth();
                newSaturation = MathHelper.clamp(newSaturation, 0, 1);

                float newBrightness = (globalMouseY - this.getGlobalY()) / this.getHeight();
                newBrightness = 1 - newBrightness;
                newBrightness = MathHelper.clamp(newBrightness, 0, 1);

                colorContainer.setRenderedColorSaturation(newSaturation);
                colorContainer.setRenderedColorBrightness(newBrightness);
                colorContainer.updateRenderedColorFromHSB();
            }

            var uniforms = SkiaUtils.createUniformsDataForShader(Float.BYTES * 3, byteBuffer -> {
                byteBuffer.putFloat(this.getWidth());
                byteBuffer.putFloat(this.getHeight());
                byteBuffer.putFloat(colorContainer.getRenderedColorHue());
            });
            try (uniforms; var paint = SkiaUtils.createPaintForRuntimeEffect(SquareColorPicker.colorPickerShader, uniforms)) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
            }


            Color strokeColor = Color.white;
            if (UIUtils.colorToHSL(colorContainer.getRenderedColor())[2] > .75f) {
                strokeColor = Color.black;
            }

            canvas.drawCircle(colorContainer.getRenderedColorSaturation() * this.getWidth(), (1 - colorContainer.getRenderedColorBrightness()) * this.getHeight(), 2.5f, colorContainer.getRenderedColor());
            try (var stroke = SkiaUtils.createStrokePaint(strokeColor, .5f)) {
                canvas.drawCircle(colorContainer.getRenderedColorSaturation() * this.getWidth(), (1 - colorContainer.getRenderedColorBrightness()) * this.getHeight(), 2.5f, stroke);
            }
        });

        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.CLICKED && button == ButtonIdentifier.LEFT) {
                isBeingDragged = true;
                colorContainer.setRenderedRainbow(false);
                return true;
            }
            return false;
        });
        this.setGlobalClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.RELEASED && button == ButtonIdentifier.LEFT) {
                isBeingDragged = false;
            }
            return false;
        });
    }

}