package com.inertiaclient.base.render.skia.instances;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.ResolutionChangeEvent;
import com.inertiaclient.base.mixin.mixins.accessors.FrontendGpuDeviceAccessor;
import com.inertiaclient.base.render.CachedFrameBuffer;
import com.inertiaclient.base.render.GenericRender;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.staterestore.OpenGLStateRestore;
import com.inertiaclient.base.utils.UIUtils;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.backend.opengl.FrameBufferAttachment;
import com.mojang.renderpearl.backend.opengl.GlDevice;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import io.github.humbleui.skija.*;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Supplier;

public class SkiaOpenGLInstance extends SkiaInstance {

    @Getter
    private static DirectContext skiaDirectContext;
    @Getter
    private static HashMap<Integer, Image> skiaNativeImages = new HashMap<>();
    private CachedFrameBuffer.TwoDDCachedFrameBuffer frameBuffer;
    private GenericRender skiaDraw;
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

    private SkiaOpenGLInstance(int width, int height, GenericRender skiaDraw) {
        this.frameBuffer = new CachedFrameBuffer.TwoDDCachedFrameBuffer();
        this.frameBuffer.setRenderer((minecraftGraphics, mouseX, mouseY, delta) -> {
            GlStateManager._pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
            GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
            GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
            try (OpenGLStateRestore openglState = new OpenGLStateRestore()) {
                SkiaOpenGLInstance.skiaDirectContext.resetGLAll();
                this.canvas.clear(0x00000000);
                this.skiaDraw.render(minecraftGraphics, mouseX, mouseY, delta);
                SkiaOpenGLInstance.skiaDirectContext.flush();
                GL33.glBindSampler(0, 0);
            }
        });
        this.resize(width, height);
        this.skiaDraw = skiaDraw;

        EventManager.register(this);
    }

    public SkiaOpenGLInstance(GenericRender drawWithSkia) {
        this(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight(), drawWithSkia);
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
            this.frameBuffer.resize(this.width, this.height);
        } else {
            this.frameBuffer.createFrameBufferIfNeeded(this.width, this.height, false, false);
        }
        //might need this, rebound?
        //InertiaBase.mc.getMainRenderTarget().bindWrite(false);


        var device = (GlDevice) getFrontendDevice().getBackend();
        //GlTexture is FrameBufferAttachment
        var colorTexture = Collections.singletonList((FrameBufferAttachment) this.frameBuffer.getFramebuffer().getColorTexture());
        var depthAttachment = (FrameBufferAttachment) this.frameBuffer.getFramebuffer().getDepthTexture();
        int fbo = -1;
        fbo = device.frameBufferCache().getFbo(device.directStateAccess(), colorTexture, depthAttachment);

        this.renderTarget = BackendRenderTarget.makeGL(width, height, 0, 8, fbo, FramebufferFormat.GR_GL_RGBA8);
        // TODO load monitor profile
        this.surface = Surface.wrapBackendRenderTarget(SkiaOpenGLInstance.skiaDirectContext, this.renderTarget, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888, ColorSpace.getDisplayP3(), new SurfaceProps(PixelGeometry.RGB_H));
        this.canvas = this.surface.getCanvas();
        this.canvasWrapper = new CanvasWrapper(this.canvas, this);

        float scale = UIUtils.getScaleFactor();
        this.canvas.scale(scale, scale);
    }

    public void setFps(Supplier<Integer> fps) {
        this.frameBuffer.setFps(fps);
    }

    public void drawAndRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {
        this.frameBuffer.drawWithRenderer(graphics, mouseX, mouseY, delta);
        this.frameBuffer.renderCachedImage(graphics);
    }

    public static void makeDirectContext() {
        skiaDirectContext = DirectContext.makeGL();
    }

    public static Image createNativeImage(TextureTarget from) {
        int colorTextureId = ((FrameBufferAttachment) from.getColorTexture()).glId();

        return Image.borrowTextureFrom(SkiaOpenGLInstance.getSkiaDirectContext(), BackendTexture.makeGL(from.width, from.height, from.getColorTexture().getMipLevels() > 0, new GLTextureInfo(GL11.GL_TEXTURE_2D, colorTextureId, GL11.GL_RGBA8)), SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888, ColorAlphaType.PREMUL, null, null);
    }

    public void onEvent(ResolutionChangeEvent event) {
        if (event.getType() == ResolutionChangeEvent.Type.POST) {
            this.resize(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight());
        }
    }

    private static FrontendGpuDeviceAccessor getFrontendDevice() {
        return (FrontendGpuDeviceAccessor) RenderSystem.getDevice();
    }

}