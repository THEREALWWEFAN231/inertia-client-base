package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;

//similar to 2d and 3d render events, renders after the 3d event, but before the traditional 2d event
//this is a 2d event, should only be used to render stuff on the screen
//this allows our rendered lines to look like they were rendered in the 3d event, and makes it so our lines don't render over our held items
@AllArgsConstructor
public class Nano2D3DEventTest extends Event {

    private float scaledWidth;
    private float scaledHeight;
    @Getter
    private GuiGraphics drawContext;
    @Getter
    private PoseStack matrices;
    @Getter
    private float tickDelta;
}