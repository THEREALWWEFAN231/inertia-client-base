package com.inertiaclient.base.gui.components.module.values.hashset;

import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.values.GenericAdvancedInfo;
import com.inertiaclient.base.gui.components.module.values.number.ValueAdvanceInfoContainer;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.value.HashsetValue;

import java.util.HashSet;
import java.util.function.Supplier;

public class HashsetAdvancedInfo extends ValueAdvanceInfoContainer {

    public HashsetAdvancedInfo(HashsetValue value, Supplier<Float> xPosition, Supplier<Float> yPosition) {
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

            HashsetValue value = (HashsetValue) this.getValue();
            buttonsContainer.addChild(GenericAdvancedInfo.createDefaultButton(this.getValue()));
            buttonsContainer.addChild(new SelectorButton(() -> "Remove all", () -> false, () -> {
                ((HashSet) value.getValue()).clear();
            }));
            buttonsContainer.addChild(GenericAdvancedInfo.createCopyButton(this.getValue()));
            buttonsContainer.addChild(GenericAdvancedInfo.createPasteButton(this.getValue()));
        }

        return content;
    }
}
