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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
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
    public void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo callbackInfo) {
        long currentTime = System.nanoTime();
        long delta = currentTime - lastFrameTime;
        lastFrameTime = currentTime;
        AnimationValue.tweenEngine.update(delta);
    }

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0))
    public void renderEnd(RenderTickCounter tickCounter, boolean tick, CallbackInfo callbackInfo, @Local DrawContext drawContextLocal, @Local(ordinal = 0) int mouseX, @Local(ordinal = 1) int mouseY) {
        if (InertiaBase.mc.currentScreen != ModernClickGui.MODERN_CLICK_GUI && AnimationValue.tweenEngine.containsTarget(ModernClickGui.MODERN_CLICK_GUI.getMainFrame().getAnimationValue())) {
            //ModernClickGui.MODERN_CLICK_GUI.render(drawContextLocalRef.get(), mouseXRef.get(), mouseYRef.get(), InertiaClient.mc.getLastFrameDuration());

            ModernClickGui.MODERN_CLICK_GUI.betterRender(drawContextLocal, mouseX, mouseY, tickCounter.getLastDuration());
            /*ModernClickGui.MODERN_CLICK_GUI.getSkiaInstance().setup(ModernClickGui.MODERN_CLICK_GUI.getCachedFrameBuffer(), () -> {
                ModernClickGui.MODERN_CLICK_GUI.getMainFrame().draw(drawContextLocal, mouseX, mouseY, 0, 0, tickCounter.getLastDuration(), ModernClickGui.MODERN_CLICK_GUI.getSkiaInstance().getCanvasWrapper());
            });*/
        }
    }

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    private void renderWorld(RenderTickCounter tickCounter, CallbackInfo callbackInfo, @Local(ordinal = 2) Matrix4f worldPositionPositionMatrix) {
        float tickDelta = tickCounter.getTickDelta(true);

        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(worldPositionPositionMatrix);
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        this.simpleStateRestore.cache();
        EventManager.fire(new _3DEvent(matrices, tickDelta));
        this.simpleStateRestore.close();
        matrices.pop();

        CoordinateDimensionTranslator.setMatrixInformation(worldPositionPositionMatrix);
        matrices.push();

        this.simpleStateRestore.cache();
        _2D3DRender.render(tickDelta, matrices, false);
        this.simpleStateRestore.close();

        matrices.pop();
        matrix4fStack.popMatrix();
    }

}
