package com.inertiaclient.modtemplate.commands;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ExampleCommand extends Command {

    public ExampleCommand() {
        super("example");
    }

    @Override
    public void buildArguments(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(this::example);
    }

    public int example(CommandContext<ClientSuggestionProvider> context) {
        InertiaBase.sendChatMessage("Hello example");
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }


}
