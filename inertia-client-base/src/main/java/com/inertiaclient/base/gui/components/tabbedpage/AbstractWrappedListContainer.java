package com.inertiaclient.base.gui.components.tabbedpage;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.FlexWrap;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.utils.UIUtils;
import lombok.Getter;

import java.awt.Color;

public abstract class AbstractWrappedListContainer<T extends YogaNode> extends YogaNode {

    @Getter
    private T listNode;

    public AbstractWrappedListContainer() {

        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.setShouldScissorChildren(true);
        this.enableVerticalScrollbar();
        this.setDebugColor(UIUtils.colorWithAlpha(Color.RED, 255));

        this.listNode = this.createListNodeType();
        this.listNode.styleSetFlexWrap(FlexWrap.WRAP);
        this.listNode.styleSetFlexShrink(0);
        this.listNode.styleSetFlexGrow(0);
        this.listNode.styleSetGap(GapGutter.ALL, 5);
        this.addChild(listNode);
    }

    public abstract T createListNodeType();

}
