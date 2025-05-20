package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.EntityLabelRenderEvent;
import com.inertiaclient.base.mixin.custominterfaces.EntityRenderStateInterface;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "updateRenderState", at = @At("RETURN"))
    public void updateRenderState(T entity, S state, float tickDelta, CallbackInfo callbackInfo) {
        ((EntityRenderStateInterface) state).setInertiaclient$entity(entity);
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo callbackInfo) {
        EntityLabelRenderEvent event = new EntityLabelRenderEvent(EntityRenderStateInterface.getEntity(state), state, text);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

}
