package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.render.animation.AnimationValue;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    private long lastFrameTime;

    @Shadow
    public abstract void close();

    @Inject(method = "extract", at = @At("HEAD"))
    public void extract(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo callbackInfo) {
        long currentTime = System.nanoTime();
        long delta = currentTime - lastFrameTime;
        lastFrameTime = currentTime;
        AnimationValue.tweenEngine.update(delta);
    }

    //TODO: fix
    /*@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render3dHud(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/OptionsRenderState;Z)V"))
    private void renderWorld(CallbackInfo callbackInfo, @Local(ordinal = 2) Matrix4f worldPositionPositionMatrix) {
        float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);

        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(worldPositionPositionMatrix);
        PoseStack matrices = new PoseStack();

        matrices.pushPose();
        EventManager.fire(new _3DEvent(matrices, tickDelta));
        matrices.popPose();

        CoordinateDimensionTranslator.setMatrixInformation(worldPositionPositionMatrix);
        matrices.pushPose();

        _2D3DRender.render(tickDelta, matrices, false);

        matrices.popPose();
        matrix4fStack.popMatrix();
    }*/

}
