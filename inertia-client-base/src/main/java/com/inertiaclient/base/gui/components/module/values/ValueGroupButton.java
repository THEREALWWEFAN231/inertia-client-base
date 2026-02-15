package com.inertiaclient.base.gui.components.module.values;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.SelectButton;
import com.inertiaclient.base.gui.components.helpers.ValuesPage;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;
import com.inertiaclient.base.value.group.ButtonValueGroup;

public class ValueGroupButton extends YogaNode {

    public ValueGroupButton(ButtonValueGroup buttonValueGroup) {
        this.styleSetHeight(10);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetAlignItems(AlignItems.CENTER);

        this.addChild(new ValueNameLabel(buttonValueGroup.getName()));

        this.addChild(new SelectButton(() -> "edit", () -> { //if we just pass buttonValueGroup to the values page we will get an infinte loop, values page will just recreate this button, but if we add it "directly" it will ignore this button
            var valuesPage = new ValuesPage(null);
            valuesPage.addGroupDirect(buttonValueGroup);
            MainFrame.pageHolder.addPage(new Page(buttonValueGroup.getName(), valuesPage));
        }));
    }

}
