package com.inertiaclient.base.value;

public interface ValueChangedListener<T> {

    void onValueChange(T oldValue, T newValue);

}
