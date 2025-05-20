package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

@AllArgsConstructor
public class EntitySpawnEvent extends Event {

    @Getter
    private Entity entity;
    @Getter
    private ClientWorld world;
}
