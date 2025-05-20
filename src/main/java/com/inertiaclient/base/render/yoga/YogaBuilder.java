package com.inertiaclient.base.render.yoga;

import com.inertiaclient.base.render.yoga.layouts.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter
public class YogaBuilder {

    private static final YogaBuilder YOGA_BUILDER = new YogaBuilder();

    public static YogaBuilder getFreshBuilder() {
        return YOGA_BUILDER.setDefaults();
    }

    public static YogaBuilder getFreshBuilder(YogaNode parent) {
        return getFreshBuilder().setParent(parent);
    }

    private YogaNode yogaNode;


    private YogaBuilder() {
        this.setDefaults();
    }

    public YogaBuilder setDefaults() {
        this.yogaNode = new YogaNode();

        return this;
    }

    public YogaBuilder setParent(YogaNode parent) {
        parent.addChild(this.yogaNode);
        return this;
    }

    public YogaBuilder setPosition(YogaEdge positionEdge, float position, ExactPercentAuto unit) {
        this.yogaNode.styleSetPosition(positionEdge, position, unit);
        return this;
    }

    public YogaBuilder setPosition(YogaEdge positionEdge, float position) {
        this.yogaNode.styleSetPosition(positionEdge, position);
        return this;
    }

    public YogaBuilder setPositionType(PositionType positionType) {
        this.yogaNode.styleSetPositionType(positionType);
        return this;
    }

    public YogaBuilder setFlexDirection(FlexDirection flexDirection) {
        this.yogaNode.styleSetFlexDirection(flexDirection);
        return this;
    }

    public YogaBuilder setAlignItems(AlignItems alignItems) {
        this.yogaNode.styleSetAlignItems(alignItems);
        return this;
    }

    public YogaBuilder setJustifyContent(JustifyContent justifyContent) {
        this.yogaNode.styleSetJustifyContent(justifyContent);
        return this;
    }

    public YogaBuilder setFlexWrap(FlexWrap flexWrap) {
        this.yogaNode.styleSetFlexWrap(flexWrap);
        return this;
    }

    public YogaBuilder setDisplay(Display display) {
        this.yogaNode.styleSetDisplay(display);
        return this;
    }

    public YogaBuilder setAlignSelf(AlignItems alignSelf) {
        this.yogaNode.styleSetAlignSelf(alignSelf);
        return this;
    }

    public YogaBuilder setFlexGrow(float flexGrow) {
        this.yogaNode.styleSetFlexGrow(flexGrow);
        return this;
    }

    public YogaBuilder setFlexShrink(float flexShrink) {
        this.yogaNode.styleSetFlexShrink(flexShrink);
        return this;
    }

    public YogaBuilder setGap(GapGutter gapGutter, float gap, ExactPercentAuto unit) {
        this.yogaNode.styleSetGap(gapGutter, gap, unit);
        return this;
    }

    public YogaBuilder setGap(GapGutter gapGutter, float gap) {
        this.yogaNode.styleSetGap(gapGutter, gap);
        return this;
    }

    public YogaBuilder setPadding(YogaEdge paddingEdge, float padding) {
        this.yogaNode.styleSetPadding(paddingEdge, padding);
        return this;
    }

    public YogaBuilder setMargin(YogaEdge marginEdge, float margin) {
        this.yogaNode.styleSetMargin(marginEdge, margin);
        return this;
    }

    public YogaBuilder setBorder(YogaEdge borderEdge, float border) {
        this.yogaNode.styleSetBorder(borderEdge, border);
        return this;
    }

    public YogaBuilder setWidth(float width) {
        this.yogaNode.styleSetWidth(width);
        return this;
    }

    public YogaBuilder setWidth(float width, ExactPercentAuto unit) {
        this.yogaNode.styleSetWidth(width, unit);
        return this;
    }

    public YogaBuilder setMinWidth(float width) {
        this.yogaNode.styleSetMinWidth(width);
        return this;
    }

    public YogaBuilder setMinWidth(float width, ExactPercentAuto unit) {
        this.yogaNode.styleSetMinWidth(width, unit);
        return this;
    }

    public YogaBuilder setMaxWidth(float width) {
        this.yogaNode.styleSetMaxWidth(width);
        return this;
    }

    public YogaBuilder setMaxWidth(float width, ExactPercentAuto unit) {
        this.yogaNode.styleSetMaxWidth(width, unit);
        return this;
    }


    public YogaBuilder setHeight(float height) {
        this.yogaNode.styleSetHeight(height);
        return this;
    }

    public YogaBuilder setHeight(float height, ExactPercentAuto unit) {
        this.yogaNode.styleSetHeight(height, unit);
        return this;
    }

    public YogaBuilder setMinHeight(float height) {
        this.yogaNode.styleSetMinHeight(height);
        return this;
    }

    public YogaBuilder setMinHeight(float height, ExactPercentAuto unit) {
        this.yogaNode.styleSetMinHeight(height, unit);
        return this;
    }

    public YogaBuilder setMaxHeight(float height) {
        this.yogaNode.styleSetMaxHeight(height);
        return this;
    }

    public YogaBuilder setMaxHeight(float height, ExactPercentAuto unit) {
        this.yogaNode.styleSetMaxHeight(height, unit);
        return this;
    }

    public YogaBuilder applyGenericStyle(GenericStyle genericStyle) {
        this.yogaNode.applyGenericStyle(genericStyle);
        return this;
    }

    public YogaBuilder addChild(YogaNode child) {
        if (child instanceof AbsoulteYogaNode) {
            this.yogaNode.addAbsoluteChild(child);
        } else {
            this.yogaNode.addChild(child);
        }
        return this;
    }

    public YogaNode build() {
        return this.yogaNode;
    }

}
