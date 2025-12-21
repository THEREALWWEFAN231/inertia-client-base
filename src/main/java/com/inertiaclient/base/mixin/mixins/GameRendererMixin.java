package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._3DEvent;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.render._2D3DRender;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.utils.opengl.CoordinateDimensionTranslator;
import com.inertiaclient.base.utils.opengl.staterestore.SimpleStateRestore;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    private SimpleStateRestore simpleStateRestore = new SimpleStateRestore();
    private long lastFrameTime;

    @Shadow
    public abstract void close();

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DeltaTracker tickCounter, boolean tick, CallbackInfo callbackInfo) {
        long currentTime = System.nanoTime();
        long delta = currentTime - lastFrameTime;
        lastFrameTime = currentTime;
        AnimationValue.tweenEngine.update(delta);
    }

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0))
    public void renderEnd(DeltaTracker tickCounter, boolean tick, CallbackInfo callbackInfo, @Local GuiGraphics drawContextLocal, @Local(ordinal = 0) int mouseX, @Local(ordinal = 1) int mouseY) {
        if (InertiaBase.mc.screen != ModernClickGui.MODERN_CLICK_GUI && AnimationValue.tweenEngine.containsTarget(ModernClickGui.MODERN_CLICK_GUI.getMainFrame().getAnimationValue())) {
            //ModernClickGui.MODERN_CLICK_GUI.render(drawContextLocalRef.get(), mouseXRef.get(), mouseYRef.get(), InertiaClient.mc.getLastFrameDuration());

            ModernClickGui.MODERN_CLICK_GUI.betterRender(drawContextLocal, mouseX, mouseY, tickCounter.getRealtimeDeltaTicks());
            /*ModernClickGui.MODERN_CLICK_GUI.getSkiaInstance().setup(ModernClickGui.MODERN_CLICK_GUI.getCachedFrameBuffer(), () -> {
                ModernClickGui.MODERN_CLICK_GUI.getMainFrame().draw(drawContextLocal, mouseX, mouseY, 0, 0, tickCounter.getLastDuration(), ModernClickGui.MODERN_CLICK_GUI.getSkiaInstance().getCanvasWrapper());
            });*/
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"))
    private void renderWorld(DeltaTracker tickCounter, CallbackInfo callbackInfo, @Local(ordinal = 2) Matrix4f worldPositionPositionMatrix) {
        float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);

        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(worldPositionPositionMatrix);
        PoseStack matrices = new PoseStack();

        matrices.pushPose();
        this.simpleStateRestore.cache();
        EventManager.fire(new _3DEvent(matrices, tickDelta));
        this.simpleStateRestore.close();
        matrices.popPose();

        CoordinateDimensionTranslator.setMatrixInformation(worldPositionPositionMatrix);
        matrices.pushPose();

        this.simpleStateRestore.cache();
        _2D3DRender.render(tickDelta, matrices, false);
        this.simpleStateRestore.close();

        matrices.popPose();
        matrix4fStack.popMatrix();
    }

}
