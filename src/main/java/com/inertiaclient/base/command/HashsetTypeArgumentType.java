package com.inertiaclient.base.command;

import com.inertiaclient.base.value.HashsetValue;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AllArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class HashsetTypeArgumentType implements ArgumentType<Object> {

    private static final DynamicCommandExceptionType STRING_MODE_NOT_FOUND = new DynamicCommandExceptionType(o -> Text.translatable("icb.command.string_mode_not_found", o));
    private HashsetValue<Object> hashsetValue;
    private boolean add;

    @Override
    public Object parse(StringReader reader) throws CommandSyntaxException {
        Identifier identifier = Identifier.fromCommandInput(reader);

        Object type = hashsetValue.getEntryFromKey(identifier.toString());
        System.out.println(type);
        if (type == null) {
            throw STRING_MODE_NOT_FOUND.create("idkk");
        }

        if (add && hashsetValue.getValue().contains(type)) {
            throw STRING_MODE_NOT_FOUND.create("i1");
        }

        return type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (add) {
            return CommandSource.suggestMatching(hashsetValue.getAllPossibleEntries().stream().filter(type -> hashsetValue.getValue().contains(type) ? false : true).map(type -> hashsetValue.getKeyFromEntry(type)).toList(), builder);
        }
        return CommandSource.suggestMatching(hashsetValue.getAllPossibleEntries().stream().filter(type -> hashsetValue.getValue().contains(type) ? add ? false : true : true).map(type -> hashsetValue.getKeyFromEntry(type)).toList(), builder);
    }

}
