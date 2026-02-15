package com.inertiaclient.base.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inertiaclient.base.utils.JsonState;
import com.inertiaclient.base.utils.UIUtils;
import lombok.Getter;

import java.awt.Color;

public class WrappedColor implements JsonState {

    @Getter
    private Color colorValueDontTouchUnlessYouKnow;
    @Getter
    private boolean rainbow;

    public WrappedColor(Color color, boolean rainbow) {
        this.colorValueDontTouchUnlessYouKnow = color;
        this.rainbow = rainbow;
    }

    public WrappedColor(Color color) {
        this(color, false);
    }

    public WrappedColor(WrappedColor copy) {
        this.colorValueDontTouchUnlessYouKnow = copy.colorValueDontTouchUnlessYouKnow;
        this.rainbow = copy.rainbow;
    }

    public Color getRenderColor() {
        if (this.rainbow) {
            return UIUtils.colorWithAlpha(UIUtils.rainbow(0), this.colorValueDontTouchUnlessYouKnow.getAlpha());
        }
        return this.colorValueDontTouchUnlessYouKnow;
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("R", this.colorValueDontTouchUnlessYouKnow.getRed());
        jsonObject.addProperty("G", this.colorValueDontTouchUnlessYouKnow.getGreen());
        jsonObject.addProperty("B", this.colorValueDontTouchUnlessYouKnow.getBlue());
        jsonObject.addProperty("A", this.colorValueDontTouchUnlessYouKnow.getAlpha());
        if (rainbow) {
            jsonObject.addProperty("Rainbow", this.rainbow);
        }

        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject jsonObject = data.getAsJsonObject();

        int red = jsonObject.get("R").getAsInt();
        int green = jsonObject.get("G").getAsInt();
        int blue = jsonObject.get("B").getAsInt();
        int alpha = jsonObject.get("A").getAsInt();
        boolean rainbow = false;
        var rainbowObject = jsonObject.get("Rainbow");
        if (rainbowObject != null) {
            rainbow = rainbowObject.getAsBoolean();
        }

        this.colorValueDontTouchUnlessYouKnow = new Color(red, green, blue, alpha);
        this.rainbow = rainbow;
    }
}
