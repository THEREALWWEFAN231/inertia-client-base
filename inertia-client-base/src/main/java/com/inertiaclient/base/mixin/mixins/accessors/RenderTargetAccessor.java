package com.inertiaclient.base.mixin.mixins.accessors;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderTarget.class)
public interface RenderTargetAccessor {

    @Accessor("label")
    String getLabel();

}
