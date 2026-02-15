package com.inertiaclient.base.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inertiaclient.base.utils.CollectionUtils;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class ModeValue extends Value<ModeValue.Mode> {

    @Getter
    private ArrayList<Mode> modes;

    public ModeValue(String id, ValueGroup parent, Mode defaultValue, Class propertyWrapper) {
        super(id, parent, defaultValue);

        this.modes = new ArrayList<>();
        this.setProperties(propertyWrapper);
    }

    private void setProperties(Class propertyWrapper) {

        for (Field field : propertyWrapper.getDeclaredFields()) {
            if (field.getType() == Mode.class) {
                if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                    try {
                        Mode stringIdentifier = (Mode) field.get(null);
                        stringIdentifier.setParentValue(this);
                        this.modes.add(stringIdentifier);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Mode getModeFromString(String modeId) {
        for (Mode stringIdentifier : this.modes) {
            if (stringIdentifier.getId().equalsIgnoreCase(modeId)) {
                return stringIdentifier;
            }
        }
        return null;
    }

    public Mode getModeFromIncrement(int increment) {
        int currentModeIndex = -1;
        for (int i = 0; i < this.modes.size(); i++) {
            if (this.getValue() == this.modes.get(i)) {
                currentModeIndex = i;
                break;
            }
        }

        if (currentModeIndex == -1) {
            return this.modes.getFirst();
        }

        int wantedIndex = currentModeIndex + increment;
        return this.modes.get(CollectionUtils.getCycledIndex(this.modes, wantedIndex));
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue().getId());
    }

    @Override
    public void fromJson(JsonElement data) {
        this.setValueAndDontSave(this.getModeFromString(data.getAsString()));
    }

    public static class Mode implements LanguageBaseKey {

        @Getter
        private final String id;
        @Setter
        private ModeValue parentValue;

        public Mode(String id) {
            this.id = id;
        }

        public String getName() {
            return Component.translatableWithFallback(this.getLanguageBaseKey() + ".name", this.id).getString();
        }

        public String getDescriptionString() {
            return this.getDescription().getString();
        }

        public Component getDescription() {
            return Component.translatableWithFallback(this.getLanguageBaseKey() + ".description", "no description");
        }

        @Override
        public String getLanguageBaseKey() {
            return parentValue.getLanguageBaseKey() + ".modes." + this.id;
        }
    }
}
