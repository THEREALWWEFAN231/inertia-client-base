package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.EntityRenderEvents;
import com.inertiaclient.base.mixin.custominterfaces.EntityRenderStateInterface;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(PoseStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(entity, null, EntityRenderEvents.Type.HITBOX);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void renderShadow(PoseStack matrices, MultiBufferSource vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, LevelReader world, float radius, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(EntityRenderStateInterface.getEntity(renderState), renderState, EntityRenderEvents.Type.SHADOW);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderFlame", at = @At("HEAD"), cancellable = true)
    private void renderFire(PoseStack matrices, MultiBufferSource vertexConsumers, EntityRenderState renderState, Quaternionf rotation, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(EntityRenderStateInterface.getEntity(renderState), renderState, EntityRenderEvents.Type.FIRE);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

}
