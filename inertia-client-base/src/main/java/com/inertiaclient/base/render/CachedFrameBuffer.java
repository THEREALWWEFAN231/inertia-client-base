package com.inertiaclient.base.render;

import com.inertiaclient.base.utils.TimerUtil;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStates;
import com.inertiaclient.base.utils.opengl.staterestore.SimpleStateRestore;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import org.lwjgl.opengl.GL11;

import java.util.function.Supplier;

public class CachedFrameBuffer {

    @Getter
    protected TextureTarget framebuffer;
    @Setter
    protected Supplier<Integer> fps = () -> -1;//no cap
    protected TimerUtil fpsTimer = new TimerUtil();
    private SimpleStateRestore simpleStateRestore = new SimpleStateRestore();
    private boolean forceUpdate = false;

    @Setter
    private Runnable renderer;

    public void createFrameBufferIfNeeded(int width, int height, boolean stencil, boolean useDepth) {
        if (this.framebuffer == null) {
            if (stencil) {
                this.framebuffer = new StencilFrameBuffer(width, height, useDepth);
            } else {
                this.framebuffer = new TextureTarget(width, height, useDepth);
            }
            framebuffer.setClearColor(0, 0, 0, 0);
            this.forceUpdate = true;
        }
    }

    public void resize(int width, int height) {
        if (this.framebuffer != null) {
            this.framebuffer.resize(width, height);
            this.forceUpdate = true;
        }
    }

    public void bind() {
        this.framebuffer.bindWrite(false);
    }

    public void drawWithRenderer() {
        if (this.renderer != null) {
            if (this.shouldUpdate()) {
                OpenGLStates.FRAME_BUFFER.cache();

                this.framebuffer.clear();
                this.bind();

                this.renderer.run();

                OpenGLStates.FRAME_BUFFER.restore();
            }
        }
    }

    public void deleteFrameBuffer() {
        if (this.framebuffer != null) {
            this.framebuffer.destroyBuffers();
            this.framebuffer = null;
        }
    }

    public void renderCachedImage() {
        this.simpleStateRestore.cache();
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        CompiledShaderProgram shaderProgram = RenderSystem.setShader(CoreShaders.BLIT_SCREEN);
        shaderProgram.bindSampler("InSampler", this.framebuffer.getColorTextureId());

        shaderProgram.apply();

        /*float f = this.lastWidth;
        float g = this.lastHeight;
        float h = 1;
        float i = 1;
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0f, g, 0.0f).texture(0.0f, 0.0f).color(255, 255, 255, 255);
        bufferBuilder.vertex(f, g, 0.0f).texture(h, 0.0f).color(255, 255, 255, 255);
        bufferBuilder.vertex(f, 0.0f, 0.0f).texture(h, i).color(255, 255, 255, 255);
        bufferBuilder.vertex(0.0f, 0.0f, 0.0f).texture(0.0f, i).color(255, 255, 255, 255);
        BufferRenderer.draw(bufferBuilder.end());*/
        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferBuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());

        shaderProgram.clear();
        this.simpleStateRestore.close();
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
}