package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.Settings;
import com.inertiaclient.base.mixin.custominterfaces.EntityRenderStateInterface;
import com.inertiaclient.base.utils.RotationUtils;
import com.inertiaclient.base.value.impl.ModeValue;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    private Entity currentRenderingEntity;
    private ModeValue clientRotationDisplay = InertiaBase.instance.getSettings().getClientRotationDisplay();

    @Inject(method = "render", at = @At("HEAD"))
    public void renderHead(S livingEntityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
        this.currentRenderingEntity = EntityRenderStateInterface.getEntity(livingEntityRenderState);
        RotationUtils.getRotationTimer().update();//TODO: put this in game renderer, so its not updated for every single living entity
    }


    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;solveBodyRot(Lnet/minecraft/world/entity/LivingEntity;FF)F"))
    private float changeBodyYaw(float original) {
        if (this.clientRotationDisplay.getValue() != Settings.ClientRotationDisplay.NONE && this.currentRenderingEntity instanceof LocalPlayer && this.isRotationTimerValid()) {
            if (this.clientRotationDisplay.getValue() == Settings.ClientRotationDisplay.WHOLE_BODY) {
                return RotationUtils.getPlayerYaw();
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;rotLerp(FFF)F"))
    private float changeYaw(float original) {
        if (this.clientRotationDisplay.getValue() != Settings.ClientRotationDisplay.NONE && this.currentRenderingEntity instanceof LocalPlayer && this.isRotationTimerValid()) {
            return RotationUtils.getPlayerYaw();
        }
        return original;
    }

    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getXRot(F)F"))
    private float changePitch(float original) {
        if (this.clientRotationDisplay.getValue() != Settings.ClientRotationDisplay.NONE && this.currentRenderingEntity instanceof LocalPlayer && this.isRotationTimerValid()) {
            return RotationUtils.getPlayerPitch();
        }
        return original;
    }

    @Unique
    private boolean isRotationTimerValid() {
        return !RotationUtils.getRotationTimer().hasDelayRun(250);
    }

}
