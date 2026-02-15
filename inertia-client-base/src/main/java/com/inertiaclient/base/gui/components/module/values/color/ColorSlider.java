package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.YogaNode;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Shader;
import net.minecraft.util.Mth;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorSlider extends YogaNode {

    private Supplier<Color> fromColor;
    private Supplier<Color> toColor;

    private boolean isBeingDragged;
    private Consumer<Integer> setColor;

    public ColorSlider(ColorContainer colorContainer, Supplier<Color> fromColor, Supplier<Color> toColor, Consumer<Integer> setColor, Supplier<Integer> getColor) {
        this.setColor = setColor;

        this.styleSetWidth(70);
        this.styleSetHeight(10);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            try (var gradient = Shader.makeLinearGradient(0, 10, this.getWidth(), 10, new int[]{fromColor.get().getRGB(), toColor.get().getRGB()}); var paint = new Paint().setShader(gradient)) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
            }

            canvas.drawRect(0 - .5f + ((getColor.get() / 255f) * this.getWidth()), 0, 1, this.getHeight(), Color.white);
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
                float progress = relativeMouseX / this.getWidth();
                progress = Mth.clamp(progress, 0, 1);
                setColor.accept((int) (progress * 255));
            }
        });
    }

}
