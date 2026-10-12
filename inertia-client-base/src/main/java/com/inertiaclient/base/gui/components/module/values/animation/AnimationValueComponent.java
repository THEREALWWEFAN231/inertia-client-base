package com.inertiaclient.base.gui.components.module.values.animation;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.animation.CustomTweenEquation;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.impl.AnimationValue;

public class AnimationValueComponent extends YogaNode {

    private boolean wasSelected;
    private com.inertiaclient.base.render.animation.AnimationValue animation;

    public AnimationValueComponent(AnimationComponent animationComponent, AnimationValue animationValue, CustomTweenEquation interpolator) {
        {
            boolean isSelected = animationValue.getValue() == interpolator;
            this.animation = new com.inertiaclient.base.render.animation.AnimationValue();
            this.animation.setValue(isSelected ? 1 : 0);
            this.wasSelected = isSelected;
        }

        this.styleSetHeight(10);
        this.setHoverCursorToIndicateClick();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(interpolator.name(), /*this.getWidth() / 2*/ 1, this.getHeight() / 2, UIUtils.transitionColor(MainFrame.s_unselectedTextColor.get(), MainFrame.s_selectedTextColor.get(), this.animation.getValue()));
            name.setFontSize(AbstractGroupContainer.valuesFontSize);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            //name.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            name.draw(canvas);
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                animationValue.setValue(interpolator);
                animationComponent.updateAnimation();
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
            if (isSelected != this.wasSelected) {
                com.inertiaclient.base.render.animation.AnimationValue.tweenEngine.cancelTarget(this.animation);
                this.animation.to(.75f).value(isSelected ? 1 : 0).ease(Animations.easeOutExpo).start();
            }

            this.wasSelected = isSelected;
        });
    }

}
