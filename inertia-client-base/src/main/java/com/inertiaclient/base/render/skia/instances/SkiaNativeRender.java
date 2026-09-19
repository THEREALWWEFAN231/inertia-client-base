package com.inertiaclient.base.render.skia.instances;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.custominterfaces.GuiRendererInterface;
import com.inertiaclient.base.mixin.mixins.accessors.GameRendererAccessor;
import com.inertiaclient.base.mixin.mixins.accessors.RenderTargetAccessor;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.utils.UIUtils;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.Paint;
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

//TODO: implement manual clean up
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
    private static GuiRenderer guiRenderer;
    private static FeatureRenderDispatcher featureRenderDispatcher;

    public void update() {
        if (guiRenderer == null) {
            featureRenderDispatcher = new FeatureRenderDispatcher(InertiaBase.mc.gameRenderer.renderBuffers(), InertiaBase.mc.getModelManager(), InertiaBase.mc.getAtlasManager(), InertiaBase.mc.font, gameRenderState);
            guiRenderer = new GuiRenderer(gameRenderState.guiRenderState, featureRenderDispatcher, List.of(new GuiEntityRenderer(Minecraft.getInstance().getEntityRenderDispatcher()), new GuiSkinRenderer(), new GuiBookModelRenderer(), new GuiBannerResultRenderer(InertiaBase.mc.getAtlasManager()), new GuiProfilerChartRenderer()));
        }
        this.cachedNativeWidth = this.nativeWidth.get();
        this.cachedNativeHeight = this.nativeHeight.get();

        int scaledWidth = (int) (this.cachedNativeWidth * UIUtils.getScaleFactor());
        int scaledHeight = (int) (this.cachedNativeHeight * UIUtils.getScaleFactor());

        if (frameBuffer == null) {
            frameBuffer = new TextureTarget(null, scaledWidth, scaledHeight, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);

            if (this.autoCleanup) {
                final TextureTarget nonReference = frameBuffer;
                InertiaBase.CLEANER.register(this, () -> {
                    //framebuffer.delete must be called on main thread
                    InertiaBase.mc.executeIfPossible(() -> {
                        InertiaBase.LOGGER.info("deleted SkiaNativeRender texture  {}", ((RenderTargetAccessor) nonReference).getLabel());
                        nonReference.destroyBuffers();
                    });
                });
            }
            this.setImage();
        }
        if (frameBuffer.width != scaledWidth || frameBuffer.height != scaledHeight) {
            frameBuffer.resize(scaledWidth, scaledHeight);
            this.setImage();
        }

        GameRendererAccessor gameRendererAccessor = (GameRendererAccessor) InertiaBase.mc.gameRenderer;
        var oldProjectionType = RenderSystem.getProjectionType();
        var oldProjectionMatrix = RenderSystem.getProjectionMatrixBuffer();
        boolean oldLightmap = gameRendererAccessor.getUseUiLightmap();
        var oldLighting = RenderSystem.getShaderLights();

        {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(frameBuffer.getColorTexture(), this.gameRenderState.guiRenderState.clearColorOverride, frameBuffer.getDepthTexture(), 0, 0, 0, frameBuffer.width, frameBuffer.height, 0);
            gameRenderState.guiRenderState.reset();

            GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(InertiaBase.mc, gameRenderState.guiRenderState, -999, -999);
            GuiRendererInterface guiRendererInterface = (GuiRendererInterface) guiRenderer;
            guiRendererInterface.setRenderTargetOverride(this.frameBuffer);
            guiRendererInterface.setProjectionOverride(new float[]{this.cachedNativeWidth, this.cachedNativeHeight});

            setNativeRender.accept(graphics);

            InertiaBase.mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
            gameRendererAccessor.setUseUiLightmap(true);
            guiRenderer.render();
            guiRenderer.endFrame();
            guiRendererInterface.setRenderTargetOverride(null);
            guiRendererInterface.setProjectionOverride(null);
            gameRendererAccessor.setUseUiLightmap(oldLightmap);
            RenderSystem.setShaderLights(oldLighting);

            RenderSystem.getDynamicUniforms().writeTransform(Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.projectionMatrix);
        }
        RenderSystem.setProjectionMatrix(oldProjectionMatrix, oldProjectionType);
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

        if (UIUtils.isUsingVulkan()) {
            this.image = SkiaVulkanInstance.createNativeImage(this.frameBuffer);
        } else {
            this.image = SkiaOpenGLInstance.createNativeImage(this.frameBuffer);
        }

        if (this.autoCleanup) {
            final Image nonReference = image;
            InertiaBase.CLEANER.register(this, () -> {
                InertiaBase.mc.executeIfPossible(() -> {
                    InertiaBase.LOGGER.info("deleted SkiaNativeRender skia image handle  {}", nonReference._ptr);
                    nonReference.close();
                });
            });
        }
    }

    public void delete() {
        this.frameBuffer.destroyBuffers();
        //does this actually delete the image/backend handle
        image.close();
    }

}

