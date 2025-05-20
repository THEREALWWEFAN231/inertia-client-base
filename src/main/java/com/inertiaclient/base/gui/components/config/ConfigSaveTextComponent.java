package com.inertiaclient.base.gui.components.config;

import com.inertiaclient.base.gui.components.TextLabel;

import java.awt.Color;
import java.util.function.Supplier;

public class ConfigSaveTextComponent extends TextLabel {

    public ConfigSaveTextComponent(Supplier<String> label, boolean[] values, int valuesIndex) {
        super(label);

        this.setColor(() -> {
            if (values[valuesIndex]) {
                return Color.green;
            }
            return Color.red;
        });
        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            values[valuesIndex] = !values[valuesIndex];
            return false;
        });
    }
}
