package com.inertiaclient.modtemplate.mixin.mixins;

import com.inertiaclient.modtemplate.ModTemplate;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "resizeGui", at = @At("HEAD"))
    public void resizeGui(CallbackInfo callbackInfo) {
        ModTemplate.LOGGER.info("window resized");
    }


}
