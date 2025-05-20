package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo callbackInfo) {
        if (content.startsWith(InertiaBase.instance.getCommandManager().getPrefix())) {
            InertiaBase.instance.getCommandManager().runCommand(content);
            callbackInfo.cancel();
        }
    }

}
