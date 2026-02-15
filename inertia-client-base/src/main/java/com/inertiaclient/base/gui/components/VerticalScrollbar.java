package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.yoga.*;
import com.inertiaclient.base.render.yoga.layouts.PositionType;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.Color;

public class VerticalScrollbar extends AbsoulteYogaNode {

    //maybe ModuleComponent.switchBackgroundColor?
    public static Color scrollbarColor = new Color(255, 255, 255, 157);//95 opacity
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

    public VerticalScrollbar() {
        this.styleSetPositionType(PositionType.ABSOLUTE);
        this.styleSetPosition(YogaEdge.RIGHT, 0);
        this.styleSetWidth(2);
        this.styleSetHeight(30);
        this.setWidth(2);

        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT && clickType == ClickType.CLICKED && this.isAbleToScroll) {
                this.startBeingDraggedY = relativeMouseY;
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
        /*this.setBeforeRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta) -> {
            this.contentsAllowedHeight = this.getParent().getHeight();
            this.contentHeight = this.getParentsContentHeight();
            this.overflowAmount = this.contentHeight - this.contentsAllowedHeight;

            if (this.overflowAmount > 0) {
                this.isAbleToScroll = true;
            } else {
                this.isAbleToScroll = false;
                this.setParentScrollOffset(0);
                this.setY(0);
                return;
            }

            float scrollbarHeight = this.contentsAllowedHeight - (this.contentHeight - this.contentsAllowedHeight);

            this.isMultiplierEnable = false;
            if (scrollbarHeight < this.minHeight) {
                scrollbarHeight = this.minHeight;
                this.isMultiplierEnable = true;
            }

            this.setX(this.getParent().getWidth() - this.getWidth());
            this.setHeight(scrollbarHeight);

            this.totalScrollingDistance = this.contentsAllowedHeight - this.minHeight;
            this.multiplier = this.isMultiplierEnable ? (this.contentHeight - this.contentsAllowedHeight) / this.totalScrollingDistance : 1;
            if (this.isBeingDragged) {
                float scrollOffset = this.getParent().getGlobalY() - globalMouseY + this.startBeingDraggedY;
                scrollOffset *= this.multiplier;
                this.setScrollOffset(scrollOffset);
            } else {
                float scrollOffset = this.getParent().getChildrenScrollYOffset();
                this.setScrollOffset(scrollOffset);
            }
        });*/

        this.applyGenericStyle(new GenericStyle().setRenderIf(() -> this.isAbleToScroll).setBackgroundColor(() -> VerticalScrollbar.scrollbarColor));
    }

    public void setGlobalPositions(float currentX, float currentY, GuiGraphics context, float globalMouseX, float globalMouseY, float delta) {
        this.contentsAllowedHeight = this.getParent().getHeight();
        this.contentHeight = this.getParentsContentHeight();
        this.overflowAmount = this.contentHeight - this.contentsAllowedHeight;

        if (this.overflowAmount > 0) {
            this.isAbleToScroll = true;
        } else {
            this.isAbleToScroll = false;
            this.setParentScrollOffset(0);
            this.setY(0);
            return;
        }

        float scrollbarHeight = this.contentsAllowedHeight - (this.contentHeight - this.contentsAllowedHeight);

        this.isMultiplierEnable = false;
        if (scrollbarHeight < this.minHeight) {
            scrollbarHeight = this.minHeight;
            this.isMultiplierEnable = true;
        }

        this.setX(this.getParent().getWidth() - this.getWidth());
        this.setHeight(scrollbarHeight);

        this.totalScrollingDistance = this.contentsAllowedHeight - this.minHeight;
        this.multiplier = this.isMultiplierEnable ? (this.contentHeight - this.contentsAllowedHeight) / this.totalScrollingDistance : 1;
        if (this.isBeingDragged) {
            float scrollOffset = this.getParent().getGlobalY() - globalMouseY + this.startBeingDraggedY;
            scrollOffset *= this.multiplier;
            this.setScrollOffset(scrollOffset);
        } else {
            float scrollOffset = this.getParent().getChildrenScrollYOffset();
            this.setScrollOffset(scrollOffset);
        }

        super.setGlobalPositions(currentX, currentY, context, globalMouseX, globalMouseY, delta);
    }

    private float getParentsContentHeight() {
        float contentHeight = 0;
        for (YogaNode yogaNode : this.getParent().getChildren()) {
            if (!(yogaNode instanceof AbsoulteYogaNode)) {
                contentHeight += yogaNode.getHeight();
            }
        }
        return contentHeight;
    }

    private void setParentScrollOffset(float scrollOffset) {
        this.getParent().setChildrenScrollYOffset(scrollOffset);
    }

    private void setScrollOffset(float scrollOffset) {
        if (scrollOffset > 0) {
            scrollOffset = 0;
        }
        if (Math.abs(scrollOffset) > this.overflowAmount) {
            scrollOffset = -this.overflowAmount;
        }
        this.setParentScrollOffset(scrollOffset);
        this.setY((scrollOffset * -1) / this.multiplier);
    }

    public boolean scroll(float amount) {
        if (!isAbleToScroll) {
            return false;
        }

        float scrollOffset = this.getParent().getChildrenScrollYOffset();
        boolean wasScrolledAllTheWay = scrollOffset > 0 || Math.abs(scrollOffset) > this.overflowAmount;
        if (wasScrolledAllTheWay) {
            return false;
        }
        this.setScrollOffset(scrollOffset + (amount * 75));
        return true;
    }


}
