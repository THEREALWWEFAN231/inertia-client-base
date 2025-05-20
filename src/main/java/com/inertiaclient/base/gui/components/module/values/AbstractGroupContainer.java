package com.inertiaclient.base.gui.components.module.values;

import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.GenericStyle;
import com.inertiaclient.base.render.yoga.YogaBuilder;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.function.Consumer;

public class AbstractGroupContainer extends YogaNode {

    public static float valuesFontSize = 6;
    public static float gap = 2;

    public AbstractGroupContainer(Text label, Consumer<YogaNode> addToValuesContainer) {
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
        //this.styleSetGap(GapGutter.ROW, gap);
        this.styleSetFlexShrink(0);
        this.styleSetFlexGrow(0);

        new YogaNode(this).styleSetHeight(CanvasWrapper.getFreshTextBuilder().getFontSize()).applyGenericStyle(new GenericStyle().setTextBuilder(() -> CanvasWrapper.getFreshTextBuilder().setText(label).setColor(Color.white)));

        YogaNode valuesContainer = YogaBuilder.getFreshBuilder(this).setFlexDirection(FlexDirection.COLUMN).setGap(GapGutter.ROW, gap).build();
        valuesContainer.styleSetMargin(YogaEdge.LEFT, 3);
        valuesContainer.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            float x = -2f;
            canvas.save();
            canvas.translate(x, 0);
            canvas.drawRect(0, 2, .5f, valuesContainer.getHeight() - 4, Color.white);
            canvas.restore();
        });


        addToValuesContainer.accept(valuesContainer);
    }

}
