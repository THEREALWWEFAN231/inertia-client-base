package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@AllArgsConstructor
public class _2D3DEvent extends Event {

    @Getter
    private GuiGraphicsExtractor drawContext;
    @Getter
    private float tickDelta;

}
