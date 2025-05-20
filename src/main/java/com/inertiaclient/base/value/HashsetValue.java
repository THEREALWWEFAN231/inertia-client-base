package com.inertiaclient.base.value;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.inertiaclient.base.utils.CollectionUtils;
import com.inertiaclient.base.value.group.ValueGroup;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public abstract class HashsetValue<T> extends Value<HashSet<T>> {

    public HashsetValue(String id, ValueGroup parent, HashSet<T> defaultValue) {
        super(id, parent, defaultValue);

        this._afterConstructor();
    }

    public HashsetValue(String id, ValueGroup parent, Collection<T> defaultValue) {
        this(id, parent, new HashSet<T>(defaultValue));
    }

    public HashsetValue(String id, ValueGroup parent, T... defaultValue) {
        this(id, parent, CollectionUtils.arrayToHashset(defaultValue));
    }

    public HashsetValue(String id, ValueGroup parent) {
        this(id, parent, new HashSet<T>());
    }

    public void _afterConstructor() {

    }

    @Override
    protected HashSet<T> copyDefaultValue(HashSet<T> value) {
        return new HashSet<>(value);
    }

    public void addHard(T add) {
        this.getValue().add(add);
        this.getParent().getSaveHandler().run();
    }

    public void removeHard(T remove) {
        this.getValue().remove(remove);
        this.getParent().getSaveHandler().run();
    }

    @Override
    public JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        for (T entry : this.getValue()) {
            jsonArray.add(this.getKeyFromEntry(entry));
        }

        return jsonArray;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonArray main = data.getAsJsonArray();
        this.getValue().clear();

        main.forEach(jsonElement -> {
            try {
                T fromString = this.getEntryFromKey(jsonElement.getAsString());
                this.getValue().add(fromString);
            } catch (Exception e) {
            }
        });
    }

    public abstract String getKeyFromEntry(T entry);

    public abstract T getEntryFromKey(String entryKey);

    public abstract List<T> getAllPossibleEntries();

    @Nullable
    public abstract Stream<T> getAllPossibleEntriesStream();
}
