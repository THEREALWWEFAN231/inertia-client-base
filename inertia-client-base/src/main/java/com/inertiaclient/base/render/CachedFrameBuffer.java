package com.inertiaclient.base.render;

import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
import com.inertiaclient.base.utils.TimerUtil;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.*;
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

    @Setter
    private Runnable renderer;

    public void createFrameBufferIfNeeded(int width, int height, boolean stencil, boolean useDepth) {
        if (this.framebuffer == null) {
            if (stencil) {
                this.framebuffer = new StencilFrameBuffer(null, width, height, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
            } else {
                this.framebuffer = new TextureTarget(null, width, height, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
            }
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

    public void drawWithRenderer() {
        if (this.renderer != null) {
            if (this.shouldUpdate()) {

                //not needed if skia clears?!?!?
                //RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.framebuffer.getColorTexture(), GuiRenderer.CLEAR_COLOR, this.framebuffer.getDepthTexture(), 0.0);

                this.renderer.run();
            }
        }
    }

    public void deleteFrameBuffer() {
        if (this.framebuffer != null) {
            this.framebuffer.destroyBuffers();
            this.framebuffer = null;
        }
    }

    public void renderCachedImage(GuiGraphicsExtractor graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(1 / SkiaVulkanInstance.getScaleFactor(), 1 / SkiaVulkanInstance.getScaleFactor());
        graphics.innerBlit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, this.framebuffer.getColorTextureView(), RenderSystem.getSamplerCache().getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.LINEAR, false), 0, 0, this.framebuffer.width, this.framebuffer.height, 0, 1, 0, 1, -1);
        graphics.pose().popMatrix();
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

    private void blitPremultiInertia(GuiGraphicsExtractor graphics, GpuTextureView textureView, GpuSampler sampler, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1) {

    }
}