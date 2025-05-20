package com.inertiaclient.base.mixin.mixins.accessors;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerAccessor {

    @Invoker("sendSequencedPacket")
    void callSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);
}
