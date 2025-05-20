package com.inertiaclient.base.gui.components.tabbedpage;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.FlexWrap;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.utils.UIUtils;
import lombok.Getter;

import java.awt.Color;

public class WrappedListContainer extends YogaNode {

    @Getter
    private YogaNode listNode;

    public WrappedListContainer() {

        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.setShouldScissorChildren(true);
        this.enableVerticalScrollbar();
        this.setDebugColor(UIUtils.colorWithAlpha(Color.RED, 255));

        listNode = new YogaNode();
        listNode.styleSetFlexWrap(FlexWrap.WRAP);
        listNode.styleSetFlexShrink(0);
        listNode.styleSetFlexGrow(0);
        listNode.styleSetGap(GapGutter.ALL, 5);
        this.addChild(listNode);
    }


}
