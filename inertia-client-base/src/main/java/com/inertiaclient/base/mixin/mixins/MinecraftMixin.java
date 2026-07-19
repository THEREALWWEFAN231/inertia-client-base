package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.ClientTickEvent;
import com.inertiaclient.base.event.impl.ResolutionChangeEvent;
import com.inertiaclient.base.event.impl.RightClickEvent;
import com.inertiaclient.base.mixin.mixins.accessors.FrontendGpuDeviceAccessor;
import com.inertiaclient.base.render.skia.Fonts;
import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Final
    private Window window;

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void rightClickMouse(CallbackInfo callbackInfo) {
        RightClickEvent event = new RightClickEvent();
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callbackInfo) {
        EventManager.fire(new ClientTickEvent());
    }

    @Inject(method = "resizeGui", at = @At("HEAD"))
    public void resizeGui(CallbackInfo callbackInfo) {
        EventManager.fire(new ResolutionChangeEvent(ResolutionChangeEvent.Type.PRE, window.getWidth(), window.getHeight()));
    }

    @Inject(method = "resizeGui", at = @At("RETURN"))
    public void resizeGuiReturn(CallbackInfo callbackInfo) {
        EventManager.fire(new ResolutionChangeEvent(ResolutionChangeEvent.Type.POST, window.getWidth(), window.getHeight()));
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;resizeGui()V"))
    public void initAfterWindow(CallbackInfo callbackInfo) {
        if (!(((FrontendGpuDeviceAccessor) RenderSystem.getDevice()).getBackend() instanceof VulkanDevice)) {
            InertiaBase.LOGGER.error("Not using vulkan init");
            return;
        }
        SkiaVulkanInstance.makeDirectContext();
        Fonts.initFonts();

    }


}
