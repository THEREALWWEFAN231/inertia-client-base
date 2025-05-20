package com.inertiaclient.base.gui.components.module.values._boolean;

import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.values.GenericAdvancedInfo;
import com.inertiaclient.base.gui.components.module.values.number.ValueAdvanceInfoContainer;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.value.impl.BooleanValue;

import java.util.function.Supplier;

public class BooleanAdvancedInfo extends ValueAdvanceInfoContainer {

    public BooleanAdvancedInfo(BooleanValue value, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        super(value, xPosition, yPosition);
    }

    @Override
    public YogaNode createContentContainer() {
        YogaNode content = new YogaNode();
        content.styleSetFlexDirection(FlexDirection.COLUMN);
        content.styleSetAlignItems(AlignItems.CENTER);
        content.styleSetGap(GapGutter.ROW, 5);

        {
            YogaNode buttonsContainer = new YogaNode();
            content.addChild(buttonsContainer);

            buttonsContainer.styleSetGap(GapGutter.COLUMN, 5);

            BooleanValue value = (BooleanValue) this.getValue();

            buttonsContainer.addChild(GenericAdvancedInfo.createDefaultButton(this.getValue()).setTooltip(() -> String.valueOf(value.getDefaultValue())));
            buttonsContainer.addChild(new SelectorButton(() -> "False", () -> false, () -> {
                value.setValue(false);
            }));
            buttonsContainer.addChild(new SelectorButton(() -> "True", () -> false, () -> {
                value.setValue(true);
            }));
            buttonsContainer.addChild(new SelectorButton(() -> "Toggle", () -> false, () -> {
                value.setValue(!value.getValue());
            }));
            buttonsContainer.addChild(GenericAdvancedInfo.createCopyButton(this.getValue()));
            buttonsContainer.addChild(GenericAdvancedInfo.createPasteButton(this.getValue()));
        }

        return content;
    }
}
