package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At(target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", value = "INVOKE"))
    public void playerUpdateInjection(CallbackInfo callbackInfo) {
        EventManager.fire(new PlayerUpdateEvent());
    }

    private PlayerPreMotionServerUpdateEvent preMotionServerUpdate;

    //TODO: fix when riding, we don't change the yaw pitch onGround, like sendMovementPackets
    @Inject(method = "tick", at = @At(target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", value = "INVOKE_ASSIGN", shift = At.Shift.AFTER), cancellable = true)
    public void preMotionRidingInjection(CallbackInfo callbackInfo) {
        ClientPlayerEntity player = ((ClientPlayerEntity) (Object) this);
        PlayerPreMotionServerUpdateEvent event = new PlayerPreMotionServerUpdateEvent(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround());
        EventManager.fire(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    //inject at the end of the hasVehicle statement, in the statement
    @Inject(method = "tick", at = @At(target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", value = "JUMP", opcode = Opcodes.GOTO, ordinal = 0))
    public void postMotionRidingInjection(CallbackInfo callbackInfo) {
        PlayerPostMotionServerUpdateEvent event = new PlayerPostMotionServerUpdateEvent();
        EventManager.fire(event);
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    public void sendMovementPacketsHead(CallbackInfo callbackInfo) {
        ClientPlayerEntity player = ((ClientPlayerEntity) (Object) this);
        this.preMotionServerUpdate = new PlayerPreMotionServerUpdateEvent(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround());
        EventManager.fire(this.preMotionServerUpdate);
        if (this.preMotionServerUpdate.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    //change xyz without redirecting!! should work "better" with mod support
    @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"))
    private double modifyPlayerX(double old) {
        return this.preMotionServerUpdate.getX();
    }

    @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"))
    private double modifyPlayerY(double old) {
        return this.preMotionServerUpdate.getY();
    }

    @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"))
    private double modifyPlayerZ(double old) {
        return this.preMotionServerUpdate.getZ();
    }

    @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float modifyPlayerYaw(float old) {
        return this.preMotionServerUpdate.getYaw();
    }

    @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float modifyPlayerPitch(float old) {
        return this.preMotionServerUpdate.getPitch();
    }

    @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"))
    private boolean modifyPlayerOnGround(boolean old) {
        return this.preMotionServerUpdate.isOnGround();
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"), cancellable = true)
    public void sendMovementPacketsReturn(CallbackInfo callbackInfo) {
        PlayerPostMotionServerUpdateEvent event = new PlayerPostMotionServerUpdateEvent();
        EventManager.fire(event);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType movementType, Vec3d movement, CallbackInfo callbackInfo, @Local LocalRef<Vec3d> movementRef) {
        MoveEvent moveEvent = new MoveEvent(movement.getX(), movement.getY(), movement.getZ());
        EventManager.fire(moveEvent);

        if (moveEvent.isCancelled()) {
            movementRef.set(new Vec3d(moveEvent.getX(), moveEvent.getY(), moveEvent.getZ()));
        }
    }


}
