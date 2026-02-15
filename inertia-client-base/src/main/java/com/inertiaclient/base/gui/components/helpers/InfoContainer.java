package com.inertiaclient.base.gui.components.helpers;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.*;
import com.inertiaclient.base.render.yoga.layouts.*;
import io.github.humbleui.skija.Paint;
import lombok.Getter;

import java.util.function.Supplier;

public abstract class InfoContainer<T> extends AbsoulteYogaNode {

    @Getter
    private final T wrapper;

    public InfoContainer(Supplier<String> labelText, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        this(null, labelText, xPosition, yPosition);
    }

    public InfoContainer(T wrapper, Supplier<String> labelText, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        this.wrapper = wrapper;
        this.styleSetFlexShrink(0);
        this.styleSetFlexGrow(0);
        this.styleSetPadding(YogaEdge.ALL, 5);
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.styleSetGap(GapGutter.ROW, 2);
        this.setBeforeSetFirstPositionCallback(() -> {
            float x = xPosition.get() - (this.getWidth() / 2);
            float y = yPosition.get() - this.getHeight();


            YogaNode parent = this.getParent();
            if (x < 0) {
                x = 0;
            } else if (x + this.getWidth() > parent.getWidth()) {
                x = parent.getWidth() - this.getWidth();
            }

            if (y < 0) {
                y = 0;
            } else if (y + this.getHeight() > parent.getHeight()) {
                y = parent.getHeight() - this.getHeight();
            }

            this.setX(x);
            this.setY(y);
        });
        //prevent other clicks when we are clicking onto this
        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            return true;
        });

        YogaNode label = new YogaNode();
        label.styleSetHeight(AbstractGroupContainer.valuesFontSize);
        label.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var text = CanvasWrapper.getFreshTextBuilder();
            text.basic(labelText.get(), 0, 0);
            text.setFontSize(AbstractGroupContainer.valuesFontSize);
            text.draw(canvas);
        });

        YogaNode mainContainer = new YogaNode();
        mainContainer.styleSetFlexGrow(1);
        mainContainer.styleSetFlexShrink(0);
        mainContainer.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        mainContainer.addChild(this.createContentContainer());

        this.addChild(label);
        this.addChild(mainContainer);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), MainFrame.s_frameBorderRadius.get(), MainFrame.s_frameBackgroundColor.get());

            try (Paint stroke = SkiaUtils.createStrokePaint(MainFrame.s_lineColor.get(), MainFrame.s_lineWidth.get())) {
                canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), MainFrame.s_frameBorderRadius.get(), stroke);
            }
        });

        this.setGlobalClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT && clickType == ClickType.CLICKED) {
                if (!this.isHoveredAndInsideParent(relativeMouseX, relativeMouseY)) {
                    this.close();
                    return true;
                }
            }
            return false;
        });

    }

    public void close() {
        ModernClickGui.MODERN_CLICK_GUI.getRoot().removeChild(this);
    }

    public abstract YogaNode createContentContainer();

}
