package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.Paint;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class SelectorButton extends YogaNode {

    private String cachedButtonText;

    public SelectorButton(Component buttonText, Supplier<Boolean> isSelected, Runnable onClick) {
        this(buttonText::getString, isSelected, onClick);
    }

    public SelectorButton(Supplier<String> buttonText, Supplier<Boolean> isSelected, Runnable onClick) {

        this.styleSetMinWidth(30);
        this.styleSetHeight(14);
        this.setHoverCursorToIndicateClick();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 2.5f, isSelected.get() ? MainFrame.s_selectorButtonSelectedColor.get() : MainFrame.s_selectorButtonColor.get());
            try (Paint strokePaint = SkiaUtils.createStrokePaint(MainFrame.s_selectorButtonOutlineColor.get(), MainFrame.s_lineWidth.get())) {
                canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 2.5f, strokePaint);
            }

            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.basic(this.cachedButtonText, this.getWidth() / 2, this.getHeight() / 2, UIUtils.isHovered(globalMouseX, globalMouseY, this.getGlobalX(), this.getGlobalY(), this.getWidth(), this.getHeight()) ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get());
            nameTextBuilder.setFontSize(6);
            nameTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            nameTextBuilder.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            nameTextBuilder.draw(canvas);

        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                onClick.run();
                return true;
            }
            return false;
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.cachedButtonText = buttonText.get();

            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.setText(this.cachedButtonText);
            nameTextBuilder.setFontSize(6);
            this.styleSetMinWidth(nameTextBuilder.getTextWidth() + 5);
        });
    }

}
