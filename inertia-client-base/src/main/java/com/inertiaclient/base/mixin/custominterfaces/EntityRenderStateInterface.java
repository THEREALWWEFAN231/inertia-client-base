package com.inertiaclient.base.mixin.custominterfaces;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public interface EntityRenderStateInterface {

    Entity getInertiaclient$entity();

    void setInertiaclient$entity(Entity inertiaclient$entity);

    static Entity getEntity(EntityRenderState entityRenderState) {
        return ((EntityRenderStateInterface) entityRenderState).getInertiaclient$entity();
    }
}
