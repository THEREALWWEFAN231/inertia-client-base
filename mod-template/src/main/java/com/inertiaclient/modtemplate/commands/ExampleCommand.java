package com.inertiaclient.modtemplate.commands;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.SharedSuggestionProvider;

public class ExampleCommand extends Command {

    public ExampleCommand() {
        super("example");
    }

    @Override
    public void buildArguments(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.executes(this::example);
    }

    public int example(CommandContext<SharedSuggestionProvider> context) {
        InertiaBase.sendChatMessage("Hello example");
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }


}
