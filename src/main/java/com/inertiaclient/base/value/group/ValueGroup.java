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
import net.minecraft.text.Text;

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
    private String parentTranslationKey;
    @Getter
    private Text name;
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

    protected Text createName() {
        if (this.getId().equals("main")) {
            return Text.translatableWithFallback(LanguageConstants.MAIN_GROUP_NAME, this.getId());
        }
        return Text.translatableWithFallback(getLanguageBaseKey() + ".name", this.getId());

    }

    public String getNameString() {
        return this.name.getString();
    }

    @Override
    public JsonElement toJson() {
        if (this.values.isEmpty()) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        JsonObject values = new JsonObject();
        for (Value<?> value : this.values) {
            values.add(value.getId(), value.toJson());
        }
        jsonObject.add("Values", values);

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject main = data.getAsJsonObject();

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
}
