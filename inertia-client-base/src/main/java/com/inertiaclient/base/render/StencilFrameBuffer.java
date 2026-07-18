package com.inertiaclient.base.render;

import com.inertiaclient.base.mixin.custominterfaces.FrameBufferInterface;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.GpuFormat;
import org.jspecify.annotations.Nullable;

public class StencilFrameBuffer extends TextureTarget implements FrameBufferInterface {

    public StencilFrameBuffer(final @Nullable String label, final int width, final int height, final @Nullable GpuFormat colorFormat, final @Nullable GpuFormat depthFormat) {
        super(label, width, height, colorFormat, depthFormat);
    }

    @Override
    public boolean shouldEnableStencil() {
        return true;
    }
}
