package com.inertiaclient.base.mixin.mixins.accessors;

import com.mojang.renderpearl.backend.api.GpuSurfaceBackend;
import com.mojang.renderpearl.frontend.FrontendGpuSurface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FrontendGpuSurface.class)
public interface FrontendGpuSurfaceAccessor {

    @Accessor("backend")
    GpuSurfaceBackend getBackend();
}
