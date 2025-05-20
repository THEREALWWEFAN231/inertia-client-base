package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;
import lombok.Getter;

public abstract class NumberValue<V extends Number> extends Value<V> {

    @Getter
    private final V minimumValue;
    @Getter
    private final V maximumValue;

    public NumberValue(String id, ValueGroup parent, V defaultValue, V minimumValue, V maximumValue) {
        super(id, parent, defaultValue);

        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public abstract V parseFromString(String string) throws NumberFormatException;

    public abstract String getGuiValueString(V number);


}
