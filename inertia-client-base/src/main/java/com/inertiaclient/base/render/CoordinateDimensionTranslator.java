package com.inertiaclient.base.render;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.mixins.accessors.GameRendererAccessor;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.*;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.GameRenderState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Consumer;

//TODO: fixme
public class CoordinateDimensionTranslator {

    private static final Matrix4f cachedProjectionMatrix = new Matrix4f();
    private static final Matrix4f cachedWorldSpacePositionMatrix = new Matrix4f();

    private static float scaledWidth;
    private static float scaledHeight;


    public static void setMatrixInformation(Matrix4f viewRotationMatrix, Matrix4f projectionMatrix) {
        CoordinateDimensionTranslator.cachedProjectionMatrix.set(projectionMatrix);
        CoordinateDimensionTranslator.cachedWorldSpacePositionMatrix.set(viewRotationMatrix);

        CoordinateDimensionTranslator.scaledWidth = InertiaBase.mc.getWindow().getGuiScaledWidth();
        CoordinateDimensionTranslator.scaledHeight = InertiaBase.mc.getWindow().getGuiScaledHeight();
    }

    //xyz should not subtract camera position
    public static ScreenPosition toScreen(double x, double y, double z) {
        Vector4f transformedCoordinates = new Vector4f((float) (x - InertiaBase.mc.gameRenderer.mainCamera().position().x), (float) (y - InertiaBase.mc.gameRenderer.mainCamera().position().y), (float) (z - InertiaBase.mc.gameRenderer.mainCamera().position().z), 1);
        transformedCoordinates.mul(CoordinateDimensionTranslator.cachedWorldSpacePositionMatrix);

        Vector3f projectionOutput = new Vector3f();
        CoordinateDimensionTranslator.cachedProjectionMatrix.project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), new int[]{0, 0, InertiaBase.mc.getWindow().getGuiScaledWidth(), InertiaBase.mc.getWindow().getGuiScaledHeight()}, projectionOutput);

        float screenXPosition = projectionOutput.x();
        float screenYPosition = InertiaBase.mc.getWindow().getGuiScaledHeight() - projectionOutput.y();
        float screenZPosition = projectionOutput.z();
        if (screenZPosition < 0.5f) {
            int bigFactor = 100000;
            screenXPosition = InertiaBase.mc.getWindow().getGuiScaledWidth() - screenXPosition;
            screenYPosition = InertiaBase.mc.getWindow().getGuiScaledHeight() - screenYPosition;
            screenXPosition *= bigFactor;
            screenYPosition *= bigFactor;
        }

        boolean isPositionOnTheScreen = true;
        if (screenXPosition < 0 || screenYPosition < 0 || screenXPosition > scaledWidth || screenYPosition > scaledHeight) {
            isPositionOnTheScreen = false;
        }

        return new ScreenPosition(screenXPosition, screenYPosition, screenZPosition, isPositionOnTheScreen);
    }

    public static void setupOverlayRendering() {
        //GameRenderer
        /*Window window = InertiaBase.mc.getWindow();
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT);
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, 21000.0f);
        RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        //matrixStack.identity();
        matrixStack.translate(0.0f, 0.0f, -11000.0f);
        Lighting.setupFor3DItems();*/
    }

    private static final Projection guiProjection = new Projection();
    private static final ProjectionMatrixBuffer guiProjectionMatrixBuffer = new ProjectionMatrixBuffer("gui");

    private static final GameRenderState gameRenderState = new GameRenderState();
    private static GuiRenderer guiRenderer;
    private static FeatureRenderDispatcher featureRenderDispatcher;

    public static void setupOverlayRendering(Consumer<GuiGraphicsExtractor> runnable) {
        if (guiRenderer == null) {
            featureRenderDispatcher = new FeatureRenderDispatcher(InertiaBase.mc.gameRenderer.renderBuffers(), InertiaBase.mc.getModelManager(), InertiaBase.mc.getAtlasManager(), InertiaBase.mc.font, gameRenderState);
            guiRenderer = new GuiRenderer(gameRenderState.guiRenderState, featureRenderDispatcher, List.of(new GuiEntityRenderer(Minecraft.getInstance().getEntityRenderDispatcher()), new GuiSkinRenderer(), new GuiBookModelRenderer(), new GuiBannerResultRenderer(InertiaBase.mc.getAtlasManager()), new GuiProfilerChartRenderer()));
        }

        GameRendererAccessor gameRendererAccessor = (GameRendererAccessor) InertiaBase.mc.gameRenderer;
        var oldProjectionMatrix = RenderSystem.getProjectionMatrixBuffer();
        var oldProjectionType = RenderSystem.getProjectionType();
        boolean oldLightmap = gameRendererAccessor.getUseUiLightmap();
        var oldLighting = RenderSystem.getShaderLights();

        gameRenderState.guiRenderState.reset();
        GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(InertiaBase.mc, gameRenderState.guiRenderState, -999, -999);

        runnable.accept(graphics);

        InertiaBase.mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
        gameRendererAccessor.setUseUiLightmap(true);
        //this sets projection
        guiRenderer.render();
        guiRenderer.endFrame();
        gameRendererAccessor.setUseUiLightmap(oldLightmap);
        RenderSystem.setShaderLights(oldLighting);

        RenderSystem.setProjectionMatrix(oldProjectionMatrix, oldProjectionType);

    }

    public static class ScreenPosition {

        @Getter
        private float x;
        @Getter
        private float y;
        @Getter
        private float w;
        @Getter
        private boolean isPositionOnTheScreen;

        public ScreenPosition(float x, float y, float w, boolean isPositionOnTheScreen) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.isPositionOnTheScreen = isPositionOnTheScreen;
        }
    }
}