package com.inertiaclient.base.mixin.custominterfaces;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;

public interface EntityRenderStateInterface {

    Entity getInertiaclient$entity();

    void setInertiaclient$entity(Entity inertiaclient$entity);

    static Entity getEntity(EntityRenderState entityRenderState) {
        return ((EntityRenderStateInterface) entityRenderState).getInertiaclient$entity();
    }
}
