package com.inertiaclient.base.gui.components.toppanel;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.*;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.CursorUtils;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Path;

public class TopPanel extends YogaNode {

    public static float topPanelHeight = 25;
    private SearchBox searchBox;


    public TopPanel(MainFrame mainFrame) {
        this.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
        this.styleSetHeight(topPanelHeight);
        this.styleSetFlexShrink(0);
        this.styleSetAlignItems(AlignItems.CENTER);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetPadding(YogaEdge.ALL, 5f);
        this.setHoverCursor(CursorUtils.Cursor.RESIZE_ALL);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            try (Path path = new Path(); Paint stroke = SkiaUtils.createStrokePaint(MainFrame.s_lineColor.get(), MainFrame.s_lineWidth.get())) {
                path.moveTo(0, this.getHeight());
                path.lineTo(this.getWidth(), this.getHeight());
                canvas.drawPath(path, stroke);
            }
        }).setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT && clickType == ClickType.CLICKED) {
                mainFrame.setDragPoints(relativeMouseX + (this.getGlobalX() - mainFrame.getGlobalX()), relativeMouseY + (this.getGlobalY() - mainFrame.getGlobalY()));
                return true;
            }
            return false;
        }).setGlobalClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.RELEASED) {
                mainFrame.setBeingDragged(false);
            }
            return false;
        });

        YogaNode left = YogaBuilder.getFreshBuilder(this).setAlignItems(AlignItems.CENTER).setGap(GapGutter.COLUMN, 3).setFlexShrink(0).addChild(new BackButton()).addChild(new PanelText()).build();
        YogaNode right = YogaBuilder.getFreshBuilder(this).addChild(this.searchBox = new SearchBox()).build();
        this.addChild(new CloseButton());
    }

    public void clearSearch() {
        this.searchBox.clearSearch();
    }

}
