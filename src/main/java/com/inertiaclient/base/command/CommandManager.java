package com.inertiaclient.base.command;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.impl.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;

public class CommandManager {

    @Getter
    private String prefix = ".";
    @Getter
    private ChatFormatting bracketColor = ChatFormatting.GRAY;
    @Getter
    private ChatFormatting nameColor = ChatFormatting.RED;
    @Getter
    private ChatFormatting messageColor = ChatFormatting.WHITE;

    @Getter
    private CommandDispatcher<SharedSuggestionProvider> dispatcher = new CommandDispatcher();
    @Getter
    private SharedSuggestionProvider commandSource = new ClientSuggestionProvider(null, InertiaBase.mc);
    @Getter
    private ArrayList<Command> commands = new ArrayList<>();//honestly its easier to keep our own list of Command, rather then using CommandDispatcher root children to do some weird lookup

    public CommandManager() {
        this.registerCommand(new HelpCommand());
        this.registerCommand(new ToggleCommand());
        this.registerCommand(new ValueCommand());
        this.registerCommand(new FriendCommand());

        InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
            ArrayList<Command> modsCommands = new ArrayList<>();
            inertiaMod.initializeCommands(modsCommands, this);

            modsCommands.forEach(this::registerCommand);
        });
    }

    private void registerCommand(Command command) {
        this.commands.add(command);
        LiteralArgumentBuilder<SharedSuggestionProvider> builder = command.literal(command.getName(), command.getDescription());
        command.buildArguments(builder);
        LiteralCommandNode<SharedSuggestionProvider> mainCommand = this.dispatcher.register(builder);

        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                //i dont know why this doesn't work for no argument commands, like help(it works with arguments, like toggle command)... so we will just build another command
                //this.dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal(alias).redirect(mainCommand));

                LiteralArgumentBuilder<SharedSuggestionProvider> aliasBuilder = LiteralArgumentBuilder.literal(alias);
                command.buildArguments(aliasBuilder);
                this.dispatcher.register(aliasBuilder);
            }
        }
    }

    public void runCommand(String command) {
        try {
            this.dispatcher.execute(command.substring(prefix.length()), this.commandSource);
        } catch (CommandSyntaxException e) {
            InertiaBase.sendChatMessage(e.getMessage());
        }
    }

}
