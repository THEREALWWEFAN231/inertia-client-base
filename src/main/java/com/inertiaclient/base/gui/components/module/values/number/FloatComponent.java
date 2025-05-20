package com.inertiaclient.base.gui.components.module.values.number;

import com.inertiaclient.base.gui.components.module.values.ValueNameLabel;
import com.inertiaclient.base.gui.components.module.values.ValueYogaNode;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;
import com.inertiaclient.base.value.impl.NumberValue;

public class FloatComponent extends ValueYogaNode<NumberValue<?>> {

    private NumberValue<Number> numberValue;

    public FloatComponent(NumberValue<Number> numberValue) {
        super(numberValue);
        this.numberValue = numberValue;

        this.styleSetHeight(12);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetAlignItems(AlignItems.CENTER);


        this.addChild(new ValueNameLabel(numberValue));
        this.addChild(new FloatSlider(numberValue));
    }

    protected YogaNode createAdvancedInfoContainer(float relativeMouseX, float relativeMouseY) {
        return new FloatAdvancedInfo(this.numberValue, () -> this.getGlobalX() + relativeMouseX, () -> this.getGlobalY());
    }

}
