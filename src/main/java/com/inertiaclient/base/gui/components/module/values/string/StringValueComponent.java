package com.inertiaclient.base.gui.components.module.values.string;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.impl.ModeValue;

import java.awt.Color;

public class StringValueComponent extends YogaNode {

    private ModeValue.Mode stringIdentifier;
    private StringComponent stringComponent;
    private ModeValue stringValue;

    public StringValueComponent(StringComponent stringComponent, ModeValue stringValue, ModeValue.Mode stringIdentifier) {
        this.stringIdentifier = stringIdentifier;
        this.stringComponent = stringComponent;
        this.stringValue = stringValue;

        this.styleSetHeight(10);
        this.setHoverCursorToIndicateClick();
        //this.setDebug(true);
        this.setDebugColor(UIUtils.colorWithAlpha(Color.red, 50));

        this.setBeforeRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            boolean isHovered = isHoveredAndInsideParent(globalMouseX, globalMouseY);
            if (isHovered) {
                ModernClickGui.MODERN_CLICK_GUI.getTooltip().setTooltip(this, stringIdentifier.getDescriptionString());
            }
        });
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            boolean isSelected = stringValue.getValue() == stringIdentifier;

            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(stringIdentifier.getName(), 1, this.getHeight() / 2, isSelected ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get());
            name.setFontSize(AbstractGroupContainer.valuesFontSize);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            name.draw(canvas);
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                stringValue.setValue(stringIdentifier);
                stringComponent.updateAnimation();
                return true;
            }
            return false;
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var name = CanvasWrapper.getFreshTextBuilder();
            name.setText(stringIdentifier.getName());
            name.setFontSize(AbstractGroupContainer.valuesFontSize);
            this.styleSetWidth(name.getTextWidth() + 2);

            boolean isSelected = stringValue.getValue() == stringIdentifier;
            if (isSelected) {
                stringComponent.setSelectedNode(this);
            }
        });
    }

}
