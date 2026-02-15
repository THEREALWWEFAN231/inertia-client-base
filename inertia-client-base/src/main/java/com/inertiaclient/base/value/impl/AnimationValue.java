package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.animation.CustomTweenEquation;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;

public class AnimationValue extends Value<CustomTweenEquation> {

    public AnimationValue(String id, ValueGroup parent, CustomTweenEquation defaultValue) {
        super(id, parent, defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue().name());
    }

    @Override
    public void fromJson(JsonElement data) {
        this.setValueAndDontSave(Animations.ANIMATIONS_BY_NAME.get(data.getAsString()));
    }
}
