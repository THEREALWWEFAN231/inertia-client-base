package com.inertiaclient.base.gui.components.module.values.number;

import com.inertiaclient.base.gui.YogaTextField;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.value.impl.NumberValue;

import java.awt.Color;

public class ModernTextBox extends YogaTextField {

    private boolean hasError;

    public ModernTextBox(NumberValue<Number> numberValue) {
        this.styleSetFlexGrow(0);
        this.styleSetFlexShrink(0);
        this.styleSetWidth(60);
        this.styleSetHeight(10);

        this.setTextColor(() -> this.hasError ? Color.red : MainFrame.s_unselectedTextColor.get());

        this.setBorderRadius(() -> 2.5f);
        this.setBackgroundColor(MainFrame.s_selectorButtonColor);
        this.setStrokeColor(MainFrame.s_selectorButtonOutlineColor);
        this.setStrokeWidth(MainFrame.s_lineWidth);
        this.setTextPadding(() -> 1f);

        this.setChangedListener(newText -> {
            if (this.getTextField().isFocused()) {
                if (this.getText().isEmpty()) {
                    return;
                }
                try {
                    Number parsedNumber = numberValue.parseFromString(this.getText());
                    numberValue.setValue(parsedNumber);
                    this.hasError = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    this.hasError = true;
                }
            }
        });

        this.setBeforeTextFieldRender(() -> {
            if (!this.getTextField().isFocused()) {
                this.setText(numberValue.getValue() + "");
            }
        });

    }

}
