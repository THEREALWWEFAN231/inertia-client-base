package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.FrameBufferWriteEvent;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStateRestore;
import com.inertiaclient.base.utils.opengl.staterestore.OpenGLStates;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class SkiaNativeRender {

    @Getter
    @Setter
    private int nativeWidth;
    @Setter
    @Getter
    private int nativeHeight;
    @Setter
    @Accessors(chain = true)
    private Consumer<DrawContext> setNativeRender;
    @Setter
    private boolean autoCleanup = true;

    @Getter
    private SimpleFramebuffer frameBuffer;
    private Image image = null;

    private static boolean cancelMinecraftFrameBufferWrites;
    @EventTarget
    private static EventListener<FrameBufferWriteEvent> frameBufferWriteListener = frameBufferWriteEvent -> {
        if (cancelMinecraftFrameBufferWrites) {
            frameBufferWriteEvent.setCancelled(true);
        }
    };

    public void update(DrawContext context) {
        int scaledWidth = (int) (nativeWidth * SkiaOpenGLInstance.getScaleFactor());
        int scaledHeight = (int) (nativeHeight * SkiaOpenGLInstance.getScaleFactor());

        OpenGLStates.FRAME_BUFFER.cache();
        OpenGLStates.VIEW_PORT.cache();
        OpenGLStates.CLEAR_COLOR.cache();

        if (frameBuffer == null) {
            frameBuffer = new SimpleFramebuffer(scaledWidth, scaledHeight, true);

            if (this.autoCleanup) {
                final SimpleFramebuffer nonReferance = frameBuffer;
                InertiaBase.CLEANER.register(this, () -> {
                    //framebuffer.delete must be called on main thread
                    InertiaBase.mc.executeSync(() -> {
                        System.out.println("deleted " + nonReferance.fbo);
                        nonReferance.delete();
                    });
                });
            }
            this.setImage();
        }
        if (frameBuffer.textureWidth != scaledWidth || frameBuffer.textureHeight != scaledHeight) {
            frameBuffer.resize(scaledWidth, scaledHeight);
            this.setImage();
        }

        //for some reason skia wants it to be black transparent, instead of minecrafts default white transparent, or our image gets a white background when being drawn with skia
        frameBuffer.setClearColor(0, 0, 0, 0);
        frameBuffer.clear();
        frameBuffer.beginWrite(true);

        {
            RenderSystem.backupProjectionMatrix();
            Matrix4fStack matrixStack = RenderSystem.getModelViewStack();

            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT);
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, nativeWidth, nativeHeight, 0.0f, 1000.0f, 21000.0f);
            RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);

            matrixStack.pushMatrix();
            matrixStack.identity();
            matrixStack.translate(0.0f, 0.0f, -11000.0f);

            try (var state = new OpenGLStateRestore()) {
                this.cancelMinecraftFrameBufferWrites = true;
                setNativeRender.accept(context);
                context.draw();
                this.cancelMinecraftFrameBufferWrites = false;
            }

            matrixStack.popMatrix();
            RenderSystem.restoreProjectionMatrix();
        }

        OpenGLStates.FRAME_BUFFER.restore();
        OpenGLStates.VIEW_PORT.restore();
        OpenGLStates.CLEAR_COLOR.restore();
    }

    public void drawNanoImage(CanvasWrapper canvas, float x, float y) {
        this.drawNanoImage(canvas, x, y, null);
    }

    public void drawNanoImage(CanvasWrapper canvas, float x, float y, Paint paint) {
        canvas.getCanvas().drawImageRect(this.image, Rect.makeXYWH(x, y, nativeWidth, nativeHeight), paint);
    }

    private void setImage() {
        if (this.image != null) {
            //TODO: see if this actually deletes the image/backend handle
            //dont do this.....?(seems like Image is RefCnt so it should delete its self?) it messes with crap, see -> go to blocks page, resize window a few times and you will see the issue
            //this.image.close();
        }

        this.image = Image.adoptGLTextureFrom(SkiaOpenGLInstance.getSkiaDirectContext(), this.frameBuffer.getColorAttachment(), GL11.GL_TEXTURE_2D, this.frameBuffer.textureWidth, this.frameBuffer.textureHeight, GL11.GL_RGBA8, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888);
    }

    public void delete() {
        this.frameBuffer.delete();
        //does this actually delete the image/backend handle
        image.close();
    }

    static {
        EventManager.register(SkiaNativeRender.class);
    }
}

