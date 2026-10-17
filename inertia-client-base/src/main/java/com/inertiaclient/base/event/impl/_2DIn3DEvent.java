package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@AllArgsConstructor
public class _2DIn3DEvent extends Event {

    @Getter
    private GuiGraphicsExtractor graphics;
    @Getter
    private float tickDelta;

}
