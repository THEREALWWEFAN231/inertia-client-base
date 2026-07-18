package com.inertiaclient.base.mixin.mixins;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    //TODO: fix
    /*@Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(PoseStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo callbackInfo) {
        var event = new EntityRenderEvents(entity, null, EntityRenderEvents.Type.HITBOX);
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "submitShadow", at = @At("HEAD"), cancellable = true)
    public void submitShadow(final PoseStack poseStack, final float radius, final List<EntityRenderState.ShadowPiece> pieces, CallbackInfo callbackInfo) {
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
    }*/

}
