package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.CapabilityTrackerInterface;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GlStateManager.CapabilityTracker.class)
public class CapabilityTrackerMixin implements CapabilityTrackerInterface {

    @Shadow
    @Final
    private int cap;
    @Shadow
    private boolean state;

    @Override
    public boolean getState() {
        return this.state;
    }

    @Override
    public void forceSetState(boolean state) {
        this.state = state;
        if (state) {
            GL11.glEnable(this.cap);
        } else {
            GL11.glDisable(this.cap);
        }
    }
}
