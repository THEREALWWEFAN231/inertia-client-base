package com.inertiaclient.base.gui.components.leftpanel;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.skia.SvgRenderer;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.ExactPercentAuto;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.Supplier;

public class PageButton extends YogaNode {

    private Supplier<Page> createPagePanel;

    public PageButton(String icon, String labelId, Supplier<Page> createPagePanel, int index) {
        this.createPagePanel = createPagePanel;
        SvgRenderer svgRenderer = new SvgRenderer(icon);

        this.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
        this.styleSetHeight(16);
        this.setHoverCursorToIndicateClick();

        Component label = Component.translatable("icb.gui.pages." + labelId + ".name");
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            boolean isSelected = MainFrame.leftPanel.pages.getSelectedPageIndex() == index;
            if (isSelected) {
                try (Paint paint = SkiaUtils.createPaintForColor(MainFrame.s_funColor.get())) {
                    canvas.drawRect(Rect.makeXYWH(0, 0, this.getWidth(), this.getHeight()), paint);
                }
            }

            Color textColor = isSelected ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get();

            {
                float imageWidth = 8;
                float imageHeight = 8;

                float imageX = 4;
                float imageY = (this.getHeight()) / 2 - imageHeight / 2;
                svgRenderer.render(canvas, imageX, imageY, imageWidth, imageHeight);
            }

            var labelTextBuilder = CanvasWrapper.getFreshTextBuilder();
            labelTextBuilder.basic(label, 15, this.getHeight() / 2 + 1);
            labelTextBuilder.setFontSize(7);
            labelTextBuilder.setColor(textColor);
            labelTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            labelTextBuilder.draw(canvas);
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                if (MainFrame.leftPanel.pages.getSelectedPageIndex() != index) {
                    MainFrame.pageHolder.addPage(createPagePanel.get());
                    MainFrame.pageHolder.removeHistory();
                    MainFrame.leftPanel.pages.setSelectedPageIndex(index);
                }
                return true;
            }
            return false;
        });
    }

    public Page createPage() {
        return this.createPagePanel.get();
    }


}
