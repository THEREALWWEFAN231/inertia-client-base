package com.inertiaclient.base.value.group;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.utils.JsonState;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.inertiaclient.base.utils.LanguageConstants;
import com.inertiaclient.base.value.Value;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ValueGroup implements LanguageBaseKey, JsonState {

    @Getter
    private String id;
    @Setter
    @Accessors(chain = true)
    private Supplier<Boolean> visibleWhen;
    @Getter
    private final ArrayList<Value> values = new ArrayList<>();
    @Nullable
    @Getter
    private ArrayList<ValueGroup> childrenGroups;
    private String parentTranslationKey;
    @Getter
    private Component name;
    @Setter
    @Getter
    private Runnable saveHandler;

    public ValueGroup(String id, String parentTranslationKey) {
        this.id = id;
        this.parentTranslationKey = parentTranslationKey;

        this.name = this.createName();
    }

    public void add(Value<?> value) {
        this.values.add(value);
    }

    public void addGroup(ValueGroup valueGroup) {
        if (this.childrenGroups == null) {
            this.childrenGroups = new ArrayList<>();
        }
        this.childrenGroups.add(valueGroup);
        //don't set it to the current value, make it run whatever the parent currently is
        valueGroup.setSaveHandler(() -> {
            if (this.saveHandler != null) {
                this.saveHandler.run();
            }
        });
    }

    public boolean isVisible() {
        if (visibleWhen != null) {
            return visibleWhen.get();
        }
        return true;
    }

    @Override
    public String getLanguageBaseKey() {
        return this.parentTranslationKey + ".groups." + this.id;
    }

    protected Component createName() {
        if (this.getId().equals("main")) {
            return Component.translatableWithFallback(LanguageConstants.MAIN_GROUP_NAME, this.getId());
        }
        return Component.translatableWithFallback(getLanguageBaseKey() + ".name", this.getId());

    }

    public String getNameString() {
        return this.name.getString();
    }

    @Override
    public JsonElement toJson() {
        if (this.values.isEmpty() && (this.childrenGroups == null || this.childrenGroups.isEmpty())) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        if (!this.values.isEmpty()) {
            JsonObject values = new JsonObject();
            for (Value<?> value : this.values) {
                values.add(value.getId(), value.toJson());
            }
            jsonObject.add("Values", values);
        }

        if (this.childrenGroups != null && !this.childrenGroups.isEmpty()) {
            JsonObject childrenGroups = new JsonObject();
            for (ValueGroup valueGroup : this.childrenGroups) {
                childrenGroups.add(valueGroup.getId(), valueGroup.toJson());
            }
            jsonObject.add("Children", childrenGroups);
        }

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject main = data.getAsJsonObject();

        if (main.has("Values")) {
            JsonObject valuesObject = main.get("Values").getAsJsonObject();

            valuesObject.entrySet().forEach(entry -> {
                try {
                    var foundValueOptional = this.values.stream().filter(value -> value.getId().equals(entry.getKey())).findFirst();
                    if (foundValueOptional.isPresent()) {
                        foundValueOptional.get().fromJson(entry.getValue());
                    }
                } catch (Exception e) {
                }
            });
        }

        if (main.has("Children")) {
            JsonObject childrenObject = main.get("Children").getAsJsonObject();

            childrenObject.entrySet().forEach(entry -> {
                try {
                    var foundChildOptional = this.childrenGroups.stream().filter(group -> group.getId().equals(entry.getKey())).findFirst();
                    if (foundChildOptional.isPresent()) {
                        foundChildOptional.get().fromJson(entry.getValue());
                    }
                } catch (Exception e) {
                }
            });
        }

    }

}
