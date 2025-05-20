package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.utils.InputUtils;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.client.util.InputUtil;

public class KeybindValue extends Value<InputUtil.Key> {

    public KeybindValue(String id, ValueGroup parent, InputUtil.Key defaultValue) {
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
