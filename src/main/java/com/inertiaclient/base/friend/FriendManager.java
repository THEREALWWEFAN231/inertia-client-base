package com.inertiaclient.base.friend;

import lombok.Getter;

import java.util.HashSet;

public class FriendManager {

    @Getter
    private HashSet<String> friends = new HashSet<>();

    public void addFriend(String friendName) {
        this.friends.add(friendName.toLowerCase());
    }

    public void removeFriend(String playerName) {
        this.friends.remove(playerName.toLowerCase());
    }

    public boolean isFriend(String playerName) {
        return this.friends.contains(playerName);
    }

}
