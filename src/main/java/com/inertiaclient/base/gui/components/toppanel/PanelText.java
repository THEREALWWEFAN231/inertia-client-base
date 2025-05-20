package com.inertiaclient.base.gui.components.toppanel;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.YogaNode;

public class PanelText extends YogaNode {

    private String currentPanelText;

    public PanelText() {
        this.styleSetHeight(8);

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.currentPanelText = MainFrame.pageHolder.getPages().peek().getLabel().getString();

            var text = CanvasWrapper.getFreshTextBuilder();
            text.setText(this.currentPanelText);
            text.setFontSize(8);

            this.styleSetMinWidth(text.getTextWidth());
        });

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var text = CanvasWrapper.getFreshTextBuilder();
            text.setText(this.currentPanelText);
            text.setFontSize(8);
            text.setColor(MainFrame.s_unselectedTextColor.get());
            text.draw(canvas);
        });
    }

}
