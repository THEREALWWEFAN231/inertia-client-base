package com.inertiaclient.base.render;

import com.inertiaclient.base.utils.TimerUtil;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStates;
import com.inertiaclient.base.utils.opengl.staterestore.SimpleStateRestore;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import org.lwjgl.opengl.GL11;

import java.util.function.Supplier;

public class CachedFrameBuffer {

    @Getter
    protected SimpleFramebuffer framebuffer;
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
                this.framebuffer = new SimpleFramebuffer(width, height, useDepth);
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
        this.framebuffer.beginWrite(false);
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
            this.framebuffer.delete();
            this.framebuffer = null;
        }
    }

    public void renderCachedImage() {
        this.simpleStateRestore.cache();
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderProgram shaderProgram = RenderSystem.setShader(ShaderProgramKeys.BLIT_SCREEN);
        shaderProgram.addSamplerTexture("InSampler", this.framebuffer.getColorAttachment());

        shaderProgram.bind();

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
        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
        bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
        BufferRenderer.draw(bufferBuilder.end());

        shaderProgram.unbind();
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