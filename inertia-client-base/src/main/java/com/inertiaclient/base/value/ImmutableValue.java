package com.inertiaclient.base.value;

import com.inertiaclient.base.value.group.ValueGroup;

public abstract class ImmutableValue<T> extends Value<T, ImmutableChangedListener<T>> {

    public ImmutableValue(String id, ValueGroup parent, T defaultValue) {
        super(id, parent, defaultValue);
    }

    @Override
    protected void runChangeListenerForSetValue(T oldValue, T newValue) {
        this.changeListener.onValueChange(oldValue, newValue);
    }
}
