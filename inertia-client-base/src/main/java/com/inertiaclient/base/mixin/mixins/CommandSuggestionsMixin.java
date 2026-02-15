package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {

    @Shadow
    @Final
    private Screen screen;
    @Shadow
    @Final
    EditBox input;
    @Shadow
    private ParseResults<SharedSuggestionProvider> currentParse;
    @Shadow
    private CommandSuggestions.SuggestionsList suggestions;
    @Shadow
    boolean keepSuggestions;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;


    @Shadow
    private void updateUsageInfo() {

    }

    @Inject(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true)
    public void refresh(CallbackInfo callbackInfo, @Local LocalRef<StringReader> stringReaderRef) {
        if (!(screen instanceof ChatScreen)) {//dont allow in command blocks or what ever
            return;
        }
        char prefix = '.';
        StringReader stringReader = stringReaderRef.get();

        boolean isInertiaCommand = stringReader.canRead() && stringReader.peek() == prefix;

        if (isInertiaCommand) {//cancel what minecraft normally does and use our command dispatcher
            stringReader.skip();

            int cursor = this.input.getCursorPosition();
            CommandDispatcher<SharedSuggestionProvider> commandDispatcher = InertiaBase.instance.getCommandManager().getDispatcher();
            if (this.currentParse == null) {
                this.currentParse = commandDispatcher.parse(stringReader, InertiaBase.instance.getCommandManager().getCommandSource());
            }

            if (!(cursor < 1 || this.suggestions != null && this.keepSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.currentParse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.updateUsageInfo();
                    }
                });
            }

            callbackInfo.cancel();
        } else {
            //reset it so normal commands can process correctly
            stringReader.setCursor(0);
        }
    }
}