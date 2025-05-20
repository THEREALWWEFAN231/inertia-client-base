package com.inertiaclient.base.gui;

import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.AbsoulteYogaNode;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.PositionType;
import com.inertiaclient.base.utils.TimerUtil;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;

public class Tooltip extends AbsoulteYogaNode {

    @Setter
    private String label;

    @Setter
    private float tooltipMiddleX;
    @Setter
    private float tooltipY;

    private TimerUtil showTimer = new TimerUtil();
    private YogaNode lastTooltipNode;
    private YogaNode currentTooltipNode;
    private long tooltipDisplayDelay;

    public Tooltip() {
        this.styleSetPositionType(PositionType.ABSOLUTE);
        this.setHeight(12);

        this.setBeforeRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            if (currentTooltipNode != lastTooltipNode) {
                this.showTimer.reset();
            }
            lastTooltipNode = currentTooltipNode;
            this.showTimer.update();
            if (!(this.showTimer.hasDelayRun(this.tooltipDisplayDelay) || Screen.hasShiftDown())) {
                this.label = null;
            }

            if (label == null) {
                return;
            }
            float width = CanvasWrapper.getFreshTextBuilder().setText(label).getTextWidth() + 6;
            this.setWidth(width);
            this.setX(this.tooltipMiddleX - (width / 2));
            this.setY(this.tooltipY);

            this.setX(globalMouseX - (width / 2));
            this.setY(this.tooltipY);

            currentTooltipNode = null;
        });

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (label == null) {
                return;
            }

            canvas.drawTooltip(0, 0, this.getWidth(), this.getHeight(), 3, this.getWidth() / 2, 5);

            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(label, this.getWidth() / 2, this.getHeight() / 2);
            name.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            name.draw(canvas);

            label = null;
        });

    }


    public void setTooltip(YogaNode yogaNode, String label, long tooltipDisplayDelay) {
        this.label = label;
        if (label == null) {
            return;
        }
        //System.out.println(yogaNode + ":" + tooltipDisplayDelay);
        this.currentTooltipNode = yogaNode;
        this.tooltipDisplayDelay = tooltipDisplayDelay;
        this.tooltipMiddleX = (yogaNode.getGlobalX() + (yogaNode.getWidth() / 2));
        this.tooltipY = (yogaNode.getGlobalY() - 15);
    }

    public void setTooltip(YogaNode yogaNode, String label) {
        this.setTooltip(yogaNode, label, 1500);
    }

}
