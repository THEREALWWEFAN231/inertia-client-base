package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class EntityRenderEvents extends Event {

    @Getter
    private Entity entity;
    //null with HITBOX
    @Getter
    @Nullable
    private EntityRenderState entityRenderState;
    @Getter
    private Type type;

    public enum Type {
        HITBOX, SHADOW, FIRE;
    }


}
