package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

@AllArgsConstructor
public class EntityLabelRenderEvent extends Event {

    @Getter
    private Entity entity;
    @Getter
    private EntityRenderState entityRenderState;
    @Getter
    private Component label;

}
