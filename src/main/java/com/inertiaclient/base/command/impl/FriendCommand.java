package com.inertiaclient.base.command.impl;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.command.Command;
import com.inertiaclient.base.utils.Friend;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AllArgsConstructor;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend");
    }

    @Override
    public void buildArguments(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {

        var addBuilder = this.literal("add").then(this.argument("player_name", new FriendArgumentType(true)).executes(this::add));
        builder.then(addBuilder);

        var removeBuilder = this.literal("remove").then(this.argument("player_name", new FriendArgumentType(false)).executes(this::remove));
        builder.then(removeBuilder);

        builder.then(this.literal("list").executes(this::list));

    }

    public int add(CommandContext<SharedSuggestionProvider> context) {
        String playerName = context.getArgument("player_name", String.class);

        var friendManager = InertiaBase.instance.getFriendManager();
        if (friendManager.isFriend(playerName)) {
            InertiaBase.sendChatMessage(Component.translatable("icb.command.friend.already_friend", playerName));
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }

        var onlineFoundPlayer = InertiaBase.mc.player.connection.getListedOnlinePlayers().stream().filter(playerInfo -> playerInfo.getProfile().getName().equalsIgnoreCase(playerName)).findFirst();

        if (onlineFoundPlayer.isPresent()) {
            var player = onlineFoundPlayer.get();
            friendManager.addFriend(new Friend(player.getProfile().getName(), player.getProfile().getId()));
        } else {
            friendManager.addFriend(new Friend(playerName, null));
        }

        InertiaBase.sendChatMessage(Component.translatable("icb.command.friend.added_friend", playerName));
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public int remove(CommandContext<SharedSuggestionProvider> context) {
        String playerName = context.getArgument("player_name", String.class);

        var friendManager = InertiaBase.instance.getFriendManager();
        if (!friendManager.isFriend(playerName)) {
            InertiaBase.sendChatMessage(Component.translatable("icb.command.friend.not_friend", playerName));
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }

        friendManager.removeFriend(playerName);
        InertiaBase.sendChatMessage(Component.translatable("icb.command.friend.removed_friend", playerName));
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public int list(CommandContext<SharedSuggestionProvider> context) {

        if (InertiaBase.instance.getFriendManager().getFriends().isEmpty()) {
            InertiaBase.sendChatMessage(Component.translatable("icb.command.friend.no_friends"));
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }

        InertiaBase.sendChatMessage(Component.translatable("icb.command.friend.list"));

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    @AllArgsConstructor
    private static class FriendArgumentType implements ArgumentType<String> {

        private boolean isAdd;

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            return reader.readString();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            if (isAdd) {
                /*var stream = InertiaBase.mc.player.connection.getListedOnlinePlayers().stream().filter(playerInfo -> !InertiaBase.instance.getFriendManager().isFriend(playerInfo.getProfile().getName())).sorted((o1, o2) -> {
                    if (InertiaBase.mc.level.players().stream().anyMatch(abstractClientPlayer -> abstractClientPlayer.getGameProfile().getName().equalsIgnoreCase(o1.getProfile().getName())))
                        return -1;
                    if (InertiaBase.mc.level.players().stream().anyMatch(abstractClientPlayer -> abstractClientPlayer.getGameProfile().getName().equalsIgnoreCase(o2.getProfile().getName())))
                        return 1;

                    return 0;
                });

                return SharedSuggestionProvider.suggest(stream.map(playerInfo -> playerInfo.getProfile().getName()), builder);*/
                var stream = InertiaBase.mc.player.connection.getListedOnlinePlayers().stream().filter(playerInfo -> !InertiaBase.instance.getFriendManager().isFriend(playerInfo.getProfile().getName()));
                SharedSuggestionProvider.suggest(stream.map(playerInfo -> playerInfo.getProfile().getName()), builder);
            }

            return SharedSuggestionProvider.suggest(InertiaBase.instance.getFriendManager().getFriends().stream().map(Friend::getUsername), builder);
        }
    }

}
