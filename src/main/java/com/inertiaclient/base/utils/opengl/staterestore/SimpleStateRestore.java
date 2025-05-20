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
        blendState = getState(GlStateManager.BLEND.capState);
        cullState = getState(GlStateManager.CULL.capState);
        depthTestState = getState(GlStateManager.DEPTH.capState);

        srcFactorRGB = GlStateManager.BLEND.srcFactorRGB;
        dstFactorRGB = GlStateManager.BLEND.dstFactorRGB;
        srcFactorAlpha = GlStateManager.BLEND.srcFactorAlpha;
        dstFactorAlpha = GlStateManager.BLEND.dstFactorAlpha;

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
        this.setState(GlStateManager.BLEND.capState, blendState);
        this.setState(GlStateManager.CULL.capState, cullState);
        this.setState(GlStateManager.DEPTH.capState, depthTestState);

        GlStateManager._blendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
        GlStateManager._colorMask(colorMaskRed, colorMaskGreen, colorMaskBlue, colorMaskAlpha);

        GlStateManager._depthMask(depthMask);
        GlStateManager._depthFunc(depthFunc);
    }

    private void setState(GlStateManager.CapabilityTracker capabilityTracker, boolean state) {
        capabilityTracker.setState(state);
    }

    private boolean getState(GlStateManager.CapabilityTracker capabilityTracker) {
        return ((CapabilityTrackerInterface) capabilityTracker).getState();
    }
}
