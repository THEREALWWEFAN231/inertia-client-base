package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.ClientTickEvent;
import com.inertiaclient.base.event.impl.ResolutionChangeEvent;
import com.inertiaclient.base.event.impl.RightClickEvent;
import com.inertiaclient.base.render.skia.Fonts;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private Window window;

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
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

    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    public void onResolutionChanged(CallbackInfo callbackInfo) {
        EventManager.fire(new ResolutionChangeEvent(ResolutionChangeEvent.Type.PRE, window.getFramebufferWidth(), window.getFramebufferHeight()));
    }

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    public void onResolutionChangedReturn(CallbackInfo callbackInfo) {
        EventManager.fire(new ResolutionChangeEvent(ResolutionChangeEvent.Type.POST, window.getFramebufferWidth(), window.getFramebufferHeight()));
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    public void initAfterWindow(CallbackInfo callbackInfo) {
        SkiaOpenGLInstance.makeDirectContext();
        Fonts.initFonts();
    }


}
