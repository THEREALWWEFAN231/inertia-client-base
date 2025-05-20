package com.inertiaclient.base.command;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.module.Module;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class ModuleSelectorArgumentType implements ArgumentType<Module> {

    private static final DynamicCommandExceptionType MODULE_NOT_FOUND = new DynamicCommandExceptionType(o -> Text.translatable("icb.command.module_not_found", o));

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        String moduleName = reader.readString();
        Module module = InertiaBase.instance.getModuleManager().getModuleById(moduleName);
        if (module == null) {
            throw MODULE_NOT_FOUND.create(moduleName);
        }
        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(InertiaBase.instance.getModuleManager().getModules(), builder, module -> module.getId(), module -> new LiteralMessage(module.getDescriptionString()));
        //return CommandSource.suggestMatching(InertiaClient.instance.getModuleManager().getModules().stream().map(module -> module.getId()).toList(), builder);
    }

}
