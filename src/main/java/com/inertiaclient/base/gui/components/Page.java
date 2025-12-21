package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.yoga.YogaNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@AllArgsConstructor
public class Page {

    @Getter
    private Component label;
    @Getter
    private YogaNode node;

}
