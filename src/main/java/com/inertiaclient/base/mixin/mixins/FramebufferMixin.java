package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.FrameBufferWriteEvent;
import com.inertiaclient.base.mixin.custominterfaces.FrameBufferInterface;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//https://github.com/MeshMC/mesh/blob/818de474628184ba6aa393844350c849e83e0e70/mesh-fabric-1.18.2/src/main/java/net/meshmc/mesh/impl/mixin/render/MixinFramebuffer.java#L14
@Mixin(Framebuffer.class)
public class FramebufferMixin {

    // enable stencil testing, for nanovg, maybe only do this for "certain" frame buffers?, for example create a frame buffer just for nanovg, and only that frame buffer does this
    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0))
    public void stencil(Args args) {
        if (shouldEnableStencil()) {
            args.set(2, GL30.GL_DEPTH32F_STENCIL8);
            args.set(6, GL30.GL_DEPTH_STENCIL);
            args.set(7, GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV);
        }
    }

    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V"), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/gl/Framebuffer;useDepthAttachment:Z", ordinal = 1)))
    public void stencil1(Args args) {
        if (shouldEnableStencil()) {
            args.set(1, GL30.GL_DEPTH_STENCIL_ATTACHMENT);
        }
    }

    @Inject(method = "beginWrite", at = @At("HEAD"), cancellable = true)
    public void beginWrite(boolean setViewport, CallbackInfo callbackInfo) {
        FrameBufferWriteEvent frameBufferWriteEvent = new FrameBufferWriteEvent((Framebuffer) (Object) this);
        EventManager.fire(frameBufferWriteEvent);
        if (frameBufferWriteEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    private boolean shouldEnableStencil() {
        return this instanceof FrameBufferInterface && ((FrameBufferInterface) this).shouldEnableStencil();
    }

}
