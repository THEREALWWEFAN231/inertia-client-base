package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.Friend;

public class AddedFriendComponent extends GenericFriendComponent {

    public AddedFriendComponent(Friend friend) {
        super(friend.getUsername(), friend.getUuid());
    }

    @Override
    public YogaNode createHeadDisplay(YogaNode headAndName) {
        CachedHeadComponent headComponent = new CachedHeadComponent(this.uuid == null ? this.username : this.uuid.toString());
        headComponent.setBlurRadius(() -> headAndName.shouldShowHoveredEffects() ? 3f : 0f);
        return headComponent;
    }
}
