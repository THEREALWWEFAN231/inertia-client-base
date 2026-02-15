package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorComponentContainer extends YogaNode {


    public ColorComponentContainer(ColorContainer colorContainer, Supplier<Color> fromColor, Supplier<Color> toColor, Consumer<Integer> setColor, Supplier<Integer> getColor) {
        this.addChild(new ColorSlider(colorContainer, fromColor, toColor, setColor, getColor));
        this.addChild(new ColorComponentTextBox(colorContainer, getColor, setColor));
        this.styleSetGap(GapGutter.COLUMN, 5);
    }

}
