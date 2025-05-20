package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.EntityRenderEvents;
import com.inertiaclient.base.mixin.custominterfaces.EntityRenderStateInterface;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(entity, null, EntityRenderEvents.Type.HITBOX);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(EntityRenderStateInterface.getEntity(renderState), renderState, EntityRenderEvents.Type.SHADOW);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(EntityRenderStateInterface.getEntity(renderState), renderState, EntityRenderEvents.Type.FIRE);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

}
