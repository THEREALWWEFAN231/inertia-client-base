package com.inertiaclient.base.render;

import com.inertiaclient.base.mixin.custominterfaces.FrameBufferInterface;
import net.minecraft.client.gl.SimpleFramebuffer;

public class StencilFrameBuffer extends SimpleFramebuffer implements FrameBufferInterface {

    public StencilFrameBuffer(int width, int height, boolean useDepth) {
        super(width, height, useDepth);
    }

    @Override
    public boolean shouldEnableStencil() {
        return true;
    }
}
