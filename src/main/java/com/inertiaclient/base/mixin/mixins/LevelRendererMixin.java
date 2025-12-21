package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.WorldRendererInterface;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements WorldRendererInterface {

    @Shadow
    private Frustum cullingFrustum;

    @Override
    public Frustum getFrustum() {
        return this.cullingFrustum;
    }
}
