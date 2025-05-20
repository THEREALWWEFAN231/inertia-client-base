package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {

    @Shadow
    @Final
    private Screen owner;
    @Shadow
    @Final
    TextFieldWidget textField;
    @Shadow
    private ParseResults<CommandSource> parse;
    @Shadow
    private ChatInputSuggestor.SuggestionWindow window;
    @Shadow
    boolean completingSuggestions;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;


    @Shadow
    private void showCommandSuggestions() {

    }

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true)
    public void refresh(CallbackInfo callbackInfo, @Local LocalRef<StringReader> stringReaderRef) {
        if (!(owner instanceof ChatScreen)) {//dont allow in command blocks or what ever
            return;
        }
        char prefix = '.';
        StringReader stringReader = stringReaderRef.get();

        boolean isInertiaCommand = stringReader.canRead() && stringReader.peek() == prefix;

        if (isInertiaCommand) {//cancel what minecraft normally does and use our command dispatcher
            stringReader.skip();

            int cursor = this.textField.getCursor();
            CommandDispatcher<CommandSource> commandDispatcher = InertiaBase.instance.getCommandManager().getDispatcher();
            if (this.parse == null) {
                this.parse = commandDispatcher.parse(stringReader, InertiaBase.instance.getCommandManager().getCommandSource());
            }

            if (!(cursor < 1 || this.window != null && this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.showCommandSuggestions();
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