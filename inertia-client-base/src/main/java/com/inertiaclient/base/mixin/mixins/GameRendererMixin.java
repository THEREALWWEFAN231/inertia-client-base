package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._3DCachedEvent;
import com.inertiaclient.base.event.impl._3DEvent;
import com.inertiaclient.base.mixin.custominterfaces.GameRendererInterface;
import com.inertiaclient.base.render.CoordinateDimensionTranslator;
import com.inertiaclient.base.render.ThreeDCacheFrameBuffer;
import com.inertiaclient.base.render._2D3DRender;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.OptionalDouble;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements GameRendererInterface {

    @Unique
    private long inertia$lastFrameTime;

    @Shadow
    @Final
    private GameRenderState gameRenderState;

    @Shadow
    @Final
    private RenderTarget mainRenderTarget;
    @Shadow
    @Final
    private FeatureRenderDispatcher featureRenderDispatcher;
    @Shadow
    @Final
    private FogRenderer fogRenderer;
    @Unique
    private SubmitNodeStorage inertia$3dPassStorage = new SubmitNodeStorage();

    @Unique
    private ThreeDCacheFrameBuffer inertia$3DCache = new ThreeDCacheFrameBuffer();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(CallbackInfo callbackInfo) {
        this.inertia$3DCache.setRenderer((poseStack, renderPassBuffer, tickDelta) -> {
            poseStack.pushPose();
            try {
                EventManager.fire(new _3DCachedEvent(poseStack, renderPassBuffer, tickDelta));
            } catch (Exception e) {
                InertiaBase.LOGGER.error("Error on 3D cached event", e);
            }
            poseStack.popPose();
        });
        this.inertia$3DCache.setFps(() -> InertiaBase.instance.getSettings().getWorldEspFPS().getFpsForCache());
    }

    @Inject(method = "extract", at = @At("HEAD"))
    public void extract(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo callbackInfo) {
        long currentTime = System.nanoTime();
        long delta = currentTime - inertia$lastFrameTime;
        inertia$lastFrameTime = currentTime;
        AnimationValue.tweenEngine.update(delta);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render3dHud(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/OptionsRenderState;Z)V"))
    private void renderWorld(CallbackInfo ci) {
        CameraRenderState cameraState = this.gameRenderState.levelRenderState.cameraRenderState;
        float worldPartialTicks = this.gameRenderState.levelRenderState.worldPartialTicks;

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(cameraState.viewRotationMatrix);

        CoordinateDimensionTranslator.setMatrixInformation(cameraState.viewRotationMatrix, cameraState.projectionMatrix);

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        try {
            EventManager.fire(new _3DEvent(poseStack, this.inertia$3dPassStorage, worldPartialTicks));
        } catch (Exception e) {
            InertiaBase.LOGGER.error("Error on 3D event", e);
        }
        poseStack.popPose();
        try {
            var oldFog = RenderSystem.getShaderFog();
            try (FeatureRenderDispatcher.PreparedFrame frame = this.featureRenderDispatcher.prepareFrame(this.inertia$3dPassStorage); RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "inertia_3d_renderpass", this.mainRenderTarget.getColorTextureView(), Optional.empty(), this.mainRenderTarget.getDepthTextureView(), OptionalDouble.empty());) {
                RenderSystem.setShaderFog(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
                RenderSystem.bindDefaultUniforms(renderPass);

                FeatureRenderDispatcher.renderAllFeatures(renderPass, frame);
            }
            RenderSystem.setShaderFog(oldFog);
        } catch (Exception e) {
            InertiaBase.LOGGER.error("Error on 3d render pass", e);
        }

        this.inertia$3DCache.createFrameBufferIfNeeded(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight(), false, true);
        this.inertia$3DCache.drawWithRenderer(poseStack, worldPartialTicks);

        poseStack.pushPose();
        _2D3DRender.render(worldPartialTicks);
        poseStack.popPose();

        modelViewStack.popMatrix();
    }

    @Inject(method = "resize", at = @At("HEAD"))
    public void resize(int width, int height, CallbackInfo ci) {
        this.inertia$3DCache.resize(width, height);
    }

    @Override
    public ThreeDCacheFrameBuffer get3DCachedFrameBuffer() {
        return this.inertia$3DCache;
    }
}
