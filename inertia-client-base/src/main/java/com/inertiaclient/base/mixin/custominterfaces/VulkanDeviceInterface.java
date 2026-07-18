package com.inertiaclient.base.mixin.custominterfaces;

import com.mojang.renderpearl.backend.vulkan.VulkanPhysicalDevice;

public interface VulkanDeviceInterface {

    VulkanPhysicalDevice getVulkanPhysicalDevice();
}
