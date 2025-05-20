package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

//similar to 2d and 3d render events, renders after the 3d event, but before the traditional 2d event
//this is a 2d event, should only be used to render stuff on the screen
//this allows our rendered lines to look like they were rendered in the 3d event, and makes it so our lines don't render over our held items
@AllArgsConstructor
@Getter
public class Skia2D3DEvent extends Event {


    private CanvasWrapper canvas;
    private DrawContext drawContext;
    private MatrixStack matrices;
    private float tickDelta;
}