package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

@AllArgsConstructor
public class EntityRemoveEvent extends Event {

    @Getter
    private Entity entity;
    @Getter
    private ClientLevel world;
}
