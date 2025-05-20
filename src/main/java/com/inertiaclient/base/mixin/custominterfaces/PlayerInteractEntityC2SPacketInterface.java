package com.inertiaclient.base.mixin.custominterfaces;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public interface PlayerInteractEntityC2SPacketInterface {

    PlayerInteractEntityC2SPacket.InteractType getInteractionType();

    int getEntityId();

}
