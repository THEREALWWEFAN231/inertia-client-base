package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.value.group.ValueGroup;

public class IntegerValue extends NumberValue<Integer> {

    public IntegerValue(String id, ValueGroup parent, int defaultValue, int minimumValue, int maximumValue) {
        super(id, parent, defaultValue, minimumValue, maximumValue);
    }

    @Override
    public Integer parseFromString(String string) throws NumberFormatException {
        return Integer.parseInt(string);
    }

    @Override
    public String getGuiValueString(Integer number) {
        return number.toString();
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void fromJson(JsonElement data) {
        this.setValueAndDontSave(data.getAsInt());
    }
}
