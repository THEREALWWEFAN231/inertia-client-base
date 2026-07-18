package com.inertiaclient.base.mixin.custominterfaces;

import com.mojang.blaze3d.pipeline.RenderTarget;

public interface GuiRendererInterface {

    void setRenderTargetOverride(RenderTarget renderTargetOverride);

    void setProjectionOverride(float[] projectionOverride);

}
