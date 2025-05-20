package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.Color;
import java.util.function.Supplier;

public class TextLabel extends YogaNode {

    public static final Supplier<CanvasWrapper.TextBuilder.HorizontalAlignment> LEFT = () -> CanvasWrapper.TextBuilder.HorizontalAlignment.LEFT;
    public static final Supplier<CanvasWrapper.TextBuilder.VerticalAlignment> TOP = () -> CanvasWrapper.TextBuilder.VerticalAlignment.TOP;

    private Supplier<String> label;
    private String cachedLabel;
    @Setter
    @Accessors(chain = true)
    private Supplier<Float> fontSize = () -> AbstractGroupContainer.valuesFontSize;
    private float cachedFontSize;
    @Setter
    @Accessors(chain = true)
    private Supplier<Color> color = () -> Color.white;

    private float cachedScrollWidth;
    @Setter
    @Accessors(chain = true)
    private Supplier<Float> scrollWidth;
    @Setter
    @Accessors(chain = true)
    private Supplier<CanvasWrapper.TextBuilder.HorizontalAlignment> horizontalAlignment = LEFT;
    @Setter
    @Accessors(chain = true)
    private Supplier<CanvasWrapper.TextBuilder.VerticalAlignment> verticalAlignment = TOP;
    private float scrollProgress;

    @Setter
    @Accessors(chain = true)
    private Supplier<Boolean> shadow = () -> false;

    //this is kind of a janky fix, but allows us to not call getTextWidth every frame for this text label
    @Setter
    @Accessors(chain = true)
    private Supplier<Boolean> hasWidth = () -> true;

    public TextLabel(Supplier<String> label) {
        this.label = label;
        // this.setDebug(true);
        this.setDebugColor(UIUtils.colorWithAlpha(Color.green, 50));

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var textBuilder = CanvasWrapper.getFreshTextBuilder();
            textBuilder.setText(this.cachedLabel);
            textBuilder.setFontSize(this.cachedFontSize);


            if (scrollWidth != null) {
                float stringWidth = textBuilder.getTextWidth();
                boolean shouldScroll = stringWidth > (this.cachedScrollWidth = this.scrollWidth.get());
                if (shouldScroll) {
                    if (Math.abs(scrollProgress) > stringWidth) {
                        scrollProgress = cachedScrollWidth;
                    }
                    scrollProgress -= 1 * delta;
                } else {
                    scrollProgress = 0;
                }
                this.styleSetWidth(Math.min(stringWidth, this.cachedScrollWidth));


                textBuilder.setX(scrollProgress);
                textBuilder.setY(0);
                textBuilder.setHorizontalAlignment(this.horizontalAlignment.get()).setVerticalAlignment(this.verticalAlignment.get());
                textBuilder.setColor(this.color.get());
                textBuilder.setShadow(this.shadow.get());
                canvas.save();
                canvas.clipRect(0, 0, this.cachedScrollWidth, this.getHeight());
                textBuilder.draw(canvas);
                canvas.restore();
            } else {
                textBuilder.setX(0);
                textBuilder.setY(0);
                textBuilder.setHorizontalAlignment(this.horizontalAlignment.get()).setVerticalAlignment(this.verticalAlignment.get());
                textBuilder.setColor(this.color.get());
                textBuilder.setShadow(this.shadow.get());
                textBuilder.draw(canvas);
            }
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var textBuilder = CanvasWrapper.getFreshTextBuilder();
            textBuilder.setText(this.cachedLabel = this.label.get());
            textBuilder.setFontSize(this.cachedFontSize = this.fontSize.get());

            this.styleSetHeight(this.cachedFontSize);
            if (this.scrollWidth != null) {
                //this.styleSetWidth(Math.min(stringWidth, this.cachedScrollWidth));
                //this.styleSetWidth(this.cachedScrollWidth);
            } else {
                if (this.hasWidth.get()) {
                    this.styleSetWidth(textBuilder.getTextWidth());
                }
            }
        });
    }

}
