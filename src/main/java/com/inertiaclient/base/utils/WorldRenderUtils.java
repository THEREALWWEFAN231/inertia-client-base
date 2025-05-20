package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.custominterfaces.BlockEntityRenderDispatcherInterface;
import com.inertiaclient.base.mixin.custominterfaces.WorldRendererInterface;
import com.inertiaclient.base.mixin.mixins.accessors.FrustumAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.Stack;

import static com.inertiaclient.base.InertiaBase.mc;

public class WorldRenderUtils {//3d render utils

    public static final Box FULL_BOX = new Box(0, 0, 0, 1, 1, 1);
    public static boolean frustumCheck = true;//probably wont use but you can disable and reenable it
    @Getter
    private static Stack<Boolean> subtractCamera = new Stack<>();

    //don't use this(I mean you can but there will be float precision errors when far from spawn /teleport IKnowImEZ 10000000 90 10000000)
    public static void subtractCameraPosition(MatrixStack matrices) {
        matrices.translate(-mc.gameRenderer.getCamera().getPos().getX(), -mc.gameRenderer.getCamera().getPos().getY(), -mc.gameRenderer.getCamera().getPos().getZ());
    }

    public static double getEntityInterpolatedX(Entity entity, float delta) {

        double posX = 0;
        if (entity.age == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posX = entity.getX();
        } else {
            posX = entity.lastRenderX + ((entity.getX() - entity.lastRenderX) * delta);
        }

        return posX;
    }

    public static double getEntityInterpolatedY(Entity entity, float delta) {

        double posY = 0;
        if (entity.age == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posY = entity.getY();
        } else {
            posY = entity.lastRenderY + ((entity.getY() - entity.lastRenderY) * delta);
        }

        return posY;
    }

    public static double getEntityInterpolatedZ(Entity entity, float delta) {

        double posZ = 0;
        if (entity.age == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            posZ = entity.getZ();
        } else {
            posZ = entity.lastRenderZ + ((entity.getZ() - entity.lastRenderZ) * delta);
        }

        return posZ;
    }

    public static float getEntityInterpolatedYaw(Entity entity, float delta) {
        if (entity.age == 0) {//if the entity was just spawned, it doesn't have lastTickPosX, this renders better/nicer
            return entity.getYaw();
        }
        return entity.prevYaw + ((entity.getYaw() - entity.prevYaw) * delta);
    }

    public static void enableGL() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
    }

    public static void drawEntityESP(MatrixStack matrices, Entity entity, float delta, boolean fill, boolean outline, Color color) {
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


    public static void drawBoxESP(MatrixStack matrices, Box box, boolean fill, boolean outline, Color color) {
        WorldRenderUtils.drawBoxESP(matrices, BlockPos.ORIGIN, box, fill, outline, color);
    }

    public static void drawBoxESP(MatrixStack matrices, BlockPos blockPos, Box box, boolean fill, boolean outline, Color color) {
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

    public static void renderBoxRaw(MatrixStack matrices, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        if (isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (shouldSubtractCameraPosition()) {
                minX -= mc.gameRenderer.getCamera().getPos().getX();
                minY -= mc.gameRenderer.getCamera().getPos().getY();
                minZ -= mc.gameRenderer.getCamera().getPos().getZ();
                maxX -= mc.gameRenderer.getCamera().getPos().getX();
                maxY -= mc.gameRenderer.getCamera().getPos().getY();
                maxZ -= mc.gameRenderer.getCamera().getPos().getZ();
            }

            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

            Tessellator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            VertexRendering.drawFilledBox(matrices, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }
    }

    public static void renderOutlineRaw(MatrixStack matrices, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float lineWidth, float red, float green, float blue, float alpha) {
        if (isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (shouldSubtractCameraPosition()) {
                minX -= mc.gameRenderer.getCamera().getPos().getX();
                minY -= mc.gameRenderer.getCamera().getPos().getY();
                minZ -= mc.gameRenderer.getCamera().getPos().getZ();
                maxX -= mc.gameRenderer.getCamera().getPos().getX();
                maxY -= mc.gameRenderer.getCamera().getPos().getY();
                maxZ -= mc.gameRenderer.getCamera().getPos().getZ();
            }

            RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
            RenderSystem.lineWidth(lineWidth);

            Tessellator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            VertexRendering.drawBox(matrices, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }
    }

    //allows rendering more than 64 blocks
    public static <T extends BlockEntity> void renderBlockEntity(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        ((BlockEntityRenderDispatcherInterface) mc.getBlockEntityRenderDispatcher()).invokeRender(renderer, blockEntity, tickDelta, matrices, vertexConsumers);
    }

    public static Frustum getFrustum() {
        return ((WorldRendererInterface) InertiaBase.mc.worldRenderer).getFrustum();
    }

    public static boolean isVisibleInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (!frustumCheck) {
            return true;
        }
        Frustum frustum = getFrustum();
        int result = ((FrustumAccessor) frustum).invokeIntersectAab(minX, minY, minZ, maxX, maxY, maxZ);
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
        VoxelShape blockEntityCollisionShape = mc.world.getBlockState(blockEntity.getPos()).getCollisionShape(mc.world, blockEntity.getPos());
        if (blockEntityCollisionShape == VoxelShapes.empty()) {
            return false;
        }

        Box box = blockEntityCollisionShape.getBoundingBox().offset(blockEntity.getPos());
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
