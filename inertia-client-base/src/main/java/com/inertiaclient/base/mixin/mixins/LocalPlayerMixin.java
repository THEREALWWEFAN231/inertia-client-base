package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "tick", at = @At(target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V", value = "INVOKE"))
    public void playerUpdateInjection(CallbackInfo callbackInfo) {
        EventManager.fire(new PlayerUpdateEvent());
    }

    private PlayerPreMotionServerUpdateEvent preMotionServerUpdate;

    //TODO: fix when riding, we don't change the yaw pitch onGround, like sendMovementPackets
    @Inject(method = "tick", at = @At(target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z", value = "INVOKE_ASSIGN", shift = At.Shift.AFTER), cancellable = true)
    public void preMotionRidingInjection(CallbackInfo callbackInfo) {
        LocalPlayer player = ((LocalPlayer) (Object) this);
        PlayerPreMotionServerUpdateEvent event = new PlayerPreMotionServerUpdateEvent(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround());
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

    @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    public void sendMovementPacketsHead(CallbackInfo callbackInfo) {
        LocalPlayer player = ((LocalPlayer) (Object) this);
        this.preMotionServerUpdate = new PlayerPreMotionServerUpdateEvent(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround());
        EventManager.fire(this.preMotionServerUpdate);
        if (this.preMotionServerUpdate.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    //change xyz without redirecting!! should work "better" with mod support
    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"))
    private double modifyPlayerX(double old) {
        return this.preMotionServerUpdate.getX();
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"))
    private double modifyPlayerY(double old) {
        return this.preMotionServerUpdate.getY();
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"))
    private double modifyPlayerZ(double old) {
        return this.preMotionServerUpdate.getZ();
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"))
    private float modifyPlayerYaw(float old) {
        return this.preMotionServerUpdate.getYaw();
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"))
    private float modifyPlayerPitch(float old) {
        return this.preMotionServerUpdate.getPitch();
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;onGround()Z"))
    private boolean modifyPlayerOnGround(boolean old) {
        return this.preMotionServerUpdate.isOnGround();
    }

    @Inject(method = "sendPosition", at = @At("RETURN"), cancellable = true)
    public void sendMovementPacketsReturn(CallbackInfo callbackInfo) {
        PlayerPostMotionServerUpdateEvent event = new PlayerPostMotionServerUpdateEvent();
        EventManager.fire(event);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType movementType, Vec3 movement, CallbackInfo callbackInfo, @Local LocalRef<Vec3> movementRef) {
        MoveEvent moveEvent = new MoveEvent(movement.x(), movement.y(), movement.z());
        EventManager.fire(moveEvent);

        if (moveEvent.isCancelled()) {
            movementRef.set(new Vec3(moveEvent.getX(), moveEvent.getY(), moveEvent.getZ()));
        }
    }


}
