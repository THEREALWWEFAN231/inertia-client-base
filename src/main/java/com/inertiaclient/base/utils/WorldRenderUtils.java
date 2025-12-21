package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.custominterfaces.BlockEntityRenderDispatcherInterface;
import com.inertiaclient.base.mixin.custominterfaces.WorldRendererInterface;
import com.inertiaclient.base.mixin.mixins.accessors.FrustumAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.Getter;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.Stack;

import static com.inertiaclient.base.InertiaBase.mc;

public class WorldRenderUtils {//3d render utils

    public static final AABB FULL_BOX = new AABB(0, 0, 0, 1, 1, 1);
    public static boolean frustumCheck = true;//probably wont use but you can disable and reenable it
    @Getter
    private static Stack<Boolean> subtractCamera = new Stack<>();

    //don't use this(I mean you can but there will be float precision errors when far from spawn /teleport IKnowImEZ 10000000 90 10000000)
    public static void subtractCameraPosition(PoseStack matrices) {
        matrices.translate(-mc.gameRenderer.getMainCamera().getPosition().x(), -mc.gameRenderer.getMainCamera().getPosition().y(), -mc.gameRenderer.getMainCamera().getPosition().z());
    }

    public static double getEntityInterpolatedX(Entity entity, float delta) {

        double posX = 0;
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posX = entity.getX();
        } else {
            posX = entity.xOld + ((entity.getX() - entity.xOld) * delta);
        }

        return posX;
    }

    public static double getEntityInterpolatedY(Entity entity, float delta) {

        double posY = 0;
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posY = entity.getY();
        } else {
            posY = entity.yOld + ((entity.getY() - entity.yOld) * delta);
        }

        return posY;
    }

    public static double getEntityInterpolatedZ(Entity entity, float delta) {

        double posZ = 0;
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posZ = entity.getZ();
        } else {
            posZ = entity.zOld + ((entity.getZ() - entity.zOld) * delta);
        }

        return posZ;
    }

    public static float getEntityInterpolatedYaw(Entity entity, float delta) {
        if (entity.tickCount == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            return entity.getYRot();
        }
        return entity.yRotO + ((entity.getYRot() - entity.yRotO) * delta);
    }

    public static void enableGL() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
    }

    public static void drawEntityESP(PoseStack matrices, Entity entity, float delta, boolean fill, boolean outline, Color color) {
        double renderX = getEntityInterpolatedX(entity, delta);
        double renderY = getEntityInterpolatedY(entity, delta);
        double renderZ = getEntityInterpolatedZ(entity, delta);


        double minX = entity.getBoundingBox().minX - entity.getX() + renderX;
        double minY = entity.getBoundingBox().minY - entity.getY() + renderY;
        double minZ = entity.getBoundingBox().minZ - entity.getZ() + renderZ;
        double maxX = entity.getBoundingBox().maxX - entity.getX() + renderX;
        double maxY = entity.getBoundingBox().maxY - entity.getY() + renderY;
        double maxZ = entity.getBoundingBox().maxZ - entity.getZ() + renderZ;
        if (fill) {
            renderBoxRaw(matrices, minX, minY, minZ, maxX, maxY, maxZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.15f);
        }
        if (outline) {
            renderOutlineRaw(matrices, minX, minY, minZ, maxX, maxY, maxZ, 1.5f, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);
        }
    }


    public static void drawBoxESP(PoseStack matrices, AABB box, boolean fill, boolean outline, Color color) {
        WorldRenderUtils.drawBoxESP(matrices, BlockPos.ZERO, box, fill, outline, color);
    }

    public static void drawBoxESP(PoseStack matrices, BlockPos blockPos, AABB box, boolean fill, boolean outline, Color color) {
        double minX = blockPos.getX() + box.minX;
        double minY = blockPos.getY() + box.minY;
        double minZ = blockPos.getZ() + box.minZ;
        double maxX = blockPos.getX() + box.maxX;
        double maxY = blockPos.getY() + box.maxY;
        double maxZ = blockPos.getZ() + box.maxZ;
        if (fill) {
            renderBoxRaw(matrices, minX, minY, minZ, maxX, maxY, maxZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.15f);
        }
        if (outline) {
            renderOutlineRaw(matrices, minX, minY, minZ, maxX, maxY, maxZ, 1.5f, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);
        }
    }

    public static void renderBoxRaw(PoseStack matrices, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        if (isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (shouldSubtractCameraPosition()) {
                minX -= mc.gameRenderer.getMainCamera().getPosition().x();
                minY -= mc.gameRenderer.getMainCamera().getPosition().y();
                minZ -= mc.gameRenderer.getMainCamera().getPosition().z();
                maxX -= mc.gameRenderer.getMainCamera().getPosition().x();
                maxY -= mc.gameRenderer.getMainCamera().getPosition().y();
                maxZ -= mc.gameRenderer.getMainCamera().getPosition().z();
            }

            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(CoreShaders.POSITION_COLOR);

            Tesselator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            ShapeRenderer.addChainedFilledBoxVertices(matrices, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        }
    }

    public static void renderOutlineRaw(PoseStack matrices, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float lineWidth, float red, float green, float blue, float alpha) {
        if (isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (shouldSubtractCameraPosition()) {
                minX -= mc.gameRenderer.getMainCamera().getPosition().x();
                minY -= mc.gameRenderer.getMainCamera().getPosition().y();
                minZ -= mc.gameRenderer.getMainCamera().getPosition().z();
                maxX -= mc.gameRenderer.getMainCamera().getPosition().x();
                maxY -= mc.gameRenderer.getMainCamera().getPosition().y();
                maxZ -= mc.gameRenderer.getMainCamera().getPosition().z();
            }

            RenderSystem.setShader(CoreShaders.RENDERTYPE_LINES);
            RenderSystem.lineWidth(lineWidth);

            Tesselator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            ShapeRenderer.renderLineBox(matrices, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        }
    }

    //allows rendering more than 64 blocks
    public static <T extends BlockEntity> void renderBlockEntity(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers) {
        ((BlockEntityRenderDispatcherInterface) mc.getBlockEntityRenderDispatcher()).invokeRender(renderer, blockEntity, tickDelta, matrices, vertexConsumers);
    }

    public static Frustum getFrustum() {
        return ((WorldRendererInterface) InertiaBase.mc.levelRenderer).getFrustum();
    }

    public static boolean isVisibleInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (!frustumCheck) {
            return true;
        }
        Frustum frustum = getFrustum();
        int result = ((FrustumAccessor) frustum).invokeCubeInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
        return result == FrustumIntersection.INSIDE || result == FrustumIntersection.INTERSECT;
    }

    public static boolean isEntityInFrustum(Entity entity) {
        if (!frustumCheck) {
            return true;
        }
        Frustum frustum = getFrustum();
        return frustum.isVisible(entity.getBoundingBox());
    }

    public static boolean isBlockEntityInFrustum(BlockEntity blockEntity) {
        VoxelShape blockEntityCollisionShape = mc.level.getBlockState(blockEntity.getBlockPos()).getCollisionShape(mc.level, blockEntity.getBlockPos());
        if (blockEntityCollisionShape == Shapes.empty()) {
            return false;
        }

        AABB box = blockEntityCollisionShape.bounds().move(blockEntity.getBlockPos());
        return isVisibleInFrustum(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static Vector3f getNormalsForLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        //code was modified from chat gbt
        double xDiff = (x2 - x1);
        double yDiff = (y2 - y1);
        double zDiff = (z2 - z1);
        double length = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
        xDiff /= length;
        yDiff /= length;
        zDiff /= length;
        return new Vector3f((float) xDiff, (float) yDiff, (float) zDiff);
    }

    private static boolean shouldSubtractCameraPosition() {
        if (subtractCamera.empty()) {
            return true;
        }
        return subtractCamera.peek();
    }


}
