package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.CapabilityTrackerInterface;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import org.lwjgl.opengl.GL33C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GlStateManager.BooleanState.class)
public class BooleanStateMixin implements CapabilityTrackerInterface {

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
            GL33C.glEnable(this.state);
        } else {
            GL33C.glDisable(this.state);
        }
    }
}