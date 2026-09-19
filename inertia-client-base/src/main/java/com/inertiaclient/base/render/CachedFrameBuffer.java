package com.inertiaclient.base.render;

import com.inertiaclient.base.mixin.mixins.accessors.GuiGraphicsExtractorAccessor;
import com.inertiaclient.base.utils.TimerUtil;
import com.inertiaclient.base.utils.UIUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.Supplier;

public class CachedFrameBuffer {

    @Getter
    protected TextureTarget framebuffer;
    @Setter
    protected Supplier<Integer> fps = () -> -1;//no cap
    protected TimerUtil fpsTimer = new TimerUtil();
    private boolean forceUpdate = false;


    public void createFrameBufferIfNeeded(int width, int height, boolean stencil, boolean depth) {
        if (this.framebuffer == null) {
            //dont really know if this is needed, we needed to enable stencil in  1.21.4 opengl, for the main gui tool tips to render, but they seem fine now without enabling stencil, at that I don't know if this "enables" stencil, think it just allocates room :shrug:

            GpuFormat depthFormat = null;//no depth, no stencil
            if (depth && !stencil) {
                depthFormat = GpuFormat.D32_FLOAT;
            } else if (depth && stencil) {
                depthFormat = GpuFormat.D32_FLOAT_S8_UINT;
            } else if (stencil && !depth) {
                depthFormat = GpuFormat.S8_UINT;
            }
            this.framebuffer = new TextureTarget(null, width, height, GpuFormat.RGBA8_UNORM, depthFormat);

            //should be zero, isn't controlled by the frame buffer anymore,gameRenderState.guiRenderState.clearColorOverride
            //framebuffer.setClearColor(0, 0, 0, 0);
            this.forceUpdate = true;
        }
    }

    public void resize(int width, int height) {
        if (this.framebuffer != null) {
            this.framebuffer.resize(width, height);
            this.forceUpdate = true;
        }
    }

    public void deleteFrameBuffer() {
        if (this.framebuffer != null) {
            this.framebuffer.destroyBuffers();
            this.framebuffer = null;
        }
    }

    public static void blitRenderTarget(GuiGraphicsExtractor graphics, RenderTarget renderTarget, boolean flipY) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(1 / UIUtils.getScaleFactor(), 1 / UIUtils.getScaleFactor());
        ((GuiGraphicsExtractorAccessor) graphics).callInnerBlit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, renderTarget.getColorTextureView(), RenderSystem.getSamplerCache().getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.LINEAR, false), 0, 0, renderTarget.width, renderTarget.height, 0, 1, flipY ? 1 : 0, flipY ? 0 : 1, -1);
        graphics.pose().popMatrix();
    }

    public void renderCachedImage(GuiGraphicsExtractor graphics) {
        blitRenderTarget(graphics, this.framebuffer, false);
    }

    public boolean shouldUpdate() {
        if (this.forceUpdate) {
            this.forceUpdate = false;
            return true;
        }

        int fps = this.fps.get();
        if (fps == -1) {
            return true;
        }

        this.fpsTimer.update();
        if (this.fpsTimer.hasDelayRun(1000f / fps)) {
            this.fpsTimer.reset();
            return true;
        }

        return false;
    }

    public static class TwoDDCachedFrameBuffer extends CachedFrameBuffer {

        @Setter
        protected GenericRender renderer;

        public void drawWithRenderer(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {
            if (this.renderer != null) {
                if (this.shouldUpdate()) {
                    //not needed if skia clears?!?!?
                    //RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.framebuffer.getColorTexture(), GuiRenderer.CLEAR_COLOR, this.framebuffer.getDepthTexture(), 0.0);
                    this.renderer.render(graphics, mouseX, mouseY, delta);
                }
            }
        }
    }

}