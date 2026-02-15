package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.skia.SvgRenderer;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SvgComponent extends YogaNode {

    public static final Supplier<Color> WHITE = () -> Color.white;
    @Accessors(chain = true)
    @Setter
    private Supplier<Color> color = WHITE;
    @Accessors(chain = true)
    @Setter
    private Supplier<Color> hoverColor;
    @Accessors(chain = true)
    @Setter
    private AnimationValue hoverAnimation;
    @Accessors(chain = true)
    @Setter
    private Function<Boolean, Float> hoverTime;//if hover, then time
    @Accessors(chain = true)
    @Setter
    private Consumer<Paint> paintModifier;
    @Accessors(chain = true)
    @Setter
    private Supplier<Float> blurRadius;

    public SvgComponent(Supplier<SvgRenderer> svgRenderer) {

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            Color color = this.color.get();
            if (hoverColor != null) {
                if (hoverAnimation != null) {
                    color = UIUtils.transitionColor(color, hoverColor.get(), hoverAnimation.getValue());
                } else if (this.shouldShowHoveredEffects()) {
                    color = hoverColor.get();
                }
            }


            if (blurRadius != null) {
                float blurRadius = this.blurRadius.get();
                if (blurRadius > 0) {
                    try (var paint = SkiaUtils.createPaintForColor(color)) {
                        SkiaUtils.setPaintBlur(paint, blurRadius);
                        this.render(canvas, svgRenderer, paint);
                    }
                }
            }

            try (var paint = SkiaUtils.createPaintForColor(color)) {
                this.render(canvas, svgRenderer, paint);
            }
        });

        this.setHoverCallback(hovered -> {
            if (hoverAnimation != null) {
                this.hoverAnimation.cancelThis();
                this.hoverAnimation.to(this.hoverTime.apply(hovered)).value(hovered ? 1 : 0).start();
            }
        });

    }

    public SvgComponent(String svgFileLocation) {
        this(() -> new SvgRenderer(svgFileLocation));
    }

    private void render(CanvasWrapper canvas, Supplier<SvgRenderer> svgRenderer, Paint paint) {
        if (paintModifier != null) {
            paintModifier.accept(paint);
        }
        svgRenderer.get().render(canvas, 0, 0, this.getWidth(), this.getHeight(), paint);
    }
}
