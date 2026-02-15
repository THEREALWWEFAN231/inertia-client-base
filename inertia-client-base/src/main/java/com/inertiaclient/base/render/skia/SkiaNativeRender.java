package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.FrameBufferWriteEvent;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStateRestore;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStates;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SkiaNativeRender {

    @Setter
    @Accessors(chain = true)
    private Supplier<Float> nativeWidth;
    @Setter
    @Accessors(chain = true)
    private Supplier<Float> nativeHeight;
    @Setter
    @Accessors(chain = true)
    private Consumer<GuiGraphics> setNativeRender;
    @Setter
    private Supplier<Float> blurRadius;
    @Setter
    private boolean autoCleanup = true;

    @Getter
    private TextureTarget frameBuffer;
    private Image image = null;

    @Getter
    private float cachedNativeWidth;
    @Getter
    private float cachedNativeHeight;

    private static boolean cancelMinecraftFrameBufferWrites;
    @EventTarget
    private static EventListener<FrameBufferWriteEvent> frameBufferWriteListener = frameBufferWriteEvent -> {
        if (cancelMinecraftFrameBufferWrites) {
            frameBufferWriteEvent.setCancelled(true);
        }
    };

    public void update(GuiGraphics guiGraphics) {
        this.cachedNativeWidth = this.nativeWidth.get();
        this.cachedNativeHeight = this.nativeHeight.get();

        int scaledWidth = (int) (this.cachedNativeWidth * SkiaOpenGLInstance.getScaleFactor());
        int scaledHeight = (int) (this.cachedNativeHeight * SkiaOpenGLInstance.getScaleFactor());

        OpenGLStates.FRAME_BUFFER.cache();
        OpenGLStates.VIEW_PORT.cache();
        OpenGLStates.CLEAR_COLOR.cache();

        if (frameBuffer == null) {
            frameBuffer = new TextureTarget(scaledWidth, scaledHeight, true);

            if (this.autoCleanup) {
                final TextureTarget nonReferance = frameBuffer;
                InertiaBase.CLEANER.register(this, () -> {
                    //framebuffer.delete must be called on main thread
                    InertiaBase.mc.executeIfPossible(() -> {
                        System.out.println("deleted " + nonReferance.frameBufferId);
                        nonReferance.destroyBuffers();
                    });
                });
            }
            this.setImage();
        }
        if (frameBuffer.width != scaledWidth || frameBuffer.height != scaledHeight) {
            frameBuffer.resize(scaledWidth, scaledHeight);
            this.setImage();
        }

        //for some reason skia wants it to be black transparent, instead of minecrafts default white transparent, or our image gets a white background when being drawn with skia
        frameBuffer.setClearColor(0, 0, 0, 0);
        frameBuffer.clear();
        frameBuffer.bindWrite(true);

        {
            RenderSystem.backupProjectionMatrix();
            Matrix4fStack matrixStack = RenderSystem.getModelViewStack();

            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT);
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, this.cachedNativeWidth, this.cachedNativeHeight, 0.0f, 1000.0f, 21000.0f);
            RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);

            matrixStack.pushMatrix();
            matrixStack.identity();
            matrixStack.translate(0.0f, 0.0f, -11000.0f);

            try (var state = new OpenGLStateRestore()) {
                this.cancelMinecraftFrameBufferWrites = true;
                setNativeRender.accept(guiGraphics);
                guiGraphics.flush();
                this.cancelMinecraftFrameBufferWrites = false;
            }

            matrixStack.popMatrix();
            RenderSystem.restoreProjectionMatrix();
        }

        OpenGLStates.FRAME_BUFFER.restore();
        OpenGLStates.VIEW_PORT.restore();
        OpenGLStates.CLEAR_COLOR.restore();
    }

    public void drawImageWithSkia(CanvasWrapper canvas, float x, float y) {
        this.drawImageWithSkia(canvas, x, y, null);
    }

    public void drawImageWithSkia(CanvasWrapper canvas, float x, float y, Paint paint) {
        canvas.drawImageRect(this.image, Rect.makeXYWH(x, y, this.cachedNativeWidth, this.cachedNativeHeight), paint, this.blurRadius);
    }

    private void setImage() {
        if (this.image != null) {
            //TODO: see if this actually deletes the image/backend handle
            //dont do this.....?(seems like Image is RefCnt so it should delete its self?) it messes with crap, see -> go to blocks page, resize window a few times and you will see the issue
            //this.image.close();
        }

        this.image = Image.adoptGLTextureFrom(SkiaOpenGLInstance.getSkiaDirectContext(), this.frameBuffer.getColorTextureId(), GL11.GL_TEXTURE_2D, this.frameBuffer.width, this.frameBuffer.height, GL11.GL_RGBA8, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888);
    }

    public void delete() {
        this.frameBuffer.destroyBuffers();
        //does this actually delete the image/backend handle
        image.close();
    }

    static {
        EventManager.register(SkiaNativeRender.class);
    }
}

