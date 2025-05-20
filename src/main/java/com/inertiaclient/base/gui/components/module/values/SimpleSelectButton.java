package com.inertiaclient.base.gui.components.module.values;

import com.inertiaclient.base.gui.components.SelectButton;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;
import com.inertiaclient.base.value.Value;

import java.util.function.Supplier;

public class SimpleSelectButton extends ValueYogaNode<Value<?>> {

    public SimpleSelectButton(Value<?> value, Supplier<Integer> selectedSize, Runnable onClick) {
        super(value);

        this.styleSetHeight(10);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetAlignItems(AlignItems.CENTER);

        this.addChild(new ValueNameLabel(() -> value.getNameString()));
        this.addChild(new SelectButton(() -> "Select", onClick));
    }
}