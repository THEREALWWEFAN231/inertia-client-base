package com.inertiaclient.base.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.inertia.ModuleToggledEvent;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.InputUtils;
import com.inertiaclient.base.utils.JsonState;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.inertiaclient.base.value.group.ValueGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Module implements LanguageBaseKey, JsonState {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String id;
    @Getter
    private Component name;
    @Getter
    private Component description;
    @Getter
    @Setter
    private InputConstants.Key bind;
    @Getter
    private final Category category;
    private boolean enabledState;
    @Getter
    @Setter
    private boolean visible;
    @Getter
    private final ValueGroup mainGroup;
    @Getter
    private ArrayList<ValueGroup> valueGroups;
    @Getter
    protected Supplier<Boolean> allowEnabled;
    protected boolean saveEnabledState = true;
    @Getter
    @Setter
    protected boolean isFavorite;
    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected InertiaMod mod;

    protected final Minecraft mc;

    public Module(String id, InputConstants.Key bind, Category category) {
        this.id = id;
        this.name = Component.translatableWithFallback(getLanguageBaseKey() + ".name", this.getId());
        this.description = Component.translatableWithFallback(getLanguageBaseKey() + ".description", "no description");
        this.bind = bind;
        this.category = category;
        this.valueGroups = new ArrayList();
        this.addGroup(this.mainGroup = new ValueGroup("main", this.getLanguageBaseKey()));

        this.mc = InertiaBase.mc;
    }

    public Module(String id, Category category) {
        this(id, InputUtils.NO_KEY_BIND, category);
    }

    public boolean isEnabled() {
        return this.enabledState;
    }

    public void setState(boolean state) {
        if (this.allowEnabled != null && !this.allowEnabled.get()) {
            return;
        }
        if (this.enabledState == state) {
            return;
        }
        this.enabledState = state;
        if (state) {
            EventManager.register(this);
            this.onEnable();
        } else {
            EventManager.unregister(this);
            this.onDisable();
        }
        EventManager.fire(new ModuleToggledEvent(this));
    }

    public void toggle() {
        this.toggle(true);
    }

    public void toggle(boolean save) {
        this.setState(!this.enabledState);
        if (save) {
            InertiaBase.instance.getFileManager().getModuleQueuedSave().queue();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }


    @Override
    public String getLanguageBaseKey() {
        return "icb.module." + this.id;
    }

    public String getNameString() {
        return this.name.getString();
    }

    public String getDescriptionString() {
        return description.getString();
    }

    public ValueGroup addGroup(ValueGroup valueGroup) {
        this.valueGroups.add(valueGroup);
        valueGroup.setSaveHandler(() -> InertiaBase.instance.getFileManager().getModuleQueuedSave().queue());

        return valueGroup;
    }

    public YogaNode getGuiNode() {
        return null;
    }

    public String getDisplayText() {
        return "";
    }

    @Override
    public JsonElement toJson() {

        JsonObject jsonObject = new JsonObject();
        if (saveEnabledState) {
            jsonObject.addProperty("Enabled", this.enabledState);
        }
        if (this.bind != InputUtils.NO_KEY_BIND) {
            jsonObject.addProperty("Key", InputUtils.getTranslationKey(this.getBind()));
        }
        if (this.isFavorite) {
            jsonObject.addProperty("Favorite", this.isFavorite);
        }
        jsonObject.addProperty("Visible", this.visible);

        JsonObject groups = new JsonObject();
        for (ValueGroup valueGroup : this.valueGroups) {
            JsonElement valueGroupJson = valueGroup.toJson();

            if (valueGroupJson != null) {
                groups.add(valueGroup.getId(), valueGroupJson);
            }
        }
        if (groups.size() > 0) {
            jsonObject.add("Groups", groups);
        }

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject main = data.getAsJsonObject();

        if (saveEnabledState) {
            this.setState(main.get("Enabled").getAsBoolean());
        }
        if (main.has("Favorite")) {
            this.isFavorite = main.get("Favorite").getAsBoolean();
        }
        this.visible = main.get("Visible").getAsBoolean();
        JsonElement keyElement = main.get("Key");
        if (keyElement == null) {
            this.setBind(InputUtils.NO_KEY_BIND);
        } else {
            this.setBind(InputUtils.fromTranslationKey(keyElement.getAsString()));
        }

        JsonElement groups = main.get("Groups");
        if (groups != null && groups.isJsonObject()) {
            JsonObject groupsObject = groups.getAsJsonObject();
            groupsObject.entrySet().forEach(entry -> {
                try {
                    var foundGroupOptional = this.valueGroups.stream().filter(valueGroup -> valueGroup.getId().equals(entry.getKey())).findFirst();
                    foundGroupOptional.ifPresent(valueGroup -> valueGroup.fromJson(entry.getValue()));
                } catch (Exception e) {
                }
            });
        }
    }
}
