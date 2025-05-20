package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(String id, ValueGroup parent, Boolean defaultValue) {
        super(id, parent, defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void fromJson(JsonElement data) {
        this.setValueAndDontSave(data.getAsBoolean());
    }

}
