package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
import com.mojang.renderpearl.backend.vulkan.VulkanCommandEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VulkanCommandEncoder.class)
public class VulkanCommandEncoderMixin {

    @Inject(method = "submit", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/backend/vulkan/VulkanCommandEncoder;awaitSubmitCompletion(JJ)Z"))
    public void submit(CallbackInfo callbackInfo) {
        if (SkiaVulkanInstance.getSkiaDirectContext() != null) {
            //changing syncCpu to true,  waits for work to be submitted and would cause more accurate rendering? but it slows  down rendering a lot, so we wont do that...
            SkiaVulkanInstance.getSkiaDirectContext().submit(false);
        }
    }

}
