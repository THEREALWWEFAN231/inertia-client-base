package com.inertiaclient.base.gui.components.module.values;

import com.inertiaclient.base.gui.components.TextLabel;
import com.inertiaclient.base.value.Value;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ValueNameLabel extends TextLabel {

    public ValueNameLabel(Value<?> value) {
        this(value.getName());
    }

    public ValueNameLabel(Component label) {
        this(label::getString);
    }

    public ValueNameLabel(Supplier<String> label) {
        super(label);

        this.setFontSize(() -> AbstractGroupContainer.valuesFontSize);
    }


}
