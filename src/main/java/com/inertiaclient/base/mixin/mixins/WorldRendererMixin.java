package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.WorldRendererInterface;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements WorldRendererInterface {

    @Shadow
    private Frustum frustum;

    @Override
    public Frustum getFrustum() {
        return this.frustum;
    }
}
