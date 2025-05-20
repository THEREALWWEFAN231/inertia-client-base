package com.inertiaclient.base.gui.components.module.values.hashset;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.SelectButton;
import com.inertiaclient.base.gui.components.module.values.ValueNameLabel;
import com.inertiaclient.base.gui.components.module.values.ValueYogaNode;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;
import com.inertiaclient.base.value.HashsetValue;

import java.util.function.Supplier;

public class HashsetValueComponent extends ValueYogaNode<HashsetValue<?>> {

    private HashsetValue<?> hashsetValue;

    public HashsetValueComponent(HashsetValue<?> hashsetValue, Supplier<YogaNode> createPage) {
        super(hashsetValue);
        this.hashsetValue = hashsetValue;

        this.styleSetHeight(10);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetAlignItems(AlignItems.CENTER);

        this.addChild(new ValueNameLabel(() -> hashsetValue.getNameString() + " (" + hashsetValue.getValue().size() + " selected)"));
        this.addChild(new SelectButton(() -> "Select", () -> MainFrame.pageHolder.addPage(new Page(hashsetValue.getName(), createPage.get()))));
    }

    protected YogaNode createAdvancedInfoContainer(float relativeMouseX, float relativeMouseY) {
        return new HashsetAdvancedInfo(this.hashsetValue, () -> this.getGlobalX() + relativeMouseX, () -> this.getGlobalY());
    }

}
