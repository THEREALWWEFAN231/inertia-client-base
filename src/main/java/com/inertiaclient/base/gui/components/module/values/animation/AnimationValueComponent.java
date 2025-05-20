package com.inertiaclient.base.gui.components.module.values.animation;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.animation.CustomTweenEquation;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.impl.AnimationValue;

import java.awt.Color;

public class AnimationValueComponent extends YogaNode {

    private CustomTweenEquation interpolator;
    AnimationComponent animationComponent;
    AnimationValue animationValue;

    public AnimationValueComponent(AnimationComponent stringComponent, AnimationValue animationValue, CustomTweenEquation interpolator) {
        this.interpolator = interpolator;
        this.animationComponent = stringComponent;
        this.animationValue = animationValue;

        this.styleSetHeight(10);
        //this.setDebug(true);
        this.setDebugColor(UIUtils.colorWithAlpha(Color.red, 50));

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            boolean isSelected = animationValue.getValue() == interpolator;

            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.basic(interpolator.name(), 1, this.getHeight() / 2, isSelected ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get());
            nameTextBuilder.setFontSize(AbstractGroupContainer.valuesFontSize);
            nameTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            nameTextBuilder.draw(canvas);
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                animationValue.setValue(interpolator);
                stringComponent.updateAnimation();
                return true;
            }
            return false;
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.setText(interpolator.name());
            nameTextBuilder.setFontSize(AbstractGroupContainer.valuesFontSize);
            this.styleSetWidth(nameTextBuilder.getTextWidth() + 2);

            boolean isSelected = animationValue.getValue() == interpolator;
            if (isSelected) {
                animationComponent.setSelectedNode(this);
            }
        });
    }

}
