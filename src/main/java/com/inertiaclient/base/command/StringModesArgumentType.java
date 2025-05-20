package com.inertiaclient.base.command;

import com.inertiaclient.base.value.impl.ModeValue;
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

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class StringModesArgumentType implements ArgumentType<ModeValue.Mode> {

    private static final DynamicCommandExceptionType STRING_MODE_NOT_FOUND = new DynamicCommandExceptionType(o -> Text.translatable("icb.command.string_mode_not_found", o));
    private ModeValue stringValue;

    @Override
    public ModeValue.Mode parse(StringReader reader) throws CommandSyntaxException {
        String modeId = reader.readString();
        ModeValue.Mode value = stringValue.getModeFromString(modeId);
        if (value == null) {
            throw STRING_MODE_NOT_FOUND.create(modeId);
        }
        return value;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(stringValue.getModes(), builder, stringIdentifier -> stringIdentifier.getId(), stringIdentifier -> stringIdentifier.getDescription());
    }

}
