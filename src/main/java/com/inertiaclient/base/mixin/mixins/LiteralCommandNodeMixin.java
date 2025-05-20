package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.mixin.custominterfaces.LiteralCommandNodeInterface;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(LiteralCommandNode.class)
public class LiteralCommandNodeMixin<S> implements LiteralCommandNodeInterface {

    @Shadow(remap = false)
    @Final
    private String literal;
    @Shadow(remap = false)
    @Final
    private String literalLowerCase;
    @Unique
    private Message inertiaMessage;


    @Inject(method = "listSuggestions", at = @At("HEAD"), remap = false, cancellable = true)
    public void listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> callbackInfoReturnable) {
        if (this.inertiaMessage != null) {
            if (literalLowerCase.startsWith(builder.getRemainingLowerCase())) {
                callbackInfoReturnable.setReturnValue(builder.suggest(literal, this.inertiaMessage).buildFuture());
            }
        }
    }

    @Override
    public void setMessage(Message message) {
        this.inertiaMessage = message;
    }
}
