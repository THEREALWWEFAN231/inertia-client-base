package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.gui.YogaTextField;
import com.inertiaclient.base.gui.components.MainFrame;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorComponentTextBox extends YogaTextField {

    private boolean hasError;

    public ColorComponentTextBox(ColorContainer colorContainer, Supplier<Integer> getColor, Consumer<Integer> setColor) {
        this.styleSetFlexGrow(0);
        this.styleSetFlexShrink(0);
        this.styleSetWidth(30);
        this.styleSetHeight(10);

        this.setMaxTextLength(3);
        this.setTextColor(() -> this.hasError ? Color.red : MainFrame.s_unselectedTextColor.get());

        this.setBorderRadius(() -> 2.5f);
        this.setBackgroundColor(MainFrame.s_selectorButtonColor);
        this.setStrokeColor(MainFrame.s_selectorButtonOutlineColor);
        this.setStrokeWidth(MainFrame.s_lineWidth);
        this.setTextPadding(() -> 1f);

        this.setChangedListener(newText -> {
            if (this.getTextField().isFocused()) {
                if (this.getText().isEmpty()) {
                    setColor.accept(0);
                    return;
                }
                try {

                    int parsedInt = Integer.parseInt(this.getText());
                    if (parsedInt < 0 || parsedInt > 255) {
                        throw new Exception();
                    }

                    setColor.accept(parsedInt);
                    this.hasError = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    setColor.accept(0);
                    this.hasError = true;
                }
            }
        });

        this.setBeforeTextFieldRender(() -> {
            if (!this.getTextField().isFocused()) {
                this.setText(getColor.get() + "");
            }
        });
        this.setAfterTextFieldClick(() -> {
            if (this.getTextField().isFocused()) {
                colorContainer.setRenderedRainbow(false);
            }
        });

    }

}
