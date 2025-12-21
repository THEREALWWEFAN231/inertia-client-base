package com.inertiaclient.base.utils.opengl.staterestore;

import com.inertiaclient.base.mixin.custominterfaces.CapabilityTrackerInterface;
import com.mojang.blaze3d.platform.GlStateManager;

public class SimpleStateRestore implements AutoCloseable {

    private boolean blendState;
    private boolean cullState;
    private boolean depthTestState;

    //blend
    private int srcFactorRGB;
    private int dstFactorRGB;
    private int srcFactorAlpha;
    private int dstFactorAlpha;


    //color mask
    private boolean colorMaskRed = true;
    private boolean colorMaskGreen = true;
    private boolean colorMaskBlue = true;
    private boolean colorMaskAlpha = true;

    private boolean depthMask;
    private int depthFunc;

    public void cache() {
        blendState = getState(GlStateManager.BLEND.mode);
        cullState = getState(GlStateManager.CULL.enable);
        depthTestState = getState(GlStateManager.DEPTH.mode);

        srcFactorRGB = GlStateManager.BLEND.srcRgb;
        dstFactorRGB = GlStateManager.BLEND.dstRgb;
        srcFactorAlpha = GlStateManager.BLEND.srcAlpha;
        dstFactorAlpha = GlStateManager.BLEND.dstAlpha;

        colorMaskRed = GlStateManager.COLOR_MASK.red;
        colorMaskGreen = GlStateManager.COLOR_MASK.green;
        colorMaskBlue = GlStateManager.COLOR_MASK.blue;
        colorMaskAlpha = GlStateManager.COLOR_MASK.alpha;

        depthMask = GlStateManager.DEPTH.mask;
        depthFunc = GlStateManager.DEPTH.func;
    }

    //resets the state if any were change, from GlStateManager
    @Override
    public void close() {
        this.setState(GlStateManager.BLEND.mode, blendState);
        this.setState(GlStateManager.CULL.enable, cullState);
        this.setState(GlStateManager.DEPTH.mode, depthTestState);

        GlStateManager._blendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
        GlStateManager._colorMask(colorMaskRed, colorMaskGreen, colorMaskBlue, colorMaskAlpha);

        GlStateManager._depthMask(depthMask);
        GlStateManager._depthFunc(depthFunc);
    }

    private void setState(GlStateManager.BooleanState capabilityTracker, boolean state) {
        capabilityTracker.setEnabled(state);
    }

    private boolean getState(GlStateManager.BooleanState capabilityTracker) {
        return ((CapabilityTrackerInterface) capabilityTracker).getState();
    }
}
