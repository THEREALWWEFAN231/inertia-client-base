package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.mixins.accessors.FrustumAccessor;
import com.inertiaclient.base.value.impl.BooleanValue;
import com.inertiaclient.base.value.impl.ColorValue;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.*;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Optional;

import static com.inertiaclient.base.InertiaBase.mc;

public class WorldRenderUtils {

    public static final AABB FULL_BOX = new AABB(0, 0, 0, 1, 1, 1);

    public static final RenderPipeline DEBUG_TRIANGLE_FAN_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET).withLocation("pipeline/debug_triangle_fan_inertia").withCull(false).withDepthStencilState(Optional.empty()).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP).build());
    public static final RenderType DEBUG_TRIANGLE_FAN = RenderType.create("debug_triangle_fan_inertia", RenderSetup.builder(DEBUG_TRIANGLE_FAN_PIPELINE).createRenderSetup());
    public static final RenderType DEBUG_TRIANGLE_FAN_OUTLINE = RenderType.create("debug_triangle_fan_outline_inertia", RenderSetup.builder(DEBUG_TRIANGLE_FAN_PIPELINE).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).createRenderSetup());

    public static final RenderPipeline LINES_TRANSLUCENT_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET).withLocation("pipeline/lines_translucent_inertia").withCull(false).withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withDepthStencilState(Optional.empty()).build());
    public static final RenderType LINES_TRANSLUCENT = RenderType.create("lines_translucent_inertia", RenderSetup.builder(LINES_TRANSLUCENT_PIPELINE).createRenderSetup());


    private static final WorldRenderUtils.ESPBuilder ESP_BUILDER = new WorldRenderUtils.ESPBuilder();

    public static WorldRenderUtils.ESPBuilder getFreshESPBuilder() {
        ESP_BUILDER.setDefaults();
        return ESP_BUILDER;
    }

    //don't use this(I mean you can but there will be float precision errors when far from spawn /teleport IKnowImEZ 10000000 90 10000000)
    public static void subtractCameraPosition(PoseStack matrices) {
        matrices.translate(-mc.gameRenderer.mainCamera().position().x(), -mc.gameRenderer.mainCamera().position().y(), -mc.gameRenderer.mainCamera().position().z());
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

    public static Frustum getFrustum() {
        return mc.gameRenderer.mainCamera().getCullFrustum();
    }

    public static boolean isVisibleInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Frustum frustum = getFrustum();
        int result = ((FrustumAccessor) frustum).invokeCubeInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
        return result == FrustumIntersection.INSIDE || result == FrustumIntersection.INTERSECT;
    }

    public static boolean isEntityInFrustum(Entity entity) {
        Frustum frustum = getFrustum();
        return frustum.isVisible(entity.getBoundingBox());
    }

    public static boolean isBlockEntityInFrustum(BlockEntity blockEntity) {
        VoxelShape blockEntityCollisionShape = mc.level.getBlockState(blockEntity.getBlockPos()).getShape(mc.level, blockEntity.getBlockPos());
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

    public static ArrayList<BlockEntity> getAllBlockEntities() {
        ArrayList<BlockEntity> blockEntities = new ArrayList<>();

        int viewDistance = InertiaBase.mc.options.renderDistance().get();
        int playerChunkX = SectionPos.blockToSectionCoord(InertiaBase.mc.player.getBlockX());
        int playerChunkZ = SectionPos.blockToSectionCoord(InertiaBase.mc.player.getBlockZ());

        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                var chunk = mc.level.getChunk(playerChunkX + x, playerChunkZ + z);

                if (chunk != null) {
                    blockEntities.addAll(chunk.getBlockEntities().values());
                }
            }
        }

        return blockEntities;
    }

    @Accessors(chain = true)
    public static class ESPBuilder {

        //position and shape
        private double minX;
        private double minY;
        private double minZ;
        private double maxX;
        private double maxY;
        private double maxZ;

        @Setter
        private boolean fill;
        //minecraft uses int, so we will store ints as well... :sunglasses:
        @Setter
        private int fillRed;
        @Setter
        private int fillGreen;
        @Setter
        private int fillBlue;
        @Setter
        private int fillAlpha;
        @Setter
        private RenderType fillRenderType;
        @Setter
        private boolean outline;
        @Setter
        private float outlineLineWidth;
        @Setter
        private int outlineRed;
        @Setter
        private int outlineGreen;
        @Setter
        private int outlineBlue;
        @Setter
        private int outlineAlpha;
        @Setter
        private RenderType outlineRenderType;
        @Setter
        private boolean frustumCheck;
        @Setter
        private boolean shouldSubtractCamera;

        public static final float DEFAULT_FILL_ALPHA = .15f;
        public static final float DEFAULT_OUTLINE_ALPHA = .5f;

        public ESPBuilder() {
            this.setDefaults();
        }

        public ESPBuilder setDefaults() {
            //we dont really need to set positions, they should be set after this

            this.fill = true;
            this.fillRed = 255;
            this.fillGreen = 255;
            this.fillBlue = 255;
            this.fillAlpha = (int) (255 * DEFAULT_FILL_ALPHA);
            this.fillRenderType = WorldRenderUtils.DEBUG_TRIANGLE_FAN;
            this.outline = true;
            this.outlineLineWidth = 1.5f;
            this.outlineRed = 255;
            this.outlineGreen = 255;
            this.outlineBlue = 255;
            this.outlineAlpha = (int) (255 * DEFAULT_OUTLINE_ALPHA);
            this.outlineRenderType = WorldRenderUtils.LINES_TRANSLUCENT;


            this.frustumCheck = true;
            this.shouldSubtractCamera = true;
            return this;
        }

        public ESPBuilder basic(Entity entity, float delta, Color color) {
            return this.fromEntity(entity, delta).setFillColor(color).setOutlineColor(color);
        }

        public ESPBuilder fromEntity(Entity entity, float delta) {
            double renderX = WorldRenderUtils.getEntityInterpolatedX(entity, delta);
            double renderY = WorldRenderUtils.getEntityInterpolatedY(entity, delta);
            double renderZ = WorldRenderUtils.getEntityInterpolatedZ(entity, delta);

            float minSize = 0.05f;
            double xAdd = 0;
            double yAdd = 0;
            double zAdd = 0;
            if (entity.getBoundingBox().getXsize() < minSize) {
                xAdd = (minSize - entity.getBoundingBox().getXsize()) / 2;
            }
            if (entity.getBoundingBox().getYsize() < minSize) {
                yAdd = (minSize - entity.getBoundingBox().getYsize()) / 2;
            }
            if (entity.getBoundingBox().getZsize() < minSize) {
                zAdd = (minSize - entity.getBoundingBox().getZsize()) / 2;
            }

            this.minX = (entity.getBoundingBox().minX - xAdd) - entity.getX() + renderX;
            this.minY = (entity.getBoundingBox().minY - yAdd) - entity.getY() + renderY;
            this.minZ = (entity.getBoundingBox().minZ - zAdd) - entity.getZ() + renderZ;
            this.maxX = (entity.getBoundingBox().maxX + xAdd) - entity.getX() + renderX;
            this.maxY = (entity.getBoundingBox().maxY + yAdd) - entity.getY() + renderY;
            this.maxZ = (entity.getBoundingBox().maxZ + zAdd) - entity.getZ() + renderZ;
            return this;
        }

        public ESPBuilder fromBlockPos(BlockPos blockPos) {
            this.minX = blockPos.getX();
            this.minY = blockPos.getY();
            this.minZ = blockPos.getZ();
            this.maxX = this.minX + 1;
            this.maxY = this.minY + 1;
            this.maxZ = this.minZ + 1;
            return this;
        }

        public ESPBuilder fromPosAndAABB(BlockPos blockPos, AABB box) {
            this.minX = blockPos.getX() + box.minX;
            this.minY = blockPos.getY() + box.minY;
            this.minZ = blockPos.getZ() + box.minZ;
            this.maxX = blockPos.getX() + box.maxX;
            this.maxY = blockPos.getY() + box.maxY;
            this.maxZ = blockPos.getZ() + box.maxZ;
            return this;
        }

        public ESPBuilder fromAABB(AABB box) {
            return this.setMinMax(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        }

        public ESPBuilder setMinMax(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            return this;
        }

        public ESPBuilder setRawFillColor(Color color) {
            this.fillRed = color.getRed();
            this.fillGreen = color.getGreen();
            this.fillBlue = color.getBlue();
            this.fillAlpha = color.getAlpha();
            return this;
        }

        //sets color but multiplies alpha by DEFAULT_FILL_ALPHA
        public ESPBuilder setFillColor(Color color) {
            this.fillRed = color.getRed();
            this.fillGreen = color.getGreen();
            this.fillBlue = color.getBlue();
            this.fillAlpha = (int) (color.getAlpha() * DEFAULT_FILL_ALPHA);
            return this;
        }

        public ESPBuilder setRawOutlineColor(Color color) {
            this.outlineRed = color.getRed();
            this.outlineGreen = color.getGreen();
            this.outlineBlue = color.getBlue();
            this.outlineAlpha = color.getAlpha();
            return this;
        }

        //sets color but multiplies alpha by DEFAULT_FILL_ALPHA
        public ESPBuilder setOutlineColor(Color color) {
            this.outlineRed = color.getRed();
            this.outlineGreen = color.getGreen();
            this.outlineBlue = color.getBlue();
            this.outlineAlpha = (int) (color.getAlpha() * DEFAULT_OUTLINE_ALPHA);
            return this;
        }

        public ESPBuilder setColor(Color color) {
            return this.setFillColor(color).setOutlineColor(color);
        }

        public ESPBuilder setColor(ColorValue color) {
            return this.setColor(color.getValue().getRenderColor());
        }

        public ESPBuilder setFill(boolean fill) {
            this.fill = fill;
            return this;
        }

        public ESPBuilder setOutline(boolean outline) {
            this.outline = outline;
            return this;
        }

        public ESPBuilder setFill(BooleanValue fill) {
            this.fill = fill.getValue();
            return this;
        }

        public ESPBuilder setOutline(BooleanValue outline) {
            this.outline = outline.getValue();
            return this;
        }

        public void draw(PoseStack poseStack, SubmitNodeStorage submitNodeStorage) {
            if (this.isVisibleInFrustum(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ) && (this.fill || this.outline)) {
                double renderMinX = this.minX;
                double renderMinY = this.minY;
                double renderMinZ = this.minZ;
                double renderMaxX = this.maxX;
                double renderMaxY = this.maxY;
                double renderMaxZ = this.maxZ;

                if (this.shouldSubtractCamera) {
                    renderMinX -= mc.gameRenderer.mainCamera().position().x();
                    renderMinY -= mc.gameRenderer.mainCamera().position().y();
                    renderMinZ -= mc.gameRenderer.mainCamera().position().z();
                    renderMaxX -= mc.gameRenderer.mainCamera().position().x();
                    renderMaxY -= mc.gameRenderer.mainCamera().position().y();
                    renderMaxZ -= mc.gameRenderer.mainCamera().position().z();
                }

                float finalMinX = (float) renderMinX;
                float finalMinY = (float) renderMinY;
                float finalMinZ = (float) renderMinZ;
                float finalMaxX = (float) renderMaxX;
                float finalMaxY = (float) renderMaxY;
                float finalMaxZ = (float) renderMaxZ;

                if (this.fill) {
                    int finalRed = this.fillRed;
                    int finalGreen = this.fillGreen;
                    int finalBlue = this.fillBlue;
                    int finalAlpha = this.fillAlpha;

                    submitNodeStorage.submitCustomGeometry(poseStack, this.fillRenderType, (pose, buffer) -> {
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha);
                    });
                }

                if (this.outline) {
                    int finalRed = this.outlineRed;
                    int finalGreen = this.outlineGreen;
                    int finalBlue = this.outlineBlue;
                    int finalAlpha = this.outlineAlpha;

                    float lineWidth = this.outlineLineWidth;
                    ;

                    submitNodeStorage.submitCustomGeometry(poseStack, this.outlineRenderType, (pose, buffer) -> {
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMinX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMinY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMinZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                        buffer.addVertex(pose, finalMaxX, finalMaxY, finalMaxZ).setColor(finalRed, finalGreen, finalBlue, finalAlpha).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
                    });
                }
            }
        }

        private boolean isVisibleInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            if (!this.frustumCheck) {
                return true;
            }
            return WorldRenderUtils.isVisibleInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
        }

    }


}
