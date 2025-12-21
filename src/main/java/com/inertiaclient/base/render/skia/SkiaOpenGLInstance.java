package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.ResolutionChangeEvent;
import com.inertiaclient.base.render.CachedFrameBuffer;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStateRestore;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;
import lombok.Getter;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Supplier;

public class SkiaOpenGLInstance {

    @Getter
    private static DirectContext skiaDirectContext;
    @Getter
    private static HashMap<Integer, Image> skiaNativeImages = new HashMap<>();
    private CachedFrameBuffer frameBuffer;
    @Getter
    private Surface surface;
    @Getter
    private Canvas canvas;
    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private CanvasWrapper canvasWrapper;
    private BackendRenderTarget renderTarget;

    @EventTarget
    private final EventListener<ResolutionChangeEvent> resolutionChangeListener = this::onEvent;

    private SkiaOpenGLInstance(int width, int height) {
        this.frameBuffer = new CachedFrameBuffer();
        this.resize(width, height);

        EventManager.register(this);
    }

    public SkiaOpenGLInstance() {
        this(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight());
    }


    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        if (this.renderTarget != null) {
            this.renderTarget.close();
            this.renderTarget = null;
        }
        if (this.surface != null) {
            this.surface.close();
            this.surface = null;
        }
        if (this.frameBuffer.getFramebuffer() != null) {
            this.frameBuffer.resize(this.width, this.getHeight());
        } else {
            this.frameBuffer.createFrameBufferIfNeeded(this.width, this.height, true, true);
        }
        InertiaBase.mc.getMainRenderTarget().bindWrite(false);


        this.renderTarget = BackendRenderTarget.makeGL(width, height, 0, 8, this.frameBuffer.getFramebuffer().frameBufferId, FramebufferFormat.GR_GL_RGBA8);
        // TODO load monitor profile
        this.surface = Surface.wrapBackendRenderTarget(SkiaOpenGLInstance.skiaDirectContext, this.renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.getDisplayP3(), new SurfaceProps(PixelGeometry.RGB_H));
        this.canvas = this.surface.getCanvas();
        this.canvasWrapper = new CanvasWrapper(this.canvas, this);

        float scale = SkiaOpenGLInstance.getScaleFactor();
        this.canvas.scale(scale, scale);
    }

    public void setFps(Supplier<Integer> fps) {
        this.frameBuffer.setFps(fps);
    }

    public void setup(Runnable draw) {

        this.frameBuffer.setRenderer(() -> {
            RenderSystem.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
            RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
            RenderSystem.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
            try (OpenGLStateRestore openglState = new OpenGLStateRestore()) {
                SkiaOpenGLInstance.skiaDirectContext.resetGLAll();
                draw.run();
                SkiaOpenGLInstance.skiaDirectContext.flush();
                GL33.glBindSampler(0, 0);
            }
        });
        this.frameBuffer.drawWithRenderer();
        this.frameBuffer.renderCachedImage();
    }

    public static void makeDirectContext() {
        skiaDirectContext = DirectContext.makeGL();
    }

    public static float getScaleFactor() {
        return (float) InertiaBase.mc.getWindow().getGuiScale();
    }

    public void onEvent(ResolutionChangeEvent event) {
        if (event.getType() == ResolutionChangeEvent.Type.POST) {
            this.resize(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight());
        }
    }

    public void writeFrameBuffer(int image) {
        //exportFrameBuffer(this.frameBuffer.getFramebuffer());

    }

    public static void exportFrameBuffer(RenderTarget frameBuffer, File file) {
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        //GL20.glBlendEquationSeparate(GL20.GL_FUNC_ADD, GL20.GL_FUNC_ADD);
        //GL20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ZERO);
        //.glBlendEquationSeparate(GL20.GL_FUNC_ADD, GL20.GL_FUNC_ADD);
        //GL20.glBlendFuncSeparate(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);


        int i = frameBuffer.width;
        int j = frameBuffer.height;
        NativeImage nativeImage = new NativeImage(i, j, false);
        RenderSystem.bindTexture(frameBuffer.getColorTextureId());
        nativeImage.downloadTexture(0, false);
        nativeImage.flipY();

        try {
            nativeImage.writeToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
