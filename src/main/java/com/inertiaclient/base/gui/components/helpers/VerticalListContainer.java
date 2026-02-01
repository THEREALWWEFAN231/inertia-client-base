package com.inertiaclient.base.gui.components.helpers;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import lombok.Getter;

public class VerticalListContainer extends YogaNode {

    @Getter
    private YogaNode listNode;

    public VerticalListContainer(float gap) {
        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.enableVerticalScrollbar();
        this.setShouldScissorChildren(true);


        this.listNode = new YogaNode();
        this.listNode.styleSetFlexDirection(FlexDirection.COLUMN);
        this.listNode.styleSetGap(GapGutter.ROW, gap);
        this.listNode.styleSetFlexShrink(0);
        this.listNode.styleSetFlexGrow(0);
        this.addChild(this.listNode);
    }

    public VerticalListContainer() {
        this(5);
    }

    public void addToList(YogaNode yogaNode) {
        this.listNode.addChild(yogaNode);
    }

    public void addToList(YogaNode yogaNode, int atIndex) {
        this.listNode.insertChild(yogaNode, atIndex);
    }

    public void removeFromList(int childIndex) {
        this.listNode.removeChildAtIndex(childIndex);
    }

    public void removeFromList(YogaNode yogaNode) {
        this.listNode.removeChild(yogaNode);
    }

    public int listSize() {
        return this.listNode.getChildren().size();
    }

}
