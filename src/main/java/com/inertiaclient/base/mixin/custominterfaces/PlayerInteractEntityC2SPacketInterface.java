package com.inertiaclient.base.mixin.custominterfaces;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;

public interface PlayerInteractEntityC2SPacketInterface {

    ServerboundInteractPacket.ActionType getInteractionType();

    int getEntityId();

}
