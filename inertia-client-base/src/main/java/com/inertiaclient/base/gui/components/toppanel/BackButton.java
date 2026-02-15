package com.inertiaclient.base.gui.components.toppanel;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.skia.SvgRenderer;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import org.lwjgl.glfw.GLFW;

public class BackButton extends YogaNode {

    public BackButton() {
        SvgRenderer svgRenderer = new SvgRenderer("icb/textures/left-arrow.svg");

        this.styleSetWidth(16);
        this.styleSetHeight(16);
        this.setHoverCursorToIndicateClick();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            try (Paint paint = SkiaUtils.createPaintForColor(MainFrame.s_stringValuesBackgroundColor.get()); Paint stroke = SkiaUtils.createStrokePaint(MainFrame.s_selectorButtonOutlineColor.get(), MainFrame.s_lineWidth.get())) {
                canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, paint);
                canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, stroke);
            }

            float iconWidth = this.getWidth() * .66f;
            float iconHeight = this.getHeight() * .66f;
            try (Paint paint = SkiaUtils.createPaintForColor(MainFrame.s_unselectedTextColor.get())) {
                svgRenderer.render(canvas, UIUtils.getCenterOffset(iconWidth, this.getWidth()), UIUtils.getCenterOffset(iconHeight, this.getHeight()), iconWidth, iconHeight, paint);
            }
        }).setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                //dont allow top panel to be clicked/dragged when this was clicked
                return true;
            }
            return false;
        }).setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                MainFrame.pageHolder.goBack();
                return true;
            }
            return false;
        }).setKeyPressedCallback((keyCode, scanCode, modifiers) -> {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                if (MainFrame.pageHolder.goBack()) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    protected boolean implIsVisible() {
        return MainFrame.pageHolder.canGoBack();
    }

}
