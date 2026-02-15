package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.Supplier;

public class SelectButton extends YogaNode {

    public static final float BORDER_RADIUS = 1.5f;

    private AnimationValue animation;
    private Supplier<String> label;
    private String cachedLabel;

    public SelectButton(Component label, Runnable onClick) {
        this(label::getString, onClick);
    }

    public SelectButton(Supplier<String> label, Runnable onClick) {
        this.label = label;
        this.animation = new AnimationValue();

        this.styleSetHeight(10);
        this.setHoverCursorToIndicateClick();

        this.setHoverCallback(hovered -> {
            AnimationValue.tweenEngine.cancelTarget(animation);
            animation.to(.25f).value(hovered ? 1 : 0).ease(Animations.linear).start();
        });
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), BORDER_RADIUS, MainFrame.s_stringValuesBackgroundColor.get());
            if (this.shouldShowHoveredEffects() || this.animation.getValue() != 0) {
                try (Paint stroke = SkiaUtils.createStrokePaint(UIUtils.transitionColor(new Color(0, 0, 0, 0), MainFrame.s_unselectedTextColor.get(), this.animation.getValue()), MainFrame.s_lineWidth.get())) {
                    canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), BORDER_RADIUS, stroke);
                }
            }

            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(this.cachedLabel, this.getWidth() / 2, (this.getHeight() + 1) / 2, UIUtils.transitionColor(MainFrame.s_unselectedTextColor.get(), MainFrame.s_selectedTextColor.get(), this.animation.getValue()));
            name.setFontSize(AbstractGroupContainer.valuesFontSize);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            name.draw(canvas);
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                onClick.run();
                return true;
            }

            return false;
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.cachedLabel = this.label.get();

            var name = CanvasWrapper.getFreshTextBuilder();
            name.setText(this.cachedLabel);
            name.setFontSize(AbstractGroupContainer.valuesFontSize);
            this.styleSetWidth(name.getTextWidth() + 3.5f);
        });

    }

}
