package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.group.ValueGroup;

import java.awt.Color;

public class ColorValue extends Value<WrappedColor> {

    public ColorValue(String id, ValueGroup parent, WrappedColor defaultValue) {
        super(id, parent, defaultValue);
    }

    public ColorValue(String id, ValueGroup parent, Color defaultValue) {
        this(id, parent, new WrappedColor(defaultValue, false));
    }

    public ColorValue(String id, ValueGroup parent, Color defaultValue, boolean rainbow) {
        this(id, parent, new WrappedColor(defaultValue, rainbow));
    }

    @Override
    protected WrappedColor copyDefaultValue(WrappedColor value) {
        return new WrappedColor(value);
    }

    @Override
    public JsonElement toJson() {
        return this.getValue().toJson();
    }

    @Override
    public void fromJson(JsonElement data) {
        WrappedColor newValue = new WrappedColor(this.getValue());
        newValue.fromJson(data);
        this.setValueAndDontSave(newValue);
    }

}
