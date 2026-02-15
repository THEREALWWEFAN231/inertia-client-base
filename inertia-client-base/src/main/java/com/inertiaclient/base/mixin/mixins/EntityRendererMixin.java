package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.EntityLabelRenderEvent;
import com.inertiaclient.base.mixin.custominterfaces.EntityRenderStateInterface;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void updateRenderState(T entity, S state, float tickDelta, CallbackInfo callbackInfo) {
        ((EntityRenderStateInterface) state).setInertiaclient$entity(entity);
    }

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(S state, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo callbackInfo) {
        EntityLabelRenderEvent event = new EntityLabelRenderEvent(EntityRenderStateInterface.getEntity(state), state, text);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

}
