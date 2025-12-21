package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;

@AllArgsConstructor
public class _2D3DEvent extends Event {

    @Getter
    private GuiGraphics drawContext;
    @Getter
    private float tickDelta;

}
