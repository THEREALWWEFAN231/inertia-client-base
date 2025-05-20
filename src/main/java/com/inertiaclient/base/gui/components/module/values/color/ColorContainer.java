package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.values.AbstractGroupContainer;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.*;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.WrappedColor;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.function.Supplier;

public class ColorContainer extends AbsoulteYogaNode {

    @Setter
    @Getter
    private Color renderedColor;
    @Setter
    @Getter
    private float renderedColorHue;
    @Setter
    @Getter
    private float renderedColorSaturation;
    @Setter
    @Getter
    private float renderedColorBrightness;

    @Setter
    @Getter
    private int renderedColorRed;
    @Setter
    @Getter
    private int renderedColorGreen;
    @Setter
    @Getter
    private int renderedColorBlue;
    @Setter
    @Getter
    private int renderedColorAlpha;
    @Setter
    private boolean renderedRainbow;

    private boolean shouldRevertColor = true;
    private int _beforeDefaultHover_renderedColorRed;
    private int _beforeDefaultHover_renderedColorGreen;
    private int _beforeDefaultHover_renderedColorBlue;
    private int _beforeDefaultHover_renderedColorAlpha;
    private boolean _beforeDefaultHover_renderedRainbow;

    Supplier<Float> xPosition;
    Supplier<Float> yPosition;

    public ColorContainer(WrappedColor color, ColorContainerInterface implementation, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        this.renderedColor = color.getColorValueDontTouchUnlessYouKnow();
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        this.renderedColorRed = this.renderedColor.getRed();
        this.renderedColorGreen = this.renderedColor.getGreen();
        this.renderedColorBlue = this.renderedColor.getBlue();
        this.renderedColorAlpha = this.renderedColor.getAlpha();
        this.renderedRainbow = color.isRainbow();
        this.updateRenderedColorFromRGB();
        this.updateRenderedColorFromHSB();

        this.styleSetFlexShrink(0);
        this.styleSetFlexGrow(0);
        this.styleSetPadding(YogaEdge.ALL, 5);
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.setAfterInitCallback(() -> {
            this.setWidth(200);
            this.setHeight(110);

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
            CanvasWrapper.getFreshTextBuilder().setText(implementation.getNameHeader()).setFontSize(AbstractGroupContainer.valuesFontSize).draw(canvas);
        });

        YogaNode mainContainer = new YogaNode();
        mainContainer.styleSetFlexGrow(1);
        mainContainer.styleSetFlexShrink(0);
        mainContainer.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.addChild(label);
        this.addChild(mainContainer);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), MainFrame.s_frameBorderRadius.get(), MainFrame.s_frameBackgroundColor.get());
            try (var stroke = SkiaUtils.createStrokePaint(MainFrame.s_lineColor.get(), MainFrame.s_lineWidth.get())) {
                canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), MainFrame.s_frameBorderRadius.get(), stroke);
            }


            if (renderedRainbow) {
                Color rainbow = UIUtils.rainbow();
                this.renderedColorRed = rainbow.getRed();
                this.renderedColorGreen = rainbow.getGreen();
                this.renderedColorBlue = rainbow.getBlue();
                this.updateRenderedColorFromRGB();
            }
        });

        this.setGlobalClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT && clickType == ClickType.CLICKED) {
                if (!this.isHoveredAndInsideParent(relativeMouseX, relativeMouseY)) {
                    ModernClickGui.MODERN_CLICK_GUI.getRoot().removeChild(this);
                    return true;
                }
            }
            return false;
        });


        YogaNode leftWrapper = new YogaNode();
        leftWrapper.styleSetWidth(60);
        leftWrapper.styleSetFlexDirection(FlexDirection.COLUMN);
        leftWrapper.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        mainContainer.addChild(leftWrapper);
        leftWrapper.addChild(new SquareColorPicker(this));
        leftWrapper.addChild(new HueSlider(this));

        {
            YogaNode colorDisplay = new YogaNode();
            leftWrapper.addChild(colorDisplay);

            colorDisplay.styleSetHeight(10);
            colorDisplay.styleSetFlexGrow(0);

            colorDisplay.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
                canvas.drawRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), this.renderedColor);
                try (var stroke = SkiaUtils.createStrokePaint(Color.white, .35f)) {
                    canvas.drawRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), stroke);
                }
            });
        }

        {
            YogaNode rightWrapper = new YogaNode();
            rightWrapper.styleSetFlexDirection(FlexDirection.COLUMN);
            rightWrapper.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
            mainContainer.addChild(rightWrapper);

            YogaNode slidersContainer = new YogaNode();
            rightWrapper.addChild(slidersContainer);
            slidersContainer.styleSetFlexDirection(FlexDirection.COLUMN);
            slidersContainer.styleSetGap(GapGutter.ROW, 5);

            {


                slidersContainer.addChild(new ColorComponentContainer(this, () -> new Color(0, renderedColor.getGreen(), renderedColor.getBlue()), () -> new Color(255, renderedColor.getGreen(), renderedColor.getBlue()), aInt -> {
                    this.renderedColorRed = aInt;
                    this.updateRenderedColorFromRGB();
                }, () -> this.renderedColorRed));

                slidersContainer.addChild(new ColorComponentContainer(this, () -> new Color(renderedColor.getRed(), 0, renderedColor.getBlue()), () -> new Color(renderedColor.getRed(), 255, renderedColor.getBlue()), aInt -> {
                    this.renderedColorGreen = aInt;
                    this.updateRenderedColorFromRGB();
                }, () -> this.renderedColorGreen));
                slidersContainer.addChild(new ColorComponentContainer(this, () -> new Color(renderedColor.getRed(), renderedColor.getGreen(), 0), () -> new Color(renderedColor.getRed(), renderedColor.getGreen(), 255), aInt -> {
                    this.renderedColorBlue = aInt;
                    this.updateRenderedColorFromRGB();
                }, () -> this.renderedColorBlue));
                slidersContainer.addChild(new ColorComponentContainer(this, () -> new Color(renderedColor.getRed(), renderedColor.getGreen(), renderedColor.getBlue(), 0), () -> new Color(renderedColor.getRed(), renderedColor.getGreen(), renderedColor.getBlue(), 255), aInt -> {
                    this.renderedColorAlpha = aInt;
                    this.updateRenderedColorFromRGB();
                }, () -> this.renderedColorAlpha));
            }

            {
                YogaNode buttonsContainer = new YogaNode();
                buttonsContainer.styleSetGap(GapGutter.COLUMN, 5);
                rightWrapper.addChild(buttonsContainer);

                buttonsContainer.addChild(new SelectorButton(() -> "Default", () -> false, () -> {
                    WrappedColor defaultColor = implementation.getDefault();
                    this.renderedColorRed = defaultColor.getColorValueDontTouchUnlessYouKnow().getRed();
                    this.renderedColorGreen = defaultColor.getColorValueDontTouchUnlessYouKnow().getGreen();
                    this.renderedColorBlue = defaultColor.getColorValueDontTouchUnlessYouKnow().getBlue();
                    this.renderedRainbow = defaultColor.isRainbow();
                    this.updateRenderedColorFromRGB();
                    shouldRevertColor = false;
                }).setHoverCallback(startHovering -> {
                    WrappedColor defaultColor = implementation.getDefault();
                    if (startHovering) {
                        this._beforeDefaultHover_renderedColorRed = this.renderedColorRed;
                        this._beforeDefaultHover_renderedColorGreen = this.renderedColorGreen;
                        this._beforeDefaultHover_renderedColorBlue = this.renderedColorBlue;
                        this._beforeDefaultHover_renderedColorAlpha = this.renderedColorAlpha;
                        this._beforeDefaultHover_renderedRainbow = this.renderedRainbow;
                        shouldRevertColor = true;

                        this.renderedColorRed = defaultColor.getColorValueDontTouchUnlessYouKnow().getRed();
                        this.renderedColorGreen = defaultColor.getColorValueDontTouchUnlessYouKnow().getGreen();
                        this.renderedColorBlue = defaultColor.getColorValueDontTouchUnlessYouKnow().getBlue();
                        this.renderedColorAlpha = defaultColor.getColorValueDontTouchUnlessYouKnow().getAlpha();
                        this.renderedRainbow = defaultColor.isRainbow();
                        this.updateRenderedColorFromRGB();
                    } else {
                        if (shouldRevertColor) {
                            this.renderedColorRed = _beforeDefaultHover_renderedColorRed;
                            this.renderedColorGreen = _beforeDefaultHover_renderedColorGreen;
                            this.renderedColorBlue = _beforeDefaultHover_renderedColorBlue;
                            this.renderedColorAlpha = _beforeDefaultHover_renderedColorAlpha;
                            this.renderedRainbow = _beforeDefaultHover_renderedRainbow;
                            this.updateRenderedColorFromRGB();
                        }
                    }
                }));
                buttonsContainer.addChild(new SelectorButton(() -> "Rainbow", () -> false, () -> {
                    this.renderedRainbow = !this.renderedRainbow;
                }));
                buttonsContainer.addChild(new SelectorButton(() -> "Save", () -> false, () -> {
                    implementation.setColor(new WrappedColor(this.renderedColor, this.renderedRainbow));
                }));
            }
        }
    }

    //prevent other clicks when we are clicking onto this
    /*public boolean mouseClicked(float mouseX, float mouseY, ButtonIdentifier button) {
        boolean isHovered = this.isHoveredAndInsideParent(mouseX, mouseY);
        if (isHovered) {
            super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(float mouseX, float mouseY, ButtonIdentifier button) {
        boolean isHovered = this.isHoveredAndInsideParent(mouseX, mouseY);
        if (isHovered) {
            super.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return false;
    }*/

    public float[] getRenderedColorHSB() {
        float[] values = new float[3];
        Color.RGBtoHSB(renderedColor.getRed(), renderedColor.getGreen(), renderedColor.getBlue(), values);
        return values;
    }

    public void updateRenderedColorFromHSB() {
        this.renderedColor = Color.getHSBColor(this.renderedColorHue, this.renderedColorSaturation, this.renderedColorBrightness);
        this.renderedColor = UIUtils.colorWithAlpha(this.renderedColor, this.renderedColorAlpha);

        this.renderedColorRed = this.renderedColor.getRed();
        this.renderedColorGreen = this.renderedColor.getGreen();
        this.renderedColorBlue = this.renderedColor.getBlue();
    }

    public void updateRenderedColorFromRGB() {
        this.renderedColor = new Color(this.renderedColorRed, this.renderedColorGreen, this.renderedColorBlue, this.renderedColorAlpha);

        float[] hsb = getRenderedColorHSB();
        this.renderedColorHue = hsb[0];
        this.renderedColorSaturation = hsb[1];
        this.renderedColorBrightness = hsb[2];
    }

}
