package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.CapabilityTrackerInterface;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GlStateManager.BooleanState.class)
public class CapabilityTrackerMixin implements CapabilityTrackerInterface {

    @Shadow
    @Final
    private int state;
    @Shadow
    private boolean enabled;

    @Override
    public boolean getState() {
        return this.enabled;
    }

    @Override
    public void forceSetState(boolean state) {
        this.enabled = state;
        if (state) {
            GL11.glEnable(this.state);
        } else {
            GL11.glDisable(this.state);
        }
    }
}
