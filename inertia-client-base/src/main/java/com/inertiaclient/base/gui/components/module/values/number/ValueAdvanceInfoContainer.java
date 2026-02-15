package com.inertiaclient.base.gui.components.module.values.number;

import com.inertiaclient.base.gui.components.helpers.InfoContainer;
import com.inertiaclient.base.value.Value;

import java.util.function.Supplier;

public abstract class ValueAdvanceInfoContainer extends InfoContainer<Value<?>> {

    public ValueAdvanceInfoContainer(Value<?> value, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        super(value, value::getNameString, xPosition, yPosition);
    }

    public Value<?> getValue() {
        return this.getWrapper();
    }

}
