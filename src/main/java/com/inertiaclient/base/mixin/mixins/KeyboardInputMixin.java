package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.InputEventPost;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo callbackInfo) {
        EventManager.fire(new InputEventPost());
    }


}