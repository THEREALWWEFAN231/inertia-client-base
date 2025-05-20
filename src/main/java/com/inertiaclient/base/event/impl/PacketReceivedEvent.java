package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.packet.Packet;

@AllArgsConstructor
public class PacketReceivedEvent extends Event {

    @Getter
    private Packet<?> packet;

}
