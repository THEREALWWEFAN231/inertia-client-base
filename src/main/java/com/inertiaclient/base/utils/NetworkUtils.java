package com.inertiaclient.base.utils;

import com.inertiaclient.base.mixin.mixins.accessors.ClientPlayerInteractionManagerAccessor;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;

import static com.inertiaclient.base.InertiaBase.mc;

public class NetworkUtils {

    public static void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {
        ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).callSendSequencedPacket(world, packetCreator);
    }

}
