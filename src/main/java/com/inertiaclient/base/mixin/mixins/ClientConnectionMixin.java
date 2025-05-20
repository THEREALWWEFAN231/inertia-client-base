package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.PacketReceivedEvent;
import com.inertiaclient.base.event.impl.PacketSendEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {


    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo callbackInfo) {
        if (packet instanceof WorldTimeUpdateS2CPacket) {
            InertiaBase.instance.getTickRateCalculator().update();
        }
        PacketReceivedEvent event = new PacketReceivedEvent(packet);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "sendInternal", at = @At("HEAD"), cancellable = true)
    private void sendInternal(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo callbackInfo) {
        PacketSendEvent event = new PacketSendEvent(packet);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }

    }
}
