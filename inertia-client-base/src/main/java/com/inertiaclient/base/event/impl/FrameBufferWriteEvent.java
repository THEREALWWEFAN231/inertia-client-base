package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mojang.blaze3d.pipeline.RenderTarget;

@AllArgsConstructor
public class FrameBufferWriteEvent extends Event {

    @Getter
    private RenderTarget framebuffer;

}
