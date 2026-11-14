package com.inertiaclient.base.value;

public interface ImmutableChangedListener<T> {

    void onValueChange(T oldValue, T newValue);

}
