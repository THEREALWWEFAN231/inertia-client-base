package com.inertiaclient.base.gui.components.module.values.string;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.impl.ModeValue;

public class StringValueComponent extends YogaNode {

    private boolean wasSelected;
    private AnimationValue animation;

    public StringValueComponent(StringComponent stringComponent, ModeValue stringValue, ModeValue.Mode stringIdentifier) {
        {
            boolean isSelected = stringValue.getValue() == stringIdentifier;
            this.animation = new AnimationValue();
            this.animation.setValue(isSelected ? 1 : 0);
            this.wasSelected = isSelected;
        }

        this.styleSetHeight(10);
        this.setHoverCursorToIndicateClick();

        this.setBeforeRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            boolean isHovered = isHoveredAndInsideParent(globalMouseX, globalMouseY);
            if (isHovered) {
                ModernClickGui.MODERN_CLICK_GUI.getTooltip().setTooltip(this, stringIdentifier.getDescriptionString());
            }
        });
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(stringIdentifier.getName(), /*this.getWidth() / 2*/ 1, this.getHeight() / 2, UIUtils.transitionColor(MainFrame.s_unselectedTextColor.get(), MainFrame.s_selectedTextColor.get(), this.animation.getValue()));
            name.setFontSize(AbstractGroupContainer.valuesFontSize);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            //name.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
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
            if (isSelected != this.wasSelected) {
                AnimationValue.tweenEngine.cancelTarget(this.animation);
                this.animation.to(.75f).value(isSelected ? 1 : 0).ease(Animations.easeOutExpo).start();
            }

            this.wasSelected = isSelected;
        });
    }

}
