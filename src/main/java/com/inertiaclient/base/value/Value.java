package com.inertiaclient.base.value;

import com.inertiaclient.base.utils.JsonState;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.inertiaclient.base.value.group.ValueGroup;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public abstract class Value<T> implements LanguageBaseKey, JsonState { //T is value

    @Getter
    private final String id;
    @Getter
    private Text name;
    @Getter
    private Text description;
    @Getter
    private final ValueGroup parent;
    @Getter
    private T value;
    @Getter
    private final T defaultValue;
    @Setter
    private ValueChangedListener<T> changeListener;
    @Setter
    private Supplier<Boolean> visibleWhen;

    public Value(String id, ValueGroup parent, T defaultValue) {
        this.id = id;
        this.parent = parent;
        this.value = defaultValue;
        this.defaultValue = this.copyDefaultValue(defaultValue);//default

        this.name = Text.translatableWithFallback(this.getLanguageBaseKey() + ".name", this.id);
        this.description = Text.translatableWithFallback(this.getLanguageBaseKey() + ".description", "no description");
        if (parent != null) {
            parent.add(this);
        }
    }

    //allow us to copy the default value, so it's always unique
    protected T copyDefaultValue(T value) {
        return value;
    }

    public void setValue(T value) {
        this.setValueAndDontSave(value);
        if (this.parent != null && this.parent.getSaveHandler() != null) {
            this.parent.getSaveHandler().run();
        }
    }

    public void setValueAndDontSave(T value) {
        T oldValue = this.value;
        if (value == this.defaultValue) {
            value = this.copyDefaultValue(this.defaultValue);
        }
        this.value = value;
        if (this.changeListener != null && !oldValue.equals(value)) {
            this.changeListener.onValueChange(oldValue, this.value);
        }
    }

    public boolean isVisible() {
        if (visibleWhen != null) {
            return visibleWhen.get();
        }
        return true;
    }

    @Override
    public String getLanguageBaseKey() {
        if (parent == null) {
            return this.id;
        }
        return this.parent.getLanguageBaseKey() + ".value." + this.id;
    }

    public String getNameString() {
        return this.name.getString();
    }

    public String getDescriptionString() {
        return this.description.getString();
    }

}