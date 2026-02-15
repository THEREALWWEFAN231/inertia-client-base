package com.inertiaclient.base.gui.components.module.values.number;

import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.values.GenericAdvancedInfo;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.value.impl.NumberValue;

import java.util.function.Supplier;

public class FloatAdvancedInfo extends ValueAdvanceInfoContainer {

    public FloatAdvancedInfo(NumberValue<Number> value, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        super(value, xPosition, yPosition);
    }

    @Override
    public YogaNode createContentContainer() {
        YogaNode content = new YogaNode();
        content.styleSetFlexDirection(FlexDirection.COLUMN);
        content.styleSetAlignItems(AlignItems.CENTER);
        content.styleSetGap(GapGutter.ROW, 5);

        content.addChild(new ModernTextBox((NumberValue<Number>) this.getValue()));

        {
            YogaNode buttonsContainer = new YogaNode();
            content.addChild(buttonsContainer);

            buttonsContainer.styleSetGap(GapGutter.COLUMN, 5);

            NumberValue<Number> value = (NumberValue<Number>) this.getValue();
            buttonsContainer.addChild(GenericAdvancedInfo.createDefaultButton(this.getValue()).setTooltip(() -> String.valueOf(value.getDefaultValue())));
            buttonsContainer.addChild(new SelectorButton(() -> "Min", () -> false, () -> {
                value.setValue(value.getMinimumValue());
            }).setTooltip(() -> String.valueOf(value.getMinimumValue())));
            buttonsContainer.addChild(new SelectorButton(() -> "Max", () -> false, () -> {
                value.setValue(value.getMaximumValue());
            }).setTooltip(() -> String.valueOf(value.getMaximumValue())));
            buttonsContainer.addChild(GenericAdvancedInfo.createCopyButton(this.getValue()));
            buttonsContainer.addChild(GenericAdvancedInfo.createPasteButton(this.getValue()));
        }

        return content;
    }
}
