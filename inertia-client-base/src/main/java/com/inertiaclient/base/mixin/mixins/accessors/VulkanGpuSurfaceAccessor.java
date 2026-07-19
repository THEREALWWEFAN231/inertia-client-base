package com.inertiaclient.base.mixin.mixins.accessors;

import com.mojang.renderpearl.backend.vulkan.VulkanGpuSurface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VulkanGpuSurface.class)
public interface VulkanGpuSurfaceAccessor {

    @Accessor("swapchainImageFormat")
    int getSwapchainImageFormat();

}
