package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.GuiRendererInterface;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Projection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//TODO: fix redirect
@Mixin(GuiRenderer.class)
public class GuiRendererMixin implements GuiRendererInterface {

    @Unique
    private RenderTarget inertia$renderTargetOverride;
    @Unique
    private float[] inertia$projectionOverride;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;mainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;"))
    private RenderTarget draw(GameRenderer instance) {
        return this.inertia$renderTargetOverride != null ? inertia$renderTargetOverride : instance.mainRenderTarget();
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Projection;setupOrtho(FFFFZ)V"))
    private void draw0(Projection instance, float zNear, float zFar, float width, float height, boolean invertY) {
        instance.setupOrtho(zNear, zFar, inertia$projectionOverride != null ? inertia$projectionOverride[0] : width, inertia$projectionOverride != null ? inertia$projectionOverride[1] : height, invertY);
    }

    @Override
    public void setRenderTargetOverride(RenderTarget renderTargetOverride) {
        this.inertia$renderTargetOverride = renderTargetOverride;
    }

    @Override
    public void setProjectionOverride(float[] projectionOverride) {
        this.inertia$projectionOverride = projectionOverride;
    }
}
