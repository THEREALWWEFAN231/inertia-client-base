package com.inertiaclient.base.gui.components.leftpanel;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.ExactPercentAuto;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Path;

//https://yogalayout.com/playground/?eyJ3aWR0aCI6NTAwLCJoZWlnaHQiOjUwMCwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiY2hpbGRyZW4iOlt7IndpZHRoIjoiMjAwIiwiaGVpZ2h0IjoiNDAwIiwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwiZmxleERpcmVjdGlvbiI6MCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiY2hpbGRyZW4iOlt7IndpZHRoIjoiIiwiaGVpZ2h0IjoiMTAwIiwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiZmxleEdyb3ciOiIwIn0seyJ3aWR0aCI6IiIsImhlaWdodCI6IiIsIm1pbldpZHRoIjpudWxsLCJtaW5IZWlnaHQiOm51bGwsIm1heFdpZHRoIjpudWxsLCJtYXhIZWlnaHQiOm51bGwsInBvc2l0aW9uIjp7InRvcCI6bnVsbCwicmlnaHQiOm51bGwsImJvdHRvbSI6bnVsbCwibGVmdCI6bnVsbH0sImZsZXhHcm93IjoiMSJ9XX1dfQ==
public class LeftPanel extends YogaNode {

    public Pages pages;

    public LeftPanel() {
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.styleSetWidth(33.333f, ExactPercentAuto.PERCENTAGE);
        this.styleSetHeight(100, ExactPercentAuto.PERCENTAGE);
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            try (Path path = new Path(); Paint stroke = SkiaUtils.createStrokePaint(MainFrame.s_lineColor.get(), MainFrame.s_lineWidth.get())) {
                path.moveTo(this.getWidth(), 0);
                path.lineTo(this.getWidth(), this.getHeight());
                canvas.drawPath(path, stroke);
            }
        });


        this.addChild(new WaterMarkComponent());

        YogaNode restOfSpace = new YogaNode();
        this.addChild(restOfSpace);
        restOfSpace.styleSetFlexGrow(1);
        //restOfSpace.setDebug(true);
        //restOfSpace.setDebugColor(new Color(0, 255, 0, 50));

        restOfSpace.addChild(pages = new Pages());
    }

}
