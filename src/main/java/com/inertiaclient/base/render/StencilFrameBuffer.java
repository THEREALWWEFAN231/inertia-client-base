package com.inertiaclient.base.render;

import com.inertiaclient.base.mixin.custominterfaces.FrameBufferInterface;
import com.mojang.blaze3d.pipeline.TextureTarget;

public class StencilFrameBuffer extends TextureTarget implements FrameBufferInterface {

    public StencilFrameBuffer(int width, int height, boolean useDepth) {
        super(width, height, useDepth);
    }

    @Override
    public boolean shouldEnableStencil() {
        return true;
    }
}
