package com.inertiaclient.base.value.group;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.utils.JsonState;
import lombok.Getter;

import java.util.ArrayList;

public class ValueGroupParent implements JsonState {

    @Getter
    private ArrayList<ValueGroup> valueGroups;
    private Runnable saveHandler;

    public ValueGroupParent(Runnable saveHandler) {
        this.valueGroups = new ArrayList<>();
        this.saveHandler = saveHandler;
    }

    public ValueGroup addGroup(ValueGroup valueGroup) {
        this.valueGroups.add(valueGroup);
        if (this.saveHandler != null) {
            valueGroup.setSaveHandler(this.saveHandler);
        }

        return valueGroup;
    }

    @Override
    public JsonElement toJson() {
        if (this.valueGroups.isEmpty()) {
            return null;
        }

        JsonObject groups = new JsonObject();
        for (ValueGroup valueGroup : this.valueGroups) {
            JsonElement valueGroupJson = valueGroup.toJson();

            if (valueGroupJson != null) {
                groups.add(valueGroup.getId(), valueGroupJson);
            }
        }
        return groups;
    }

    @Override
    public void fromJson(JsonElement groups) {
        if (groups != null && groups.isJsonObject()) {
            JsonObject groupsObject = groups.getAsJsonObject();
            groupsObject.entrySet().forEach(entry -> {
                try {
                    var foundValueGroupOptional = this.valueGroups.stream().filter(valueGroup -> valueGroup.getId().equals(entry.getKey())).findFirst();
                    if (foundValueGroupOptional.isPresent()) {
                        foundValueGroupOptional.get().fromJson(entry.getValue());
                    }
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Error occurred trying to parse Value Group {}", entry.getKey(), e);
                }
            });
        }
    }
}
