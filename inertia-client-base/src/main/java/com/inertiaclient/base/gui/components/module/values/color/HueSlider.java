package com.inertiaclient.base.gui.components.module.values.color;


import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.YogaNode;
import io.github.humbleui.skija.RuntimeEffect;
import net.minecraft.util.Mth;

import java.awt.Color;
import java.io.IOException;

public class HueSlider extends YogaNode {

    private ColorContainer colorContainer;
    private boolean isBeingDragged;

    private static RuntimeEffect hueRectShader;

    public HueSlider(ColorContainer colorContainer) {
        this.colorContainer = colorContainer;

        this.styleSetHeight(10);
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (hueRectShader == null) {
                try {
                    hueRectShader = SkiaUtils.makeShader("icb", "hue_rect");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            var uniforms = SkiaUtils.createUniformsDataForShader(Float.BYTES * 1, byteBuffer -> {
                byteBuffer.putFloat(this.getWidth());
            });
            try (uniforms; var paint = SkiaUtils.createPaintForRuntimeEffect(HueSlider.hueRectShader, uniforms)) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
            }
            canvas.drawRect(0 - .5f + (colorContainer.getRenderedColorHue() * this.getWidth()), 0, 1, this.getHeight(), Color.white);
        });
        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.CLICKED && button == ButtonIdentifier.LEFT) {
                this.isBeingDragged = true;
                colorContainer.setRenderedRainbow(false);
                return true;
            }
            return false;
        });
        this.setGlobalClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.RELEASED && button == ButtonIdentifier.LEFT) {
                this.isBeingDragged = false;
            }
            return false;
        });
        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (isBeingDragged) {
                float newHue = relativeMouseX / this.getWidth();
                newHue = Mth.clamp(newHue, 0, 1);
                colorContainer.setRenderedColorHue(newHue);
                colorContainer.updateRenderedColorFromHSB();
            }
        });
    }

}
