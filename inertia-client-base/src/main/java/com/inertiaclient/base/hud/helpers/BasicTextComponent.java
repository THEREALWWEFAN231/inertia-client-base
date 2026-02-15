package com.inertiaclient.base.hud.helpers;

import com.inertiaclient.base.value.impl.BooleanValue;
import com.inertiaclient.base.value.impl.ColorValue;

import java.awt.Color;

public abstract class BasicTextComponent extends TextComponent {

    private final BooleanValue shadow;
    private final ColorValue colorValue;

    public BasicTextComponent(String name, Color defaultColor, boolean defaultRainbow) {
        super(name);

        this.shadow = new BooleanValue("shadow", this.getMainGroup(), false);
        this.colorValue = new ColorValue("color", this.getMainGroup(), defaultColor, defaultRainbow);
    }

    public BasicTextComponent(String name, Color defaultColor) {
        this(name, defaultColor, false);
    }

    public BasicTextComponent(String name) {
        this(name, Color.white);
    }

    public Color getColor() {
        return this.colorValue.getValue().getRenderColor();
    }

    public boolean drawShadow() {
        return this.shadow.getValue();
    }
}
