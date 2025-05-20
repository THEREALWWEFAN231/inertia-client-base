package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gl.Framebuffer;

@AllArgsConstructor
public class FrameBufferWriteEvent extends Event {

    @Getter
    private Framebuffer framebuffer;

}
