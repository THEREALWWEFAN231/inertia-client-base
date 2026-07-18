package com.inertiaclient.base.command.impl;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void buildArguments(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(this::onHelp);
    }

    public int onHelp(CommandContext<ClientSuggestionProvider> context) {
        for (Command command : InertiaBase.instance.getCommandManager().getCommands()) {
            if (command != this) {
                InertiaBase.sendChatMessage(command.getName() + " " + InertiaBase.instance.getCommandManager().getBracketColor() + "-" + InertiaBase.instance.getCommandManager().getMessageColor() + " " + command.getDescription().getString());
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }


}
