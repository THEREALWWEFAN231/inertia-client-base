package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.module.values.ValueNameLabel;
import com.inertiaclient.base.gui.components.module.values.ValueYogaNode;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.impl.ColorValue;

public class ColorValueComponent extends ValueYogaNode<ColorValue> {

    public ColorValueComponent(ColorValue colorValue) {
        super(colorValue);

        this.styleSetHeight(10);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetAlignItems(AlignItems.CENTER);

        YogaNode colorDisplay = new YogaNode();
        this.addChild(new ValueNameLabel(colorValue));
        this.addChild(colorDisplay);

        colorDisplay.styleSetWidth(8);
        colorDisplay.styleSetHeight(8);
        colorDisplay.setHoverCursorToIndicateClick();
        colorDisplay.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), 1.5f, colorValue.getValue().getRenderColor());
        });

        colorDisplay.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                WrappedColor wrappedColor = colorValue.getValue();
                ModernClickGui.MODERN_CLICK_GUI.getRoot().addChild(new ColorContainer(wrappedColor, new ColorContainerInterface() {
                    @Override
                    public String getNameHeader() {
                        return colorValue.getNameString();
                    }

                    @Override
                    public WrappedColor getDefault() {
                        return colorValue.getDefaultValue();
                    }

                    @Override
                    public void setColor(WrappedColor wrappedColor) {
                        colorValue.setValue(wrappedColor);
                    }
                }, () -> colorDisplay.getGlobalX() + relativeMouseX, () -> colorDisplay.getGlobalY()));
                this._valueNodeSetShouldShowTooltip(false);
                return true;
            }
            return false;
        });
    }

}
