package com.inertiaclient.base.command.impl;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.Command;
import com.inertiaclient.base.command.HashsetTypeArgumentType;
import com.inertiaclient.base.command.StringModesArgumentType;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.value.HashsetValue;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;
import com.inertiaclient.base.value.impl.FloatValue;
import com.inertiaclient.base.value.impl.ModeValue;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ValueCommand extends Command {

    public ValueCommand() {
        super("value");
    }

    @Override
    public void buildArguments(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {

        /*var moduleBuilder = this.argument("module", new ModuleSelectorArgumentType());
        moduleBuilder.then(this.argument("valuegroup", new ValueGroupArgumentType()));

        builder.then(moduleBuilder);*/


        for (Module module : InertiaBase.instance.getModuleManager().getModules()) {

            var moduleBuilder = this.literal(module.getId(), module.getDescription());

            for (ValueGroup valueGroup : module.getValueGroups()) {
                var valueGroupBuilder = this.literal(valueGroup.getId());

                for (Value value : valueGroup.getValues()) {
                    var valueBuilder = this.literal(value.getId(), value.getDescription());


                    if (value instanceof FloatValue fv) {
                        var afterTypedValueBuilder = this.argument("value", FloatArgumentType.floatArg());
                        afterTypedValueBuilder.executes(context -> ValueCommand.executeFloat(context, fv, context.getArgument("value", Float.class)));

                        valueBuilder.then(afterTypedValueBuilder);
                    } else if (value instanceof ModeValue sv) {
                        var afterTypedValueBuilder = this.argument("value", new StringModesArgumentType(sv));
                        afterTypedValueBuilder.executes(context -> ValueCommand.executeString(context, sv, context.getArgument("value", ModeValue.Mode.class)));

                        valueBuilder.then(afterTypedValueBuilder);
                    } else if (value instanceof HashsetValue<?> hsv) {

                        var addBuilder = valueBuilder.then(this.literal("add").then(this.argument("idkj", new HashsetTypeArgumentType((HashsetValue<Object>) hsv, true))));
                        var removeBuilder = valueBuilder.then(this.literal("remove"));
                        var listBuilder = valueBuilder.then(this.literal("list").executes(context -> {
                            if (hsv.getValue().isEmpty()) {
                                InertiaBase.sendChatMessage("Nothing is in the " + value.getId() + " list");
                                return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                            }
                            hsv.getValue().forEach(something -> {
                                if (something instanceof Block block) {
                                    InertiaBase.sendChatMessage(BuiltInRegistries.BLOCK.getKey(block));
                                } else if (something instanceof BlockEntityType blockEntity) {
                                    InertiaBase.sendChatMessage(BlockEntityType.getKey(blockEntity));
                                } else if (something instanceof EntityType entityType) {
                                    InertiaBase.sendChatMessage(EntityType.getKey(entityType));
                                }
                            });
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        }));
                        var setBuilder = valueBuilder.then(this.literal("set"));


                    } else {

                        var afterTypedValueBuilder = this.argument("value", StringArgumentType.greedyString());
                        afterTypedValueBuilder.executes(context -> ValueCommand.execute(context, value));

                        valueBuilder.then(afterTypedValueBuilder);
                    }

                    valueGroupBuilder.then(valueBuilder);
                }
                moduleBuilder.then(valueGroupBuilder);
            }

            builder.then(moduleBuilder);
        }
    }

    private static int executeFloat(CommandContext<SharedSuggestionProvider> context, FloatValue floatValue, float value) throws CommandSyntaxException {
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private static int executeString(CommandContext<SharedSuggestionProvider> context, ModeValue valueObject, ModeValue.Mode value) throws CommandSyntaxException {
        valueObject.setValue(value);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private static int execute(CommandContext<SharedSuggestionProvider> context, Value value) throws CommandSyntaxException {


        System.out.println(context.getArgument("value", String.class));

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }


}
