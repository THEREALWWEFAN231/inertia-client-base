package com.inertiaclient.base.render.yoga;

import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.Color;
import java.util.function.Supplier;

@Accessors(chain = true)
@Setter
public class GenericStyle {

    public static Supplier<Float> ZERO_F = () -> 0f;
    public static Supplier<Boolean> TRUE_B = () -> true;

    private Supplier<Boolean> renderIf = TRUE_B;
    private Supplier<Color> backgroundColor;
    private Supplier<Color> strokeColor;
    private Supplier<Float> strokeWidth = ZERO_F;
    private Supplier<Float> borderRadius;

    private Supplier<CanvasWrapper.TextBuilder> textBuilder;
    private Supplier<Boolean> shouldClipSelfOverflow;

    public GenericStyle setStroke(Supplier<Color> color, Supplier<Float> width) {
        this.strokeColor = color;
        this.strokeWidth = width;
        return this;
    }

    protected void applyTo(YogaNode yogaNode) {
        yogaNode.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (!renderIf.get()) {
                return;
            }
            boolean shouldClipSelf = this.shouldClipSelfOverflow != null && this.shouldClipSelfOverflow.get();

            canvas.save();
            if (shouldClipSelf) {
                if (this.borderRadius != null) {
                    canvas.clipRRect(RRect.makeXYWH(0, 0, yogaNode.getWidth(), yogaNode.getHeight(), this.borderRadius.get()));
                } else {
                    canvas.clipRect(0, 0, yogaNode.getWidth(), yogaNode.getHeight());
                }
            }
            if (backgroundColor != null) {
                try (Paint paint = SkiaUtils.createPaintForColor(this.backgroundColor.get())) {
                    this.drawBackground(yogaNode, paint, canvas);
                }
            }

            if (strokeColor != null) {
                try (Paint paint = SkiaUtils.createStrokePaint(this.strokeColor.get(), this.strokeWidth.get())) {
                    this.drawBackground(yogaNode, paint, canvas);
                }
            }

            if (textBuilder != null) {
                var textBuilder = this.textBuilder.get();
                textBuilder.draw(canvas);
            }
            canvas.restore();
        });
    }

    private void drawBackground(YogaNode yogaNode, Paint paint, CanvasWrapper canvasWrapper) {
        if (borderRadius != null) {
            canvasWrapper.drawRRect(0, 0, yogaNode.getWidth(), yogaNode.getHeight(), this.borderRadius.get(), paint);
        } else {
            canvasWrapper.drawRect(0, 0, yogaNode.getWidth(), yogaNode.getHeight(), paint);
        }
    }

}
