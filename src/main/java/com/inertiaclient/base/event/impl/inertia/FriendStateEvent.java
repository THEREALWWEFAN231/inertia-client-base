package com.inertiaclient.base.event.impl.inertia;

import com.inertiaclient.base.event.Event;
import com.inertiaclient.base.utils.Friend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FriendStateEvent extends Event {

    private Friend friend;
    /**
     * index in the friends list where this friend was added to or removed from
     */
    private int listIndex;
    /**
     * true if they were added to the friends list
     * false if they were removed from the friends list
     */
    private boolean wasAdded;

}