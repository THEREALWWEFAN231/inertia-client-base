package com.inertiaclient.base.gui.components.toppanel;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.SvgComponent;
import com.inertiaclient.base.render.yoga.AbsoulteYogaNode;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.layouts.ExactPercentAuto;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;

public class CloseButton extends AbsoulteYogaNode {

    public CloseButton() {
        this.setWidth(8);
        this.setHeight(8);
        //this.styleSetPosition(YogaEdge.RIGHT, 0, ExactPercentAuto.PERCENTAGE);
        this.styleSetPosition(YogaEdge.RIGHT, 0, ExactPercentAuto.PERCENTAGE);
        this.styleSetPosition(YogaEdge.TOP, 0, ExactPercentAuto.PERCENTAGE);
        //this.styleSetPosition(YogaEdge.LEFT, 100, ExactPercentAuto.PERCENTAGE);

        SvgComponent svgComponent = new SvgComponent("icb/textures/close.svg");
        this.addChild(svgComponent);

        svgComponent.styleSetMinWidth(8);
        svgComponent.styleSetWidth(8);
        svgComponent.styleSetHeight(8);
        svgComponent.setColor(() -> MainFrame.s_unselectedTextColor.get());
        svgComponent.setHoverCursorToIndicateClick();
        svgComponent.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                InertiaBase.mc.setScreen(null);
                return true;
            }
            return false;
        });
        svgComponent.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT && clickType == ClickType.CLICKED) {
                //don't allow top panel to be clicked/dragged when this was clicked
                return true;
            }
            return false;
        });
    }
}
