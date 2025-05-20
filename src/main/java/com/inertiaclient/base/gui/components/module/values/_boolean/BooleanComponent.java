package com.inertiaclient.base.gui.components.module.values._boolean;

import com.inertiaclient.base.gui.components.module.ModuleComponent;
import com.inertiaclient.base.gui.components.module.values.ValueNameLabel;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;
import com.inertiaclient.base.utils.UIUtils;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanComponent {

    private AnimationValue toggleAnimation;
    private boolean oldState;

    public BooleanComponent(Supplier<String> nameLabel, Supplier<Boolean> get, Consumer<Boolean> set, YogaNode yogaNode) {
        this.oldState = get.get();
        this.toggleAnimation = new AnimationValue();
        this.toggleAnimation.setValue(this.oldState ? 1 : 0);

        yogaNode.styleSetHeight(10);
        yogaNode.styleSetFlexDirection(FlexDirection.ROW);
        yogaNode.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        yogaNode.styleSetAlignItems(AlignItems.CENTER);

        yogaNode.setDebugColor(UIUtils.colorWithAlpha(Color.red, 50));

        YogaNode switchNode = new YogaNode();
        yogaNode.addChild(new ValueNameLabel(nameLabel));
        yogaNode.addChild(switchNode);

        switchNode.styleSetWidth(16);
        switchNode.styleSetHeight(8);
        switchNode.setHoverCursorToIndicateClick();
        switchNode.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            boolean newState = get.get();
            if (newState != oldState) {
                AnimationValue.tweenEngine.cancelTarget(toggleAnimation);
                toggleAnimation.to(.15f).value(newState ? 1 : 0).start();
                oldState = newState;
            }
            ModuleComponent.drawSwitchAnimation(canvas, 0, 0, switchNode.getWidth(), switchNode.getHeight(), toggleAnimation.getValue());
        });
        switchNode.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                set.accept(!get.get());
                //linear.toggleDirection();
                return true;
            }
            return false;
        });
    }
}
