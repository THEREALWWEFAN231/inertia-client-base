package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.skia.SvgRenderer;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import io.github.humbleui.skija.Paint;

import java.awt.Color;

public class DeleteButton extends YogaNode {

    public DeleteButton(Runnable onClick) {
        this.styleSetWidth(10);
        this.styleSetHeight(10);
        this.setHoverCursorToIndicateClick();

        SvgRenderer svgRenderer = new SvgRenderer("icb/textures/trash-can.svg");
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            try (Paint paint = SkiaUtils.createPaintForColor(isHoveredAndInsideParent(globalMouseX, globalMouseY) ? MainFrame.s_funColor.get() : Color.white)) {
                svgRenderer.render(canvas, this.getWidth(), this.getHeight(), paint);
            }
        });
        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                onClick.run();
            }
            return true;
        });
    }

}
