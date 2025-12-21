package com.inertiaclient.base.command;

import com.inertiaclient.base.mixin.custominterfaces.LiteralCommandNodeInterface;
import com.inertiaclient.base.utils.LanguageBaseKey;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Getter;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class Command implements LanguageBaseKey {

    @Getter
    private String name;
    @Nullable
    @Getter
    private String[] aliases;
    @Getter
    private Component description;


    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.description = Component.translatableWithFallback(this.baseKeyWithParameter("description"), "no description");
    }

    public Command(String name) {
        this(name, (String[]) null);
    }

    public abstract void buildArguments(LiteralArgumentBuilder<SharedSuggestionProvider> builder);

    //a helper(so we don't have to add the generics every time we call RequiredArgumentBuilder.argument) like CommandManager.argument, with a generic CommandSource,
    public <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public LiteralArgumentBuilder<SharedSuggestionProvider> literal(String name) {
        return LiteralArgumentBuilder.<SharedSuggestionProvider>literal(name);
    }

    public LiteralArgumentBuilder<SharedSuggestionProvider> literal(String name, Message message) {
        var literalBuilder = LiteralArgumentBuilder.<SharedSuggestionProvider>literal(name);
        ((LiteralCommandNodeInterface) literalBuilder).setMessage(message);

        return literalBuilder;
    }

    @Override
    public String getLanguageBaseKey() {
        return "icb.command." + this.name;
    }

    public String getDescriptionString() {
        return this.description.getString();
    }

}