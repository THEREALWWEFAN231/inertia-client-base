package com.inertiaclient.base.mixin.custominterfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public interface BlockEntityRenderDispatcherInterface {

    <S extends BlockEntityRenderState> void invokeRender(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera);

}
