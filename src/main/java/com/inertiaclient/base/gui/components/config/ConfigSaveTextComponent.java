package com.inertiaclient.base.gui.components.config;

import com.inertiaclient.base.gui.components.TextLabel;
import net.minecraft.text.Text;

import java.awt.Color;

public class ConfigSaveTextComponent extends TextLabel {

    public ConfigSaveTextComponent(Text label, boolean[] values, int valuesIndex) {
        super(label::getString);

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
