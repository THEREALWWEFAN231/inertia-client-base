package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.EntityInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInterface {

    @Unique
    private float inertiaclient$fallDistance;


    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    public void move(MovementType type, Vec3d movement, CallbackInfo callbackInfo, @Local(ordinal = 1) Vec3d vec3d) {
        double heightDifference = vec3d.y;
        if (((Entity) (Object) this).isOnGround()) {
            this.inertiaclient$fallDistance = 0;
        } else if (heightDifference < 0.0) {
            this.inertiaclient$fallDistance -= (float) heightDifference;
        }
    }

    @Override
    public float inertiaclient$getFallDistance() {
        return this.inertiaclient$fallDistance;
    }

}
