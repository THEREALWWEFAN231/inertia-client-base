package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.BlockEntityRenderDispatcherInterface;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin implements BlockEntityRenderDispatcherInterface {

    @Shadow
    private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers) {

    }

    //for some reason using @Invoker on this causes hotswap to stop working(apparently because its static)... so we do this....
    @Override
    public <T extends BlockEntity> void invokeRender(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers) {
        this.setupAndRender(renderer, blockEntity, tickDelta, matrices, vertexConsumers);
    }
}
