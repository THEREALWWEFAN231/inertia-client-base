package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.yoga.YogaNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;

@AllArgsConstructor
public class Page {

    @Getter
    private Text label;
    @Getter
    private YogaNode node;

}
