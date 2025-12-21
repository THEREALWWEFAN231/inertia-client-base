package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.utils.InputUtils;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;
import com.mojang.blaze3d.platform.InputConstants;

public class KeybindValue extends Value<InputConstants.Key> {

    public KeybindValue(String id, ValueGroup parent, InputConstants.Key defaultValue) {
        super(id, parent, defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(InputUtils.getTranslationKey(this.getValue()));
    }

    @Override
    public void fromJson(JsonElement data) {
        this.setValueAndDontSave(InputUtils.fromTranslationKey(data.getAsString()));
    }

}
