package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.PacketReceivedEvent;
import com.inertiaclient.base.event.impl.PacketSendEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {


    @Inject(method = "genericsFtw", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo callbackInfo) {
        if (packet instanceof ClientboundSetTimePacket) {
            InertiaBase.instance.getTickRateCalculator().update();
        }
        PacketReceivedEvent event = new PacketReceivedEvent(packet);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "doSendPacket", at = @At("HEAD"), cancellable = true)
    private void sendInternal(Packet<?> packet, @Nullable PacketSendListener callbacks, boolean flush, CallbackInfo callbackInfo) {
        PacketSendEvent event = new PacketSendEvent(packet);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }

    }
}
