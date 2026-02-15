package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.utils.MathUtils;
import com.inertiaclient.base.value.group.ValueGroup;

public class FloatValue extends NumberValue<Float> {

    public FloatValue(String id, ValueGroup parent, float defaultValue, float minimumValue, float maximumValue) {
        super(id, parent, defaultValue, minimumValue, maximumValue);
    }

    @Override
    public Float parseFromString(String string) throws NumberFormatException {
        return Float.parseFloat(string);
    }

    @Override
    public String getGuiValueString(Float number) {
        return MathUtils.roundBD(number.floatValue(), 2).toString();
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void fromJson(JsonElement data) {
        this.setValueAndDontSave(data.getAsFloat());
    }
}
