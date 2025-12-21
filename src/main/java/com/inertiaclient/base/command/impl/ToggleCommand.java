package com.inertiaclient.base.command.impl;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.Command;
import com.inertiaclient.base.command.ModuleSelectorArgumentType;
import com.inertiaclient.base.command.ModuleStateArgumentType;
import com.inertiaclient.base.module.Module;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "t");
    }

    @Override
    public void buildArguments(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.then(this.literal("alloff", Component.translatable(this.baseKeyWithParameter("alloff_description"))).executes(this::alloff));

        var moduleBuilder = this.argument("module", new ModuleSelectorArgumentType()).executes(context -> this.setState(context, null));
        moduleBuilder.then(this.argument("state", new ModuleStateArgumentType()).executes(context -> this.setState(context, ModuleStateArgumentType.getState(context, "state"))));

        builder.then(moduleBuilder);
    }

    public int alloff(CommandContext<SharedSuggestionProvider> context) {

        int count = 0;
        for (Module module : InertiaBase.instance.getModuleManager().getModules()) {
            if (module.isEnabled()) {
                module.toggle();
                count++;
            }
        }

        InertiaBase.sendChatMessage(Component.translatable(this.baseKeyWithParameter("alloff")));

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public int setState(CommandContext<SharedSuggestionProvider> context, Boolean state) throws CommandSyntaxException {

        Module module = context.getArgument("module", Module.class);
        if (state == null) {//toggle
            module.toggle();
            InertiaBase.sendChatMessage(Component.translatable(this.baseKeyWithParameter("toggled"), module.getId()));
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }
        if (module.isEnabled() == state) {
            InertiaBase.sendChatMessage(Component.translatable(this.baseKeyWithParameter("already_" + (state ? "enabled" : "disabled")), module.getId()));
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }
        module.setState(state);
        InertiaBase.sendChatMessage(Component.translatable(this.baseKeyWithParameter(state ? "enabled" : "disabled"), module.getId()));

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

}
