package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.yoga.*;
import com.inertiaclient.base.render.yoga.layouts.PositionType;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;
import net.minecraft.client.gui.GuiGraphics;

public class HorizontalScrollbar extends AbsoulteYogaNode {

    public float startBeingDraggedY;
    private boolean isBeingDragged;
    private boolean isAbleToScroll;//used when there isn't enough content to scroll


    private float contentsAllowedHeight;
    private float contentHeight;
    private float overflowAmount;

    private boolean isMultiplierEnable;
    private float multiplier;
    private float totalScrollingDistance;
    private float minHeight = 10;

    public HorizontalScrollbar() {
        this.styleSetPositionType(PositionType.ABSOLUTE);
        this.styleSetPosition(YogaEdge.RIGHT, 0);
        this.styleSetHeight(2);
        this.styleSetWidth(30);
        this.setHeight(2);

        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT && clickType == ClickType.CLICKED && this.isAbleToScroll) {
                this.startBeingDraggedY = relativeMouseX;
                this.isBeingDragged = true;
                return true;
            }
            return false;
        });
        this.setGlobalClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.RELEASED) {
                this.isBeingDragged = false;
            }
            return false;
        });
        this.applyGenericStyle(new GenericStyle().setRenderIf(() -> this.isAbleToScroll).setBackgroundColor(() -> VerticalScrollbar.scrollbarColor));
    }

    public void setGlobalPositions(float currentX, float currentY, GuiGraphics context, float globalMouseX, float globalMouseY, float delta) {
        this.contentsAllowedHeight = this.getParent().getWidth();
        this.contentHeight = this.getParentsContentHeight();
        this.overflowAmount = this.contentHeight - this.contentsAllowedHeight;

        if (this.overflowAmount > 0) {
            this.isAbleToScroll = true;
        } else {
            this.isAbleToScroll = false;
            this.setParentScrollOffset(0);
            this.setX(0);
            return;
        }

        float scrollbarHeight = this.contentsAllowedHeight - (this.contentHeight - this.contentsAllowedHeight);

        this.isMultiplierEnable = false;
        if (scrollbarHeight < this.minHeight) {
            scrollbarHeight = this.minHeight;
            this.isMultiplierEnable = true;
        }

        this.setY(this.getParent().getHeight() - this.getHeight());
        this.setWidth(scrollbarHeight);

        this.totalScrollingDistance = this.contentsAllowedHeight - this.minHeight;
        this.multiplier = this.isMultiplierEnable ? (this.contentHeight - this.contentsAllowedHeight) / this.totalScrollingDistance : 1;
        if (this.isBeingDragged) {
            float scrollOffset = this.getParent().getGlobalX() - globalMouseX + this.startBeingDraggedY;
            scrollOffset *= this.multiplier;
            this.setScrollOffset(scrollOffset);
        } else {
            float scrollOffset = this.getParent().getChildrenScrollXOffset();
            this.setScrollOffset(scrollOffset);
        }

        super.setGlobalPositions(currentX, currentY, context, globalMouseX, globalMouseY, delta);
    }

    private float getParentsContentHeight() {
        float contentHeight = 0;
        for (YogaNode yogaNode : this.getParent().getChildren()) {
            if (!(yogaNode instanceof AbsoulteYogaNode)) {
                contentHeight += yogaNode.getWidth();
            }
        }
        return contentHeight;
    }

    private void setParentScrollOffset(float scrollOffset) {
        this.getParent().setChildrenScrollXOffset(scrollOffset);
    }

    private void setScrollOffset(float scrollOffset) {
        if (scrollOffset > 0) {
            scrollOffset = 0;
        }
        if (Math.abs(scrollOffset) > this.overflowAmount) {
            scrollOffset = -this.overflowAmount;
        }
        this.setParentScrollOffset(scrollOffset);
        this.setX((scrollOffset * -1) / this.multiplier);
    }

    public boolean scroll(float amount) {
        if (!isAbleToScroll) {
            return false;
        }

        float scrollOffset = this.getParent().getChildrenScrollXOffset();
        boolean wasScrolledAllTheWay = scrollOffset > 0 || Math.abs(scrollOffset) > this.overflowAmount;
        if (wasScrolledAllTheWay) {
            return false;
        }
        this.setScrollOffset(scrollOffset + (amount * 75));
        return true;
    }


}
