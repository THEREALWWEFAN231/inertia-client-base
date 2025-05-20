package com.inertiaclient.base.command;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.impl.HelpCommand;
import com.inertiaclient.base.command.impl.ToggleCommand;
import com.inertiaclient.base.command.impl.ValueCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class CommandManager {

    @Getter
    private String prefix = ".";
    @Getter
    private Formatting bracketColor = Formatting.GRAY;
    @Getter
    private Formatting nameColor = Formatting.RED;
    @Getter
    private Formatting messageColor = Formatting.WHITE;

    @Getter
    private CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher();
    @Getter
    private CommandSource commandSource = new ClientCommandSource(null, InertiaBase.mc);
    @Getter
    private ArrayList<Command> commands = new ArrayList<>();//honestly its easier to keep our own list of Command, rather then using CommandDispatcher root children to do some weird lookup

    public CommandManager() {
        this.registerCommand(new HelpCommand());
        this.registerCommand(new ToggleCommand());
        this.registerCommand(new ValueCommand());

        InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
            ArrayList<Command> modsCommands = new ArrayList<>();
            inertiaMod.initializeCommands(modsCommands, this);

            modsCommands.forEach(this::registerCommand);
        });
    }

    private void registerCommand(Command command) {
        this.commands.add(command);
        LiteralArgumentBuilder<CommandSource> builder = command.literal(command.getName(), command.getDescription());
        command.buildArguments(builder);
        LiteralCommandNode<CommandSource> mainCommand = this.dispatcher.register(builder);

        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                //i dont know why this doesn't work for no argument commands, like help(it works with arguments, like toggle command)... so we will just build another command
                //this.dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal(alias).redirect(mainCommand));

                LiteralArgumentBuilder<CommandSource> aliasBuilder = LiteralArgumentBuilder.literal(alias);
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
