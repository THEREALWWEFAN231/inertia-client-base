package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.BlockCollisionShapeEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    protected abstract BlockState asBlockState();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> callback) {
        if (world != null && !(world instanceof EmptyBlockView)) {//this isn't needed tbh but wont fire on block cache
            BlockCollisionShapeEvent blockCollisionShapeEvent = new BlockCollisionShapeEvent(world, pos, context, this.asBlockState());
            EventManager.fire(blockCollisionShapeEvent);
            if (blockCollisionShapeEvent.isCancelled() && blockCollisionShapeEvent.getVoxelShape() != null) {
                callback.setReturnValue(blockCollisionShapeEvent.getVoxelShape());
            }
        }
    }

}
