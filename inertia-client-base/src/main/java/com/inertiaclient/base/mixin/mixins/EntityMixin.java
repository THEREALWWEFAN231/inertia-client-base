package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.EntityInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInterface {

    @Unique
    private float inertiaclient$fallDistance;


    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    public void move(MoverType type, Vec3 movement, CallbackInfo callbackInfo, @Local(ordinal = 1) Vec3 vec3d) {
        double heightDifference = vec3d.y;
        if (((Entity) (Object) this).onGround()) {
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
