package com.inertiaclient.base.gui.components.module.values.number;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.ExactPercentAuto;
import com.inertiaclient.base.value.impl.IntegerValue;
import com.inertiaclient.base.value.impl.NumberValue;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;

public class FloatSlider extends YogaNode {

    private boolean isBeingDragged;
    private float sliderWidth;

    public FloatSlider(NumberValue<Number> numberValue) {
        this.styleSetWidth(50, ExactPercentAuto.PERCENTAGE);
        this.styleSetHeight(12);
        this.setHoverCursorToIndicateClick();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            float max = numberValue.getMaximumValue().floatValue();
            float min = numberValue.getMinimumValue().floatValue();
            this.sliderWidth = this.getWidth() - 3;

            if (isBeingDragged) {
                float clickPercentage = MathHelper.clamp(relativeMouseX / this.sliderWidth, 0, 1);

                float newValue = clickPercentage * (max - min) + min;

                if ((NumberValue) numberValue instanceof IntegerValue) {
                    numberValue.setValue((int) newValue);
                } else {
                    numberValue.setValue(newValue);
                }
            }

            float sliderY = this.height() - 4;

            float progress = ((numberValue.getValue().floatValue() - min) / (max - min)) * this.sliderWidth;
            progress = MathHelper.clamp(progress, 0, this.sliderWidth);
            {
                //track
                canvas.drawRRect(0, sliderY, this.sliderWidth, 2, 1, new Color(183, 183, 183));

                //progress background
                canvas.drawRRect(0, sliderY, progress, 2, 1, MainFrame.s_funColor.get());

                canvas.drawCircle(progress, sliderY + 1, 3, MainFrame.s_funColor.get());
            }

            var value = CanvasWrapper.getFreshTextBuilder();
            value.setText(numberValue.getGuiValueString(numberValue.getValue()));
            value.setY(1);
            value.setFontSize(AbstractGroupContainer.valuesFontSize);
            value.draw(canvas);
        });

        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.CLICKED && button == ButtonIdentifier.LEFT) {
                if (relativeMouseX < this.sliderWidth) {
                    isBeingDragged = true;
                    return true;
                }
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
