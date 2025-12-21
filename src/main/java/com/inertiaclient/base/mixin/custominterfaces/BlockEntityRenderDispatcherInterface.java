package com.inertiaclient.base.mixin.custominterfaces;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

public interface BlockEntityRenderDispatcherInterface {

    <T extends BlockEntity> void invokeRender(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers);

}
