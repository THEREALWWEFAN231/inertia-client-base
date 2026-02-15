package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.EntityRenderStateInterface;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateInterface {

    @Unique
    private Entity inertiaclient$entity;

    @Override
    public Entity getInertiaclient$entity() {
        return this.inertiaclient$entity;
    }

    @Override
    public void setInertiaclient$entity(Entity entity) {
        this.inertiaclient$entity = entity;
    }
}
