package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.BlockCollisionShapeEvent;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback) {
        if (world != null && !(world instanceof EmptyBlockGetter)) {//this isn't needed tbh but wont fire on block cache
            BlockCollisionShapeEvent blockCollisionShapeEvent = new BlockCollisionShapeEvent(world, pos, context, this.asState());
            EventManager.fire(blockCollisionShapeEvent);
            if (blockCollisionShapeEvent.isCancelled() && blockCollisionShapeEvent.getVoxelShape() != null) {
                callback.setReturnValue(blockCollisionShapeEvent.getVoxelShape());
            }
        }
    }

}
