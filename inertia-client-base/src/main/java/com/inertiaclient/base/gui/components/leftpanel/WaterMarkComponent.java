package com.inertiaclient.base.gui.components.leftpanel;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.toppanel.TopPanel;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.YogaNode;

public class WaterMarkComponent extends YogaNode {

    public WaterMarkComponent() {
        this.styleSetHeight(TopPanel.topPanelHeight);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var inertiaTextBuilder = CanvasWrapper.getFreshTextBuilder();
            inertiaTextBuilder.basic(InertiaBase.CLIENT_NAME, this.getWidth() / 2, this.getHeight() / 2 - 2);
            inertiaTextBuilder.setFontSize(14);
            inertiaTextBuilder.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            inertiaTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            inertiaTextBuilder.draw(canvas);

            var versionTextBuilder = CanvasWrapper.getFreshTextBuilder();
            versionTextBuilder.basic(InertiaBase.VERSION, this.getWidth() / 2, this.getHeight() / 2 + 7).setFontSize(8);
            versionTextBuilder.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            versionTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            versionTextBuilder.draw(canvas);
        });
    }

}
