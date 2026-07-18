package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.VulkanDeviceInterface;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VulkanDevice.class)
public class VulkanDeviceMixin implements VulkanDeviceInterface {

    @Unique
    private VulkanPhysicalDevice inertia$physicalDevice;

    //TODO: remove redirect
    @Redirect(method = "<init>", at = @At(target = "Lcom/mojang/renderpearl/backend/vulkan/VulkanPhysicalDevice;vkPhysicalDeviceProperties()Lorg/lwjgl/vulkan/VkPhysicalDeviceProperties;", ordinal = 0, value = "INVOKE"))
    private VkPhysicalDeviceProperties VulkanDevice(VulkanPhysicalDevice instance) {
        this.inertia$physicalDevice = instance;
        return instance.vkPhysicalDeviceProperties();
    }


    @Override
    public VulkanPhysicalDevice getVulkanPhysicalDevice() {
        return this.inertia$physicalDevice;
    }
}
