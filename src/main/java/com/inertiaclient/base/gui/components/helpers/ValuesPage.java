package com.inertiaclient.base.gui.components.helpers;

import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.gui.components.module.values.ValueGroupButton;
import com.inertiaclient.base.gui.components.module.values.ValuesGroupContainer;
import com.inertiaclient.base.render.yoga.YogaBuilder;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.value.group.ButtonValueGroup;
import com.inertiaclient.base.value.group.ValueGroup;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ValuesPage extends YogaNode {

    @Getter
    private YogaNode allGroups;

    public ValuesPage(@Nullable List<ValueGroup> valueGroups) {
        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);

        allGroups = YogaBuilder.getFreshBuilder(this).setFlexGrow(0).setFlexShrink(0).setFlexDirection(FlexDirection.COLUMN).setGap(GapGutter.ROW, AbstractGroupContainer.gap).build();
        if (valueGroups != null) {
            for (ValueGroup valueGroup : valueGroups) {
                if (valueGroup instanceof ButtonValueGroup bvg) {
                    allGroups.addChild(new ValueGroupButton(bvg));
                    continue;
                }

                this.addGroupDirect(valueGroup);
            }
        }

        this.enableVerticalScrollbar();
        this.setShouldScissorChildren(true);
    }

    public void addGroupDirect(ValueGroup valueGroup) {
        allGroups.addChild(new ValuesGroupContainer(valueGroup));
    }

}