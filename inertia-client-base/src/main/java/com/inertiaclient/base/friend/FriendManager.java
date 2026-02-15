package com.inertiaclient.base.friend;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.inertia.FriendStateEvent;
import com.inertiaclient.base.utils.Friend;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;

public class FriendManager {

    @Getter
    private ArrayList<Friend> friends = new ArrayList<>();
    private HashSet<String> friendLookup = new HashSet<>();

    public void addFriend(Friend friend, boolean save) {
        boolean isNewFriend = this.friendLookup.add(friend.getUsername().toLowerCase());
        if (isNewFriend) {
            int addedIndex = this.friends.size();
            this.friends.add(friend);
            EventManager.fire(new FriendStateEvent(friend, addedIndex, true));
            if (save) {
                InertiaBase.instance.getFileManager().saveFriendsJson();
            }
        }
    }

    public void addFriend(Friend friend) {
        this.addFriend(friend, true);
    }

    public void removeFriend(String playerName, boolean save) {
        boolean removedFromLookup = this.friendLookup.remove(playerName.toLowerCase());//was found and removed
        if (removedFromLookup) {
            var iterator = this.friends.listIterator();
            while (iterator.hasNext()) {
                int index = iterator.nextIndex();
                Friend friend = iterator.next();
                if (friend.getUsername().equalsIgnoreCase(playerName)) {
                    iterator.remove();
                    EventManager.fire(new FriendStateEvent(friend, index, false));
                }
            }

            if (save) {
                InertiaBase.instance.getFileManager().saveFriendsJson();
            }
        }
    }

    public void removeFriend(String playerName) {
        this.removeFriend(playerName, true);
    }

    public boolean isFriend(String playerName) {
        return this.friendLookup.contains(playerName.toLowerCase());
    }

    public void removeAllFriends() {
        this.friends.clear();
        this.friendLookup.clear();
    }

}
