package com.inertiaclient.base.utils.opengl;

import com.inertiaclient.base.InertiaBase;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import org.joml.*;

public class CoordinateDimensionTranslator {

    private static final Matrix4f cachedProjectionMatrix = new Matrix4f();
    private static final Matrix4f cachedWorldSpacePositionMatrix = new Matrix4f();

    private static float scaledWidth;
    private static float scaledHeight;


    public static void setMatrixInformation(Matrix4f worldPositionPositionMatrix) {
        CoordinateDimensionTranslator.cachedProjectionMatrix.set(RenderSystem.getProjectionMatrix());
        CoordinateDimensionTranslator.cachedWorldSpacePositionMatrix.set(worldPositionPositionMatrix);

        CoordinateDimensionTranslator.scaledWidth = InertiaBase.mc.getWindow().getGuiScaledWidth();
        CoordinateDimensionTranslator.scaledHeight = InertiaBase.mc.getWindow().getGuiScaledHeight();
    }

    //xyz should not subtract camera position
    public static ScreenPosition toScreen(double x, double y, double z) {
        Vector4f transformedCoordinates = new Vector4f((float) (x - InertiaBase.mc.getEntityRenderDispatcher().camera.getPosition().x), (float) (y - InertiaBase.mc.getEntityRenderDispatcher().camera.getPosition().y), (float) (z - InertiaBase.mc.getEntityRenderDispatcher().camera.getPosition().z), 1);
        transformedCoordinates.mul(CoordinateDimensionTranslator.cachedWorldSpacePositionMatrix);

        Vector3f projectionOutput = new Vector3f();
        CoordinateDimensionTranslator.cachedProjectionMatrix.project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), new int[]{0, 0, InertiaBase.mc.getWindow().getGuiScaledWidth(), InertiaBase.mc.getWindow().getGuiScaledHeight()}, projectionOutput);

        float screenXPosition = projectionOutput.x();
        float screenYPosition = InertiaBase.mc.getWindow().getGuiScaledHeight() - projectionOutput.y();
        float screenZPosition = projectionOutput.z();
        if (screenZPosition > 1) {
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
        Window window = InertiaBase.mc.getWindow();
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT);
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, 21000.0f);
        RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        //matrixStack.identity();
        matrixStack.translate(0.0f, 0.0f, -11000.0f);
        Lighting.setupFor3DItems();
    }

    public static void setupOverlayRendering(Runnable runnable) {
        Matrix4f oldProjectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
        ProjectionType oldProjectionType = RenderSystem.getProjectionType();
        Matrix4f oldModelViewMatrix = RenderSystem.getModelViewMatrix();

        //GameRenderer
        Window window = InertiaBase.mc.getWindow();
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT);
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, 21000.0f);
        RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        //matrixStack.identity();
        matrixStack.translate(0.0f, 0.0f, -11000.0f);
        Lighting.setupFor3DItems();

        runnable.run();

        //GameRenderer.render
        RenderSystem.setProjectionMatrix(oldProjectionMatrix, oldProjectionType);
        Matrix4fStack renderSystemMatrixStack = RenderSystem.getModelViewStack();
        renderSystemMatrixStack.identity();
        renderSystemMatrixStack.mul(oldModelViewMatrix);
        renderSystemMatrixStack.popMatrix();//pushed in CoordinateDimensionTranslator.setupOverlayRendering
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