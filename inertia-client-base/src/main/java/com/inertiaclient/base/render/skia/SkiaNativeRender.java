package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.custominterfaces.GuiRendererInterface;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.backend.vulkan.VulkanConst;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuTexture;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.*;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.GameRenderState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;

public class SkiaNativeRender {

    @Setter
    @Accessors(chain = true)
    private Supplier<Float> nativeWidth;
    @Setter
    @Accessors(chain = true)
    private Supplier<Float> nativeHeight;
    @Setter
    @Accessors(chain = true)
    private Consumer<GuiGraphicsExtractor> setNativeRender;
    @Setter
    private Supplier<Float> blurRadius;
    @Setter
    private boolean autoCleanup = true;

    @Getter
    private TextureTarget frameBuffer;
    @Getter
    private Image image = null;

    @Getter
    private float cachedNativeWidth;
    @Getter
    private float cachedNativeHeight;

    private static final GameRenderState gameRenderState = new GameRenderState();
    private static final GuiRenderer guiRenderer;
    private static final FeatureRenderDispatcher featureRenderDispatcher;

    static {
        featureRenderDispatcher = new FeatureRenderDispatcher(InertiaBase.mc.gameRenderer.renderBuffers(), InertiaBase.mc.getModelManager(), InertiaBase.mc.getAtlasManager(), InertiaBase.mc.font, gameRenderState);
        guiRenderer = new GuiRenderer(gameRenderState.guiRenderState, featureRenderDispatcher, List.of(new GuiEntityRenderer(Minecraft.getInstance().getEntityRenderDispatcher()), new GuiSkinRenderer(), new GuiBookModelRenderer(), new GuiBannerResultRenderer(InertiaBase.mc.getAtlasManager()), new GuiProfilerChartRenderer()));
    }

    public void update() {
        this.cachedNativeWidth = this.nativeWidth.get();
        this.cachedNativeHeight = this.nativeHeight.get();

        int scaledWidth = (int) (this.cachedNativeWidth * SkiaVulkanInstance.getScaleFactor());
        int scaledHeight = (int) (this.cachedNativeHeight * SkiaVulkanInstance.getScaleFactor());

        if (frameBuffer == null) {
            frameBuffer = new TextureTarget(null, scaledWidth, scaledHeight, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);

            if (this.autoCleanup) {
                //TODO: vulkan, fux
                /*final TextureTarget nonReferance = frameBuffer;
                InertiaBase.CLEANER.register(this, () -> {
                    //framebuffer.delete must be called on main thread
                    InertiaBase.mc.executeIfPossible(() -> {
                        System.out.println("deleted " + nonReferance.frameBufferId);
                        nonReferance.destroyBuffers();
                    });
                });*/
            }
            this.setImage();
        }
        if (frameBuffer.width != scaledWidth || frameBuffer.height != scaledHeight) {
            frameBuffer.resize(scaledWidth, scaledHeight);
            this.setImage();
        }
        {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(frameBuffer.getColorTexture(), this.gameRenderState.guiRenderState.clearColorOverride, frameBuffer.getDepthTexture(), 0, 0, 0, frameBuffer.width, frameBuffer.height);
            gameRenderState.guiRenderState.reset();

            GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(InertiaBase.mc, gameRenderState.guiRenderState, -999, -999);
            GuiRendererInterface guiRendererInterface = (GuiRendererInterface) guiRenderer;
            guiRendererInterface.setRenderTargetOverride(this.frameBuffer);
            guiRendererInterface.setProjectionOverride(new float[]{this.cachedNativeWidth, this.cachedNativeHeight});

            setNativeRender.accept(graphics);

            guiRenderer.render();
            guiRenderer.endFrame();
            guiRendererInterface.setRenderTargetOverride(null);
            guiRendererInterface.setProjectionOverride(null);
        }
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

        var colorTexture = (VulkanGpuTexture) this.frameBuffer.getColorTexture();
        var skiaImageInfo = new VkImageInfo(colorTexture.vkImage(), new VulkanAlloc(VK_NULL_HANDLE, VK_NULL_HANDLE, VK_NULL_HANDLE, VK_NULL_HANDLE), 0, 0, VulkanConst.toVk(this.frameBuffer.getColorTexture().getFormat()), 15, 1, this.frameBuffer.getColorTexture().getMipLevels(), -1, false, 0);
        this.image = Image.borrowTextureFrom(SkiaVulkanInstance.getSkiaDirectContext(), BackendTexture.makeVulkan(this.frameBuffer.width, this.frameBuffer.height, skiaImageInfo), SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888, ColorAlphaType.PREMUL, null, null);
    }

    public void delete() {
        this.frameBuffer.destroyBuffers();
        //does this actually delete the image/backend handle
        image.close();
    }

}

