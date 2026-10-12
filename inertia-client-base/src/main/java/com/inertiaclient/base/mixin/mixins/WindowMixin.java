package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.render.yoga.YogaConfig;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "setGuiScale", at = @At("RETURN"))
    public void setGuiScale(final int guiScale, CallbackInfo callbackInfo) {
        YogaConfig.setPointScaleFactor(guiScale);
    }

}
