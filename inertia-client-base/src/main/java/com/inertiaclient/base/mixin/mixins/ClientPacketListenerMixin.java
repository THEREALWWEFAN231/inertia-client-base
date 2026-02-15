package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo callbackInfo) {
        if (content.startsWith(InertiaBase.instance.getCommandManager().getPrefix())) {
            InertiaBase.instance.getCommandManager().runCommand(content);
            callbackInfo.cancel();
        }
    }

}
