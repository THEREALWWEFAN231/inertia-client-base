package com.inertiaclient.base.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class ModuleStateArgumentType implements ArgumentType<String> {

    private static final DynamicCommandExceptionType INVALID_STATE = new DynamicCommandExceptionType(o -> Component.translatable("icb.command.invalid_state", o));

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String requestedState = reader.readString();
        Boolean state = ModuleStateArgumentType.getType(requestedState);
        if (state == null) {
            throw INVALID_STATE.create(requestedState);
        }
        return requestedState;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(new String[]{"true", "false", "on", "off"}, builder);
    }

    public static Boolean getType(String state) {
        if (state.equals("true") || state.equals("on")) {
            return true;
        }
        if (state.equals("false") || state.equals("off")) {
            return false;
        }
        return null;
    }

    public static Boolean getState(CommandContext<?> context, String name) {
        return ModuleStateArgumentType.getType(context.getArgument(name, String.class));
    }

}