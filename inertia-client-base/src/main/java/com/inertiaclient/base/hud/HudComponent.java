package com.inertiaclient.base.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.utils.JsonState;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.inertiaclient.base.value.group.ValueGroup;
import com.inertiaclient.base.value.group.ValueGroupParent;
import com.inertiaclient.base.value.impl.FloatValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public abstract class HudComponent implements LanguageBaseKey, JsonState {

    @Setter
    @Getter
    private HudGroup group;

    @Getter
    private String id;
    @Getter
    private Component name;

    @Getter
    private boolean isEnabled;

    @Setter
    @Getter
    private float width;
    @Setter
    @Getter
    private float height;

    @Setter
    @Getter
    private float scaledWidth;
    @Setter
    @Getter
    private float scaledHeight;

    @Getter
    private ValueGroup mainGroup;
    @Getter
    private ValueGroupParent valueGroups;
    @Getter
    private FloatValue scale;
    @Getter
    private FloatValue topPadding;
    @Getter
    private FloatValue bottomPadding;
    protected final Minecraft mc;


    public HudComponent(String id) {
        this.id = id;
        this.name = Component.translatableWithFallback(getLanguageBaseKey() + ".name", this.getId());

        this.valueGroups = new ValueGroupParent(() -> InertiaBase.instance.getFileManager().getHudQueuedSave().queue());
        this.valueGroups.addGroup(this.mainGroup = new ValueGroup("main", this.getLanguageBaseKey()));

        this.scale = new FloatValue("Scale", this.mainGroup, this.defaultScale(), 0.1f, 5);
        this.topPadding = new FloatValue("Top Padding", this.mainGroup, this.defaultTopPadding(), 0, 5);
        this.bottomPadding = new FloatValue("Bottom Padding", this.mainGroup, this.defaultBottomPadding(), 0, 5);

        this.mc = InertiaBase.mc;
    }

    public void beforeRender(boolean preview, boolean editor, CanvasWrapper canvas) {

    }

    public abstract void render(GuiGraphicsExtractor graphics, boolean editor, CanvasWrapper canvas);

    public float defaultScale() {
        return 1;
    }

    public float defaultTopPadding() {
        return 0;
    }

    public float defaultBottomPadding() {
        return 0;
    }

    protected final float getXOffset(float componentWidth) {
        if (this.getGroup().getComponentsXAlignmentState() == HudGroup.XAlignment.RIGHT) {
            return this.getWidth() - componentWidth;
        }

        if (this.getGroup().getComponentsXAlignmentState() == HudGroup.XAlignment.MIDDLE) {
            return (this.getWidth() - componentWidth) / 2;
        }

        return 0;
    }

    @Override
    public String getLanguageBaseKey() {
        return "icb.hud." + this.id;
    }

    public String getNameString() {
        return this.name.getString();
    }

    public HudComponent setEnabled(boolean enabled) {
        return this.setEnabled(enabled, true);
    }

    //just doesn't save
    public HudComponent setEnabledOnInit(boolean enabled) {
        return this.setEnabled(enabled, false);
    }

    public HudComponent setEnabled(boolean enabled, boolean save) {
        if (this.isEnabled == enabled) {
            return this;
        }

        this.isEnabled = enabled;
        if (enabled) {
            if (shouldAutoManageEventRegistration()) {
                EventManager.register(this);
            }
            this.onEnable();
        } else {
            if (shouldAutoManageEventRegistration()) {
                EventManager.unregister(this);
            }
            this.onDisable();
        }
        if (save) {
            InertiaBase.instance.getFileManager().getHudQueuedSave().queue();
        }
        return this;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected boolean shouldAutoManageEventRegistration() {
        return true;
    }

    protected boolean shouldDoScaleOnRender() {
        return true;
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Enabled", this.isEnabled);

        JsonElement valueGroups = this.valueGroups.toJson();
        if (valueGroups != null) {
            jsonObject.add("Value Groups", valueGroups);
        }

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject main = data.getAsJsonObject();

        this.setEnabled(main.get("Enabled").getAsBoolean(), false);

        JsonElement groups = main.get("Value Groups");
        this.valueGroups.fromJson(groups);
    }

    public interface WidthHeightValue {

        float getValue(boolean editor);
    }


}
