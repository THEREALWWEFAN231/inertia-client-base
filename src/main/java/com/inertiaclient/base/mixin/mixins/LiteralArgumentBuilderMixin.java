package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.LiteralCommandNodeInterface;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiteralArgumentBuilder.class)
public class LiteralArgumentBuilderMixin<S> implements LiteralCommandNodeInterface {

    @Unique
    private Message inertiaMessage;

    @Inject(method = "build()Lcom/mojang/brigadier/tree/LiteralCommandNode;", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;getArguments()Ljava/util/Collection;"), remap = false)
    public void build(CallbackInfoReturnable<LiteralCommandNode> callbackInfoReturnable, @Local LocalRef<LiteralCommandNode> literalCommandNodeLocalRef) {
        LiteralCommandNode literalCommandNode = literalCommandNodeLocalRef.get();
        if (this.inertiaMessage != null) {
            ((LiteralCommandNodeInterface) literalCommandNode).setMessage(this.inertiaMessage);
        }
    }

    @Override
    public void setMessage(Message message) {
        this.inertiaMessage = message;
    }
}
