package com.inertiaclient.base.gui.components.module.values.string;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.ValueNameLabel;
import com.inertiaclient.base.gui.components.module.values.ValueYogaNode;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.impl.ModeValue;
import lombok.Setter;
import org.lwjgl.util.yoga.Yoga;


//TODO: fix overlapping, maybe yoga issue, remove gap, set debug on StringValueComponent and open kill aura you will see the issue
public class StringComponent extends ValueYogaNode<ModeValue> {

    @Setter
    private YogaNode selectedNode;
    private float selectedRenderX;
    private float selectedRenderY;
    private float selectedRenderWidth;
    private float fromX;
    private float fromY;
    private float fromWidth;
    private AnimationValue animation;

    public StringComponent(ModeValue stringValue) {
        super(stringValue);

        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetAlignItems(AlignItems.CENTER);

        this.addChild(new ValueNameLabel(stringValue));

        animation = new AnimationValue();
        animation.setValue(1);

        YogaNode valuesContainer = new YogaNode();
        valuesContainer.styleSetFlexWrap(FlexWrap.WRAP);
        valuesContainer.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        valuesContainer.styleSetGap(GapGutter.COLUMN, 2);
        Yoga.YGNodeStyleSetMaxWidthPercent(valuesContainer.getNativeNode(), 75);
        valuesContainer.styleSetFlexShrink(1);
        valuesContainer.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, valuesContainer.getWidth(), valuesContainer.getHeight(), 1.5f, MainFrame.s_stringValuesBackgroundColor.get());

            {
                float gotoX = selectedNode.getRelativeX();
                float gotoY = selectedNode.getRelativeY();
                float gotoWidth = selectedNode.getWidth();

                this.selectedRenderX = UIUtils.transitionNumber(fromX, gotoX, animation.getValue());
                this.selectedRenderY = UIUtils.transitionNumber(fromY, gotoY, animation.getValue());
                this.selectedRenderWidth = UIUtils.transitionNumber(fromWidth, gotoWidth, animation.getValue());

                canvas.drawRRect(this.selectedRenderX, this.selectedRenderY, this.selectedRenderWidth, selectedNode.getHeight(), 1.5f, MainFrame.s_funColor.get());
            }
        });
        this.addChild(valuesContainer);

        for (ModeValue.Mode stringIdentifier : stringValue.getModes()) {
            valuesContainer.addChild(new StringValueComponent(this, stringValue, stringIdentifier));
        }
    }

    public void updateAnimation() {
        AnimationValue.tweenEngine.cancelTarget(animation);
        this.animation.setValue(0);
        this.animation.to(.75f).value(1).ease(Animations.easeOutBounce).start();

        fromX = selectedRenderX;
        fromY = selectedRenderY;
        fromWidth = selectedRenderWidth;
    }
}
