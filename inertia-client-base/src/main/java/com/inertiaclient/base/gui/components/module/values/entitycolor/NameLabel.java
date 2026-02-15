package com.inertiaclient.base.gui.components.module.values.entitycolor;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.YogaNode;

import java.util.function.Supplier;

public class NameLabel extends YogaNode {

    private Supplier<String> label;

    public NameLabel(Supplier<String> label, YogaNode toHover) {
        this.label = label;

        this.styleSetHeight(AbstractGroupContainer.valuesFontSize);
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.basic(label.get(), 0, 0, toHover.isHoveredAndInsideParent(globalMouseX, globalMouseY) ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get());
            nameTextBuilder.setFontSize(8);
            nameTextBuilder.draw(canvas);
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.setText(label.get());
            nameTextBuilder.setFontSize(8);

            this.styleSetWidth(nameTextBuilder.getTextWidth());
        });
    }

}
